package ru.virra.clicker.dto;


import lombok.Getter;
import lombok.Setter;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.model.ProgramType;

@Getter
@Setter
public class ProgramResponse {

    private ProgramType type;
    private long requiredLines;
    private long rewardMoney;
    private Stage requiredStage;
    private boolean available;

}
