package com.bitqba.ledger;

import com.bitqba.ArcanumStorageMod;
import net.minecraft.util.Identifier;

/**
 * A verifiable statement about what a player has seen or done.
 *
 * <p>Facts are the source of truth of the ledger. Automatic facts are backed by
 * hidden advancements; event facts are granted by the mod. {@code adv:} facts are
 * only used while calibrating an existing world and always reference a vanilla
 * advancement.</p>
 */
public record LedgerFact(String id) {
    public static final String ITEM = "item";
    public static final String CRAFT = "craft";
    public static final String PLACE = "place";
    public static final String EVENT = "event";
    public static final String ADV = "adv";

    public LedgerFact {
        id = normalize(id);
        if (!isValid(id)) {
            throw new IllegalArgumentException("Invalid ledger fact: " + id);
        }
    }

    public static LedgerFact of(String raw) {
        return new LedgerFact(raw);
    }

    public String type() {
        return id.substring(0, id.indexOf(':'));
    }

    public String target() {
        return id.substring(id.indexOf(':') + 1);
    }

    public boolean isEvent() {
        return EVENT.equals(type());
    }

    public boolean isAdvancement() {
        return ADV.equals(type());
    }

    /**
     * Hidden advancement that stores this fact. {@code adv:} facts are read from
     * vanilla and do not have a ledger advancement.
     */
    public Identifier advancementId() {
        if (isAdvancement()) {
            throw new IllegalStateException("adv: facts are not stored as ledger advancements");
        }
        return Identifier.of(ArcanumStorageMod.MOD_ID, "ledger/" + id.replace(':', '/'));
    }

    /**
     * Translation key used by the client for facts that do not map to an item.
     */
    public String translationKey() {
        return "gui.arcanum_storage.ledger.fact." + target().replace(':', '.').replace('/', '.');
    }

    /**
     * Best-effort item id for display: item, craft and place facts point at an
     * item; event facts do not.
     */
    public Identifier displayItem() {
        if (ITEM.equals(type()) || CRAFT.equals(type()) || PLACE.equals(type())) {
            return Identifier.tryParse(target());
        }
        return null;
    }

    public static boolean isValid(String raw) {
        if (raw == null) return false;
        int split = raw.indexOf(':');
        if (split <= 0 || split == raw.length() - 1) return false;
        String type = raw.substring(0, split);
        if (!ITEM.equals(type) && !CRAFT.equals(type) && !PLACE.equals(type)
                && !EVENT.equals(type) && !ADV.equals(type)) {
            return false;
        }
        String target = raw.substring(split + 1);
        for (int i = 0; i < target.length(); i++) {
            char c = target.charAt(i);
            if (Character.isWhitespace(c) || c == '\\' || c == '"') return false;
        }
        // Item and event targets are identifiers; craft targets carry the recipe
        // namespace too, so both form a valid identifier after normalizing the
        // second colon.
        String normalized = target;
        int second = target.indexOf(':');
        if (second >= 0) {
            normalized = target.substring(0, second) + "/" + target.substring(second + 1);
        }
        return Identifier.tryParse(normalized) != null;
    }

    public static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
