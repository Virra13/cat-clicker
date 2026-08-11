package ru.virra.clicker.model;

import lombok.Getter;

@Getter
public enum KeyboardTier {
    BASIC(0, 1),
    MECHANICAL(100, 3),
    GAMING(500, 8),
    PROFESSIONAL(2000, 20),
    ABSOLUTE_NONSENSE(10000, 50);

    private final long price;
    private final int linesPerClick;

    KeyboardTier(long price, int linesPerClick) {
        this.price = price;
        this.linesPerClick = linesPerClick;
    }

    public KeyboardTier next() {
        KeyboardTier[] values = values();
        int nextOrdinal = ordinal() + 1;

        if (nextOrdinal >= values.length) {
            return null;
        }

        return values[nextOrdinal];
    }
}
