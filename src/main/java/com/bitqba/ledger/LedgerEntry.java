package com.bitqba.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A manual page with up to three progressive states. */
public class LedgerEntry {
    public String id = "";
    public int order;
    public String icon = "";
    public List<Stage> stages = new ArrayList<>();
    public List<List<String>> recipe = new ArrayList<>();

    public static class Stage {
        public String state = "unknown";
        public List<String> requires = new ArrayList<>();
    }

    public String titleKey() {
        return "gui.arcanum_storage.ledger." + id + ".title";
    }

    public String bodyKey(LedgerState state) {
        return "gui.arcanum_storage.ledger." + id + "." + state.id();
    }

    public LedgerState state(Set<String> facts) {
        LedgerState best = LedgerState.UNKNOWN;
        for (Stage stage : stages) {
            LedgerState candidate = parse(stage.state);
            if (candidate == null || candidate.ordinal() <= best.ordinal()) continue;
            if (!requirementsMet(stage.requires, facts)) continue;
            best = candidate;
        }
        return best;
    }

    /**
     * Requirements of the next state. An entry that is already complete has no
     * pending references.
     */
    public List<String> missing(Set<String> facts) {
        LedgerState current = state(facts);
        LedgerState target = null;
        for (Stage stage : stages) {
            LedgerState candidate = parse(stage.state);
            if (candidate == null || candidate.ordinal() <= current.ordinal()) continue;
            if (target == null || candidate.ordinal() < target.ordinal()) {
                target = candidate;
            }
        }
        if (target == null) return List.of();

        for (Stage stage : stages) {
            if (parse(stage.state) != target) continue;
            List<String> missing = new ArrayList<>();
            for (String requirement : stage.requires) {
                if (!facts.contains(requirement)) missing.add(requirement);
            }
            return List.copyOf(missing);
        }
        return List.of();
    }

    public List<String> allRequirements() {
        List<String> result = new ArrayList<>();
        for (Stage stage : stages) {
            for (String requirement : stage.requires) {
                if (!result.contains(requirement)) result.add(requirement);
            }
        }
        return result;
    }

    /** Validates the file. Returns a human-readable error, or {@code null}. */
    public String validate() {
        if (id == null || id.isBlank()) return "entry without id";
        if (icon == null || !icon.contains(":")) return id + ": invalid icon";
        if (stages.isEmpty()) return id + ": no stages";

        LedgerState last = null;
        boolean complete = false;
        for (Stage stage : stages) {
            LedgerState state = parse(stage.state);
            if (state == null) return id + ": invalid state " + stage.state;
            if (last != null && state.ordinal() <= last.ordinal()) {
                return id + ": stages must be ordered and unique";
            }
            if (state == LedgerState.UNKNOWN && !stage.requires.isEmpty()) {
                return id + ": unknown stage must not require facts";
            }
            for (String requirement : stage.requires) {
                if (!LedgerFact.isValid(requirement)) {
                    return id + ": invalid fact " + requirement;
                }
            }
            complete |= state == LedgerState.COMPLETE;
            last = state;
        }
        if (!complete) return id + ": missing complete stage";
        for (List<String> row : recipe) {
            for (String item : row) {
                if (item == null || item.isBlank() || !item.contains(":")) {
                    return id + ": invalid recipe item " + item;
                }
            }
        }
        return null;
    }

    private static boolean requirementsMet(List<String> requirements, Set<String> facts) {
        for (String requirement : requirements) {
            if (!facts.contains(requirement)) return false;
        }
        return true;
    }

    private static LedgerState parse(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase(java.util.Locale.ROOT)) {
            case "unknown" -> LedgerState.UNKNOWN;
            case "partial" -> LedgerState.PARTIAL;
            case "complete" -> LedgerState.COMPLETE;
            default -> null;
        };
    }
}
