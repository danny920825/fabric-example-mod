package com.bitqba.ledger;

import com.bitqba.ArcanumStorageMod;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Immutable snapshot of the ledger content, loaded from a datapack.
 *
 * <p>Only the server needs this data. The client receives the resolved states
 * and reads the visible text from the normal language files.</p>
 */
public final class LedgerContent {
    private static final Gson GSON = new Gson();
    private static final Identifier MANUAL = Identifier.of(ArcanumStorageMod.MOD_ID, "ledger/manual.json");
    private static final Identifier ANALS = Identifier.of(ArcanumStorageMod.MOD_ID, "ledger/anales.json");
    private static final Identifier ACHIEVEMENTS =
            Identifier.of(ArcanumStorageMod.MOD_ID, "ledger/achievements.json");

    private static volatile LedgerContent instance = new LedgerContent(List.of(), List.of(), List.of());

    private final List<LedgerChapter> chapters;
    private final List<LedgerAnal> anals;
    private final List<LedgerAchievement> achievements;
    private final List<LedgerEntry> entries;
    private final Map<String, LedgerEntry> entriesById;
    private final Set<String> requiredFacts;

    private LedgerContent(List<LedgerChapter> chapters, List<LedgerAnal> anals,
            List<LedgerAchievement> achievements) {
        this.chapters = List.copyOf(chapters);
        this.anals = List.copyOf(anals);
        this.achievements = List.copyOf(achievements);

        List<LedgerEntry> flattened = new ArrayList<>();
        Map<String, LedgerEntry> byId = new LinkedHashMap<>();
        Set<String> facts = new LinkedHashSet<>();
        for (LedgerChapter chapter : this.chapters) {
            for (LedgerEntry entry : chapter.entries) {
                if (byId.putIfAbsent(entry.id, entry) != null) {
                    ArcanumStorageMod.LOGGER.warn("Duplicate ledger entry {}", entry.id);
                    continue;
                }
                flattened.add(entry);
                facts.addAll(entry.allRequirements());
            }
        }
        for (LedgerAnal anal : this.anals) {
            facts.addAll(anal.requires);
        }
        this.entries = List.copyOf(flattened);
        this.entriesById = Map.copyOf(byId);
        this.requiredFacts = Set.copyOf(facts);
    }

    public static LedgerContent get() {
        return instance;
    }

    public List<LedgerChapter> chapters() {
        return chapters;
    }

    public List<LedgerAnal> anals() {
        return anals;
    }

    public List<LedgerAchievement> achievements() {
        return achievements;
    }

    public List<LedgerEntry> entries() {
        return entries;
    }

    public LedgerEntry entry(String id) {
        return entriesById.get(id);
    }

    public Set<String> requiredFacts() {
        return requiredFacts;
    }

    public boolean complete(Set<String> facts) {
        if (entries.isEmpty()) return false;
        for (LedgerEntry entry : entries) {
            if (entry.state(facts) != LedgerState.COMPLETE) return false;
        }
        return true;
    }

    public int completed(Set<String> facts) {
        int count = 0;
        for (LedgerEntry entry : entries) {
            if (entry.state(facts) == LedgerState.COMPLETE) count++;
        }
        return count;
    }

    private static LedgerContent load(ResourceManager manager) {
        List<LedgerChapter> chapters = loadChapters(manager);
        List<LedgerAnal> anals = loadAnals(manager);
        List<LedgerAchievement> achievements = loadAchievements(manager);
        return new LedgerContent(chapters, anals, achievements);
    }

    private static List<LedgerChapter> loadChapters(ResourceManager manager) {
        ManualFile file = read(manager, MANUAL, ManualFile.class);
        if (file == null || file.chapters == null) return List.of();

        List<LedgerChapter> valid = new ArrayList<>();
        Set<String> ids = new LinkedHashSet<>();
        for (LedgerChapter chapter : file.chapters) {
            String error = chapter.validate();
            if (error != null) {
                ArcanumStorageMod.LOGGER.error("Invalid ledger chapter: {}", error);
                continue;
            }
            boolean duplicate = false;
            for (LedgerEntry entry : chapter.entries) {
                if (!ids.add(entry.id)) {
                    ArcanumStorageMod.LOGGER.error("Duplicate ledger entry id {}", entry.id);
                    duplicate = true;
                }
            }
            if (duplicate) continue;
            chapter.entries.sort(Comparator.comparingInt(entry -> entry.order));
            valid.add(chapter);
        }
        valid.sort(Comparator.comparingInt(chapter -> chapter.order));
        return valid;
    }

    private static List<LedgerAnal> loadAnals(ResourceManager manager) {
        AnalFile file = read(manager, ANALS, AnalFile.class);
        if (file == null || file.anals == null) return List.of();

        List<LedgerAnal> valid = new ArrayList<>();
        Set<String> ids = new LinkedHashSet<>();
        for (LedgerAnal anal : file.anals) {
            String error = anal.validate();
            if (error != null) {
                ArcanumStorageMod.LOGGER.error("Invalid anal document: {}", error);
                continue;
            }
            if (!ids.add(anal.id)) {
                ArcanumStorageMod.LOGGER.error("Duplicate anal id {}", anal.id);
                continue;
            }
            valid.add(anal);
        }
        valid.sort(Comparator.comparingInt(anal -> anal.order));
        return valid;
    }

    private static List<LedgerAchievement> loadAchievements(ResourceManager manager) {
        AchievementFile file = read(manager, ACHIEVEMENTS, AchievementFile.class);
        if (file == null || file.achievements == null) return List.of();

        List<LedgerAchievement> valid = new ArrayList<>();
        for (LedgerAchievement achievement : file.achievements) {
            String error = achievement.validate();
            if (error != null) {
                ArcanumStorageMod.LOGGER.error("Invalid ledger achievement: {}", error);
                continue;
            }
            valid.add(achievement);
        }
        return valid;
    }

    private static <T> T read(ResourceManager manager, Identifier id, Class<T> type) {
        Resource resource = manager.getResource(id).orElse(null);
        if (resource == null) {
            ArcanumStorageMod.LOGGER.error("Missing ledger data {}", id);
            return null;
        }
        try (BufferedReader reader = resource.getReader()) {
            return GSON.fromJson(reader, type);
        } catch (IOException | JsonParseException exception) {
            ArcanumStorageMod.LOGGER.error("Could not read ledger data {}", id, exception);
            return null;
        }
    }

    /** Creates the content snapshot. Visible for the reloader. */
    public static LedgerContent loadForReload(ResourceManager manager) {
        return load(manager);
    }

    public static void set(LedgerContent content) {
        instance = content;
    }

    private static final class ManualFile {
        private List<LedgerChapter> chapters;
    }

    private static final class AnalFile {
        private List<LedgerAnal> anals;
    }

    private static final class AchievementFile {
        private List<LedgerAchievement> achievements;
    }
}
