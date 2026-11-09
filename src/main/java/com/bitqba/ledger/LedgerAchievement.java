package com.bitqba.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A visible achievement and the facts that celebrate it. */
public class LedgerAchievement {
    public String id = "";
    public List<String> requires = new ArrayList<>();
    public boolean completeManual;

    public boolean earned(Set<String> facts, boolean manualComplete) {
        if (completeManual && !manualComplete) return false;
        for (String requirement : requires) {
            if (!facts.contains(requirement)) return false;
        }
        return true;
    }

    public String validate() {
        if (id == null || !id.contains(":")) return "achievement without id";
        for (String requirement : requires) {
            if (!LedgerFact.isValid(requirement)) return id + ": invalid fact " + requirement;
        }
        return null;
    }
}
