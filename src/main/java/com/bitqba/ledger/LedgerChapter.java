package com.bitqba.ledger;

import java.util.ArrayList;
import java.util.List;

/** A group of manual entries. */
public class LedgerChapter {
    public String id = "";
    public int order;
    public List<LedgerEntry> entries = new ArrayList<>();

    public String titleKey() {
        return "gui.arcanum_storage.ledger.chapter." + id + ".title";
    }

    public String validate() {
        if (id == null || id.isBlank()) return "chapter without id";
        if (entries.isEmpty()) return id + ": chapter without entries";
        for (LedgerEntry entry : entries) {
            String error = entry.validate();
            if (error != null) return error;
        }
        return null;
    }
}
