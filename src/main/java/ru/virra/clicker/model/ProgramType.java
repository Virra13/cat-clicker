package ru.virra.clicker.model;

import lombok.Getter;
import ru.virra.clicker.entity.Stage;

@Getter
public enum ProgramType {

    HELLO_WORLD(100, 50, Stage.BEGINNER),
    TODO_APP(500, 300, Stage.JUNIOR),
    ONLINE_STORE(2000, 1500, Stage.MIDDLE),
    SOCIAL_NETWORK(10000, 10000, Stage.SENIOR);

    private final long requiredLines;
    private final long rewardMoney;
    private final Stage stage;

    ProgramType(long requiredLines, long rewardMoney, Stage stage) {
        this.requiredLines = requiredLines;
        this.rewardMoney = rewardMoney;
        this.stage = stage;
    }

}