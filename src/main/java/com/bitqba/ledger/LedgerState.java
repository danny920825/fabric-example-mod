package com.bitqba.ledger;

import net.minecraft.util.Formatting;

/** The three practical states of a manual entry. */
public enum LedgerState {
    UNKNOWN("unknown", Formatting.GRAY),
    PARTIAL("partial", Formatting.GOLD),
    COMPLETE("complete", Formatting.GREEN);

    private final String id;
    private final Formatting formatting;

    LedgerState(String id, Formatting formatting) {
        this.id = id;
        this.formatting = formatting;
    }

    public String id() {
        return id;
    }

    public Formatting formatting() {
        return formatting;
    }

    public String translationKey() {
        return "gui.arcanum_storage.ledger.state." + id;
    }
}
