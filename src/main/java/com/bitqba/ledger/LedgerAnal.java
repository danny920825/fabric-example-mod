package com.bitqba.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A historical document. It never gates a practical page of the manual. */
public class LedgerAnal {
    public String id = "";
    public int order;
    public List<String> requires = new ArrayList<>();
    public boolean completeManual;

    public String titleKey() {
        return "gui.arcanum_storage.ledger.anal." + id + ".title";
    }

    public String bodyKey() {
        return "gui.arcanum_storage.ledger.anal." + id + ".body";
    }

    public boolean unlocked(Set<String> facts, boolean manualComplete) {
        if (completeManual && manualComplete) return true;
        for (String requirement : requires) {
            if (!facts.contains(requirement)) return false;
        }
        return true;
    }

    public List<String> missing(Set<String> facts, boolean manualComplete) {
        if (completeManual) {
            return manualComplete ? List.of() : List.of("event:manual_complete");
        }
        List<String> missing = new ArrayList<>();
        for (String requirement : requires) {
            if (!facts.contains(requirement)) missing.add(requirement);
        }
        return List.copyOf(missing);
    }

    public String validate() {
        if (id == null || id.isBlank()) return "anal without id";
        for (String requirement : requires) {
            if (!LedgerFact.isValid(requirement)) return id + ": invalid fact " + requirement;
        }
        return null;
    }
}
