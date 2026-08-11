package ru.virra.clicker.entity;

import lombok.Getter;

@Getter
public enum Stage {
    BEGINNER(0),
    JUNIOR(1000),
    MIDDLE(10000),
    SENIOR(50000),
    TECH_LEAD(200000),
    CTO(1000000);

    private final long requiredTotalLines;

    Stage(long requiredTotalLines) {
        this.requiredTotalLines = requiredTotalLines;
    }

    public boolean allows(Stage requiredStage) {
        return ordinal() >= requiredStage.ordinal();
    }
}
