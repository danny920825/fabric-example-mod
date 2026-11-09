package com.bitqba.ledger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * Small S2C document sent when the ledger opens. Visible text is not included:
 * the client resolves the keys against the normal language files.
 */
public final class LedgerView {
    private static final Gson GSON = new Gson();

    public int version = 1;
    public String playerName = "";
    public boolean seeded;
    public boolean complete;
    public int completed;
    public int total;
    public List<Chapter> chapters = new ArrayList<>();
    public List<Anal> anals = new ArrayList<>();

    public static final class Chapter {
        public String id = "";
        public String titleKey = "";
        public List<Entry> entries = new ArrayList<>();
    }

    public static final class Entry {
        public String id = "";
        public String titleKey = "";
        public String icon = "";
        public String state = "unknown";
        public List<String> missing = new ArrayList<>();
        public List<List<String>> recipe = new ArrayList<>();
    }

    public static final class Anal {
        public String id = "";
        public String titleKey = "";
        public String bodyKey = "";
        public boolean unlocked;
        public List<String> missing = new ArrayList<>();
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static LedgerView fromJson(String json) {
        try {
            LedgerView view = GSON.fromJson(json, LedgerView.class);
            return view == null ? new LedgerView() : view;
        } catch (JsonSyntaxException exception) {
            return new LedgerView();
        }
    }
}
