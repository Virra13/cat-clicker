package ru.virra.clicker.model;

import lombok.Getter;
import ru.virra.clicker.entity.Stage;

@Getter
public enum AiTier {

    NONE(0, 0, Stage.BEGINNER),
    FREE_AI(300, 1, Stage.BEGINNER),
    AI_PRO(1500, 3, Stage.JUNIOR),
    VIBE_CODING(5000, 10, Stage.MIDDLE);

    private final long price;
    private final int linesPerSecond;
    private final Stage requiredStage;

    AiTier(long price, int linesPerSecond, Stage requiredStage) {
        this.price = price;
        this.linesPerSecond = linesPerSecond;
        this.requiredStage = requiredStage;
    }
}
