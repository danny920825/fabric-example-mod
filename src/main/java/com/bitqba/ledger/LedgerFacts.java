package com.bitqba.ledger;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Canonical fact names used by the mod. Item facts are derived from the item
 * registry so new vanilla items do not need code changes; recipes, blocks and
 * events are the small, deliberate vocabulary of the ledger.
 */
public final class LedgerFacts {
    public static final String SEEDED = "event:seeded";
    public static final String MANUAL_COMPLETE = "event:manual_complete";

    /** Mod recipes whose output item id equals the recipe id. */
    public static final String[] MOD_RECIPES = {
            "field_ledger",
            "storage_connector",
            "storage_display",
            "ender_display",
            "archive_cable",
            "archive_connector",
            "end_pouch"
    };

    public static final String[] MOD_BLOCKS = {
            "storage_connector",
            "storage_display",
            "archive_cable",
            "archive_connector"
    };

    public static final String[] EVENTS = {
            "first_chest",
            "twenty_chests",
            "searched",
            "extracted_item",
            "extracted_shulker",
            "depth_warning",
            "listing_warning",
            "press_analyzed",
            "press_organized",
            "press_multiple",
            "end_pouch_used",
            "ender_display_used",
            "binding_obtained",
            "binding_applied",
            "cache_created",
            "cache_recovered",
            "cache_emptied",
            "no_chests",
            "connection_error",
            "inventory_full"
    };

    /** Vanilla milestones read during calibration. They never fake a mod action. */
    public static final String[] VANILLA_MILESTONES = {
            "minecraft:story/smelt_iron",
            "minecraft:story/obtain_armor",
            "minecraft:story/enchant_item",
            "minecraft:story/enter_the_end",
            "minecraft:story/mine_diamond",
            "minecraft:end/kill_dragon",
            "minecraft:nether/obtain_blaze_rod",
            "minecraft:adventure/trade"
    };

    private LedgerFacts() {}

    public static String item(Item item) {
        return LedgerFact.ITEM + ":" + Registries.ITEM.getId(item);
    }

    public static String craft(String recipePath) {
        return LedgerFact.CRAFT + ":arcanum_storage:" + recipePath;
    }

    public static String place(String blockPath) {
        return LedgerFact.PLACE + ":arcanum_storage:" + blockPath;
    }

    public static String event(String name) {
        return LedgerFact.EVENT + ":" + name;
    }

    public static String advancement(String vanillaAdvancement) {
        return LedgerFact.ADV + ":" + vanillaAdvancement;
    }

    /**
     * Canonical item facts for a stack. Any shulker box also teaches the generic
     * shulker material, so a page does not depend on the colour the player found.
     */
    public static List<String> itemFacts(ItemStack stack) {
        if (stack.isEmpty()) return List.of();
        Set<String> facts = new LinkedHashSet<>();
        facts.add(item(stack.getItem()));
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof ShulkerBoxBlock) {
                facts.add(item(Items.SHULKER_BOX));
            }
        }
        return List.copyOf(facts);
    }

    public static List<String> all() {
        Set<String> facts = new LinkedHashSet<>();
        for (Item item : Registries.ITEM) {
            facts.add(item(item));
        }
        for (String recipe : MOD_RECIPES) facts.add(craft(recipe));
        for (String block : MOD_BLOCKS) facts.add(place(block));
        for (String event : EVENTS) facts.add(event(event));
        facts.add(SEEDED);
        facts.add(MANUAL_COMPLETE);
        for (String milestone : VANILLA_MILESTONES) facts.add(advancement(milestone));
        return List.copyOf(facts);
    }

    public static String craftForOutput(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        if (!"arcanum_storage".equals(id.getNamespace())) return null;
        for (String recipe : MOD_RECIPES) {
            if (recipe.equals(id.getPath())) return craft(recipe);
        }
        return null;
    }

    public static List<String> eventFacts() {
        List<String> facts = new ArrayList<>();
        for (String event : EVENTS) facts.add(event(event));
        return facts;
    }
}
