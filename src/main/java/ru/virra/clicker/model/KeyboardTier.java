package ru.virra.clicker.model;

import lombok.Getter;
import ru.virra.clicker.entity.Stage;

@Getter
public enum KeyboardTier {
    BASIC(0, 1, Stage.BEGINNER),
    MECHANICAL(100, 3, Stage.BEGINNER),
    GAMING(500, 8, Stage.JUNIOR),
    PROFESSIONAL(2000, 20, Stage.MIDDLE),
    ABSOLUTE_NONSENSE(10000, 50, Stage.SENIOR);

    private final long price;
    private final int linesPerClick;
    private final Stage requiredStage;

    KeyboardTier(long price, int linesPerClick, Stage requiredStage) {
        this.price = price;
        this.linesPerClick = linesPerClick;
        this.requiredStage = requiredStage;
    }

    public KeyboardTier next() {
        KeyboardTier[] values = values();
        int nextOrdinal = ordinal() + 1;

        return nextOrdinal < values.length ? values[nextOrdinal] : null;
    }
}
