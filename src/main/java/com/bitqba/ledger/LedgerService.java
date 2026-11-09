package com.bitqba.ledger;

import com.bitqba.ArcanumStorageMod;
import com.bitqba.enchantment.ModEnchantments;
import com.bitqba.network.LedgerNoteS2CPayload;
import com.bitqba.network.LedgerOpenS2CPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side knowledge store for the ledger.
 *
 * <p>There is no custom save format: every fact is a hidden vanilla advancement.
 * The service only translates between fact names, advancement progress, item
 * scans and the content files.</p>
 */
public final class LedgerService {
    private static final Map<UUID, Integer> LAST_CHANGE = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, LedgerState>> LAST_STATES = new ConcurrentHashMap<>();
    private static final Map<UUID, LinkedHashSet<String>> PENDING_NOTES = new ConcurrentHashMap<>();
    private static final Set<String> WARNED_FACTS = ConcurrentHashMap.newKeySet();
    private static int ticks;

    private LedgerService() {}

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                server.execute(() -> onJoin(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                forget(handler.player.getUuid()));
        ServerTickEvents.END_SERVER_TICK.register(LedgerService::tick);
    }

    private static void onJoin(ServerPlayerEntity player) {
        LAST_CHANGE.put(player.getUuid(), player.getInventory().getChangeCount());
        unlockFieldLedger(player);
        scanInventory(player, false);
        seed(player);
        syncEntries(player, false);
        syncAchievements(player);
    }

    /**
     * The field ledger recipe is always available. Vanilla only copies a recipe
     * into the recipe book when its unlock advancement fires, which does not
     * happen retroactively in already-explored worlds, so unlock it explicitly.
     */
    private static void unlockFieldLedger(ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return;
        Identifier id = Identifier.of(ArcanumStorageMod.MOD_ID, "field_ledger");
        server.getRecipeManager()
                .get(RegistryKey.of(RegistryKeys.RECIPE, id))
                .ifPresent(recipe -> player.unlockRecipes(List.of(recipe)));
    }

    private static void forget(UUID uuid) {
        LAST_CHANGE.remove(uuid);
        LAST_STATES.remove(uuid);
        PENDING_NOTES.remove(uuid);
    }

    private static void tick(MinecraftServer server) {
        ticks++;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            flushNotes(player);
        }
        if (ticks % 20 != 0) return;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            int change = player.getInventory().getChangeCount();
            Integer previous = LAST_CHANGE.put(player.getUuid(), change);
            if (!Objects.equals(previous, change) || ticks % 100 == 0) {
                scanInventory(player, true);
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /* Facts                                                              */
    /* ------------------------------------------------------------------ */

    public static boolean has(ServerPlayerEntity player, String factId) {
        if (!LedgerFact.isValid(factId)) return false;
        LedgerFact fact = LedgerFact.of(factId);
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return false;

        if (fact.isAdvancement()) {
            Identifier vanilla = Identifier.tryParse(fact.target());
            if (vanilla == null) return false;
            AdvancementEntry entry = server.getAdvancementLoader().get(vanilla);
            return entry != null && player.getAdvancementTracker().getProgress(entry).isDone();
        }

        AdvancementEntry entry = server.getAdvancementLoader().get(fact.advancementId());
        return entry != null && player.getAdvancementTracker().getProgress(entry).isDone();
    }

    /**
     * Grants a fact and refreshes the affected pages. Returns whether the fact was
     * new. Event facts and automatic facts use the same path.
     */
    public static boolean grant(ServerPlayerEntity player, String factId) {
        if (!grantInternal(player, factId)) return false;
        syncEntries(player, true);
        syncAchievements(player);
        return true;
    }

    private static boolean grantInternal(ServerPlayerEntity player, String factId) {
        if (!LedgerFact.isValid(factId)) return false;
        LedgerFact fact = LedgerFact.of(factId);
        if (fact.isAdvancement()) return false;

        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return false;
        AdvancementEntry entry = server.getAdvancementLoader().get(fact.advancementId());
        if (entry == null) {
            if (WARNED_FACTS.add(fact.id())) {
                ArcanumStorageMod.LOGGER.warn("Ledger fact {} has no advancement", fact.id());
            }
            return false;
        }
        if (player.getAdvancementTracker().getProgress(entry).isDone()) return false;
        return player.getAdvancementTracker().grantCriterion(entry, "done");
    }

    private static int scanInventory(ServerPlayerEntity player, boolean notify) {
        int changed = 0;
        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            changed += scanStack(player, player.getInventory().getStack(slot));
        }
        var enderChest = player.getEnderChestInventory();
        for (int slot = 0; slot < enderChest.size(); slot++) {
            changed += scanStack(player, enderChest.getStack(slot));
        }
        if (changed > 0) {
            syncEntries(player, notify);
            syncAchievements(player);
        }
        return changed;
    }

    private static int scanStack(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int changed = 0;
        for (String fact : LedgerFacts.itemFacts(stack)) {
            if (tracked(fact) && grantInternal(player, fact)) changed++;
        }
        if (ModEnchantments.hasArcanumBinding(stack)) {
            if (grantInternal(player, LedgerFacts.event("binding_obtained"))) changed++;
            if (ModEnchantments.isBoundPouch(stack)
                    && grantInternal(player, LedgerFacts.event("binding_applied"))) {
                changed++;
            }
        }
        return changed;
    }

    /**
     * First-contact calibration. It only grants things the world can prove:
     * possession, crafting statistics and a short list of vanilla milestones.
     */
    public static void seed(ServerPlayerEntity player) {
        if (has(player, LedgerFacts.SEEDED)) return;

        int recovered = scanInventory(player, false);
        recovered += seedStats(player);
        recovered += seedVanilla(player);
        grantInternal(player, LedgerFacts.SEEDED);

        syncEntries(player, false);
        syncAchievements(player);

        if (recovered > 0) {
            player.sendMessage(Text.translatable("message.arcanum_storage.ledger.calibrated", recovered)
                    .formatted(Formatting.GRAY), false);
        }
    }

    private static int seedStats(ServerPlayerEntity player) {
        int changed = 0;
        var stats = player.getStatHandler();
        for (Item item : Registries.ITEM) {
            boolean known = stats.getStat(Stats.PICKED_UP, item) > 0
                    || stats.getStat(Stats.CRAFTED, item) > 0
                    || stats.getStat(Stats.USED, item) > 0;
            if (!known) continue;

            for (String fact : LedgerFacts.itemFacts(new ItemStack(item))) {
                if (tracked(fact) && grantInternal(player, fact)) changed++;
            }
            if (stats.getStat(Stats.CRAFTED, item) > 0) {
                String craft = LedgerFacts.craftForOutput(item);
                if (craft != null && tracked(craft) && grantInternal(player, craft)) changed++;
            }
        }
        return changed;
    }

    private static int seedVanilla(ServerPlayerEntity player) {
        int changed = 0;
        for (String milestone : LedgerFacts.VANILLA_MILESTONES) {
            String fact = LedgerFacts.advancement(milestone);
            if (has(player, fact)) changed++;
        }
        return changed;
    }

    /* ------------------------------------------------------------------ */
    /* States, notes and achievements                                     */
    /* ------------------------------------------------------------------ */

    private static Set<String> grantedFacts(ServerPlayerEntity player, Iterable<String> candidates) {
        Set<String> facts = new LinkedHashSet<>();
        for (String candidate : candidates) {
            if (has(player, candidate)) facts.add(candidate);
        }
        return facts;
    }

    private static boolean tracked(String fact) {
        return LedgerContent.get().requiredFacts().contains(fact);
    }

    private static Set<String> relevantFacts() {
        Set<String> facts = new LinkedHashSet<>(LedgerContent.get().requiredFacts());
        for (LedgerAchievement achievement : LedgerContent.get().achievements()) {
            facts.addAll(achievement.requires);
        }
        facts.addAll(LedgerFacts.eventFacts());
        facts.add(LedgerFacts.SEEDED);
        facts.add(LedgerFacts.MANUAL_COMPLETE);
        return facts;
    }

    private static void syncEntries(ServerPlayerEntity player, boolean notify) {
        LedgerContent content = LedgerContent.get();
        Set<String> facts = grantedFacts(player, relevantFacts());
        Map<String, LedgerState> previous = LAST_STATES.computeIfAbsent(player.getUuid(),
                ignored -> new LinkedHashMap<>());
        Map<String, LedgerState> current = new LinkedHashMap<>();
        boolean hadBaseline = !previous.isEmpty();

        for (LedgerEntry entry : content.entries()) {
            LedgerState state = entry.state(facts);
            current.put(entry.id, state);
            LedgerState old = previous.getOrDefault(entry.id, LedgerState.UNKNOWN);
            if (notify && hadBaseline && state.ordinal() > old.ordinal()) {
                PENDING_NOTES.computeIfAbsent(player.getUuid(), ignored -> new LinkedHashSet<>())
                        .add(entry.id);
            }
        }
        LAST_STATES.put(player.getUuid(), current);

        if (content.complete(facts)) {
            grantInternal(player, LedgerFacts.MANUAL_COMPLETE);
        }
    }

    public static void syncAchievements(ServerPlayerEntity player) {
        LedgerContent content = LedgerContent.get();
        Set<String> facts = grantedFacts(player, relevantFacts());
        boolean manualComplete = content.complete(facts);

        MinecraftServer server = player.getEntityWorld().getServer();
        if (server == null) return;

        for (LedgerAchievement achievement : content.achievements()) {
            if (!achievement.earned(facts, manualComplete)) continue;
            Identifier id = Identifier.tryParse(achievement.id);
            if (id == null) continue;
            AdvancementEntry entry = server.getAdvancementLoader().get(id);
            if (entry == null) continue;
            if (!player.getAdvancementTracker().getProgress(entry).isDone()) {
                player.getAdvancementTracker().grantCriterion(entry, "done");
            }
        }
    }

    private static void flushNotes(ServerPlayerEntity player) {
        LinkedHashSet<String> notes = PENDING_NOTES.remove(player.getUuid());
        if (notes == null || notes.isEmpty()) return;
        ServerPlayNetworking.send(player, new LedgerNoteS2CPayload(List.copyOf(notes)));
    }

    /* ------------------------------------------------------------------ */
    /* View and commands                                                  */
    /* ------------------------------------------------------------------ */

    public static void open(ServerPlayerEntity player) {
        unlockFieldLedger(player);
        seed(player);
        scanInventory(player, false);
        syncEntries(player, false);
        syncAchievements(player);

        LedgerContent content = LedgerContent.get();
        Set<String> facts = grantedFacts(player, relevantFacts());
        boolean complete = content.complete(facts);

        LedgerView view = new LedgerView();
        view.playerName = player.getName().getString();
        view.seeded = has(player, LedgerFacts.SEEDED);
        view.complete = complete;
        view.total = content.entries().size();
        view.completed = content.completed(facts);

        for (LedgerChapter chapter : content.chapters()) {
            LedgerView.Chapter chapterView = new LedgerView.Chapter();
            chapterView.id = chapter.id;
            chapterView.titleKey = chapter.titleKey();
            for (LedgerEntry entry : chapter.entries) {
                LedgerView.Entry entryView = new LedgerView.Entry();
                entryView.id = entry.id;
                entryView.titleKey = entry.titleKey();
                entryView.icon = entry.icon;
                entryView.state = entry.state(facts).id();
                entryView.missing = entry.missing(facts);
                entryView.recipe = entry.recipe;
                chapterView.entries.add(entryView);
            }
            view.chapters.add(chapterView);
        }

        for (LedgerAnal anal : content.anals()) {
            LedgerView.Anal analView = new LedgerView.Anal();
            analView.id = anal.id;
            analView.titleKey = anal.titleKey();
            analView.bodyKey = anal.bodyKey();
            analView.unlocked = anal.unlocked(facts, complete);
            analView.missing = anal.missing(facts, complete);
            view.anals.add(analView);
        }

        ServerPlayNetworking.send(player, new LedgerOpenS2CPayload(view.toJson()));
    }

    public static List<String> grantedFacts(ServerPlayerEntity player) {
        List<String> granted = new ArrayList<>();
        for (String fact : LedgerFacts.all()) {
            if (has(player, fact)) granted.add(fact);
        }
        return granted;
    }

    public static List<String> pendingFacts(ServerPlayerEntity player) {
        List<String> pending = new ArrayList<>();
        for (String fact : LedgerFacts.all()) {
            if (!has(player, fact)) pending.add(fact);
        }
        return pending;
    }

    public static int completed(ServerPlayerEntity player) {
        LedgerContent content = LedgerContent.get();
        Set<String> facts = grantedFacts(player, relevantFacts());
        return content.completed(facts);
    }

    public static boolean complete(ServerPlayerEntity player) {
        Set<String> facts = grantedFacts(player, relevantFacts());
        return LedgerContent.get().complete(facts);
    }

    public static void reset(ServerPlayerEntity player) {
        MinecraftServer server = player.getEntityWorld().getServer();
        if (server != null) {
            for (AdvancementEntry entry : server.getAdvancementLoader().getAdvancements()) {
                Identifier id = entry.id();
                if (!ArcanumStorageMod.MOD_ID.equals(id.getNamespace())) continue;
                String path = id.getPath();
                if (!path.startsWith("ledger/") && !path.startsWith("main/")) continue;
                player.getAdvancementTracker().revokeCriterion(entry, "done");
            }
        }
        forget(player.getUuid());
        LAST_CHANGE.put(player.getUuid(), player.getInventory().getChangeCount());
        seed(player);
        syncEntries(player, false);
        syncAchievements(player);
    }

    /* ------------------------------------------------------------------ */
    /* Event hooks                                                        */
    /* ------------------------------------------------------------------ */

    public static void onDisplayOpened(ServerPlayerEntity player, int connections,
            boolean depthLimited, boolean truncated) {
        if (connections <= 0) {
            grant(player, LedgerFacts.event("no_chests"));
        } else {
            grant(player, LedgerFacts.event("first_chest"));
            if (connections >= 20) grant(player, LedgerFacts.event("twenty_chests"));
        }
        if (depthLimited) grant(player, LedgerFacts.event("depth_warning"));
        if (truncated) grant(player, LedgerFacts.event("listing_warning"));
    }

    public static void onSearched(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("searched"));
    }

    public static void onExtracted(ServerPlayerEntity player, ItemStack stack, boolean nested) {
        grant(player, LedgerFacts.event("extracted_item"));
        if (nested || stack.getItem() instanceof net.minecraft.item.BlockItem blockItem
                && blockItem.getBlock() instanceof net.minecraft.block.ShulkerBoxBlock) {
            grant(player, LedgerFacts.event("extracted_shulker"));
        }
    }

    public static void onInventoryFull(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("inventory_full"));
    }

    public static void onConnectionError(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("connection_error"));
    }

    public static void onPressOpened(ServerPlayerEntity player, boolean multipleChests) {
        if (multipleChests) grant(player, LedgerFacts.event("press_multiple"));
    }

    public static void onPressAnalyzed(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("press_analyzed"));
    }

    public static void onPressOrganized(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("press_organized"));
    }

    public static void onEndPouchUsed(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("end_pouch_used"));
    }

    public static void onCacheCreated(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("cache_created"));
    }

    public static void onCacheRecovered(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("cache_recovered"));
    }

    public static void onCacheEmptied(ServerPlayerEntity player) {
        grant(player, LedgerFacts.event("cache_recovered"));
        grant(player, LedgerFacts.event("cache_emptied"));
    }

    /** Used by the death cache and the command output. */
    public static boolean isSeeded(ServerPlayerEntity player) {
        return has(player, LedgerFacts.SEEDED);
    }

    /** Convenience for the item: it never opens on the client thread. */
    public static void openFromItem(ServerPlayerEntity player) {
        open(player);
    }
}
