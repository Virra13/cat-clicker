package ru.virra.clicker.mapper;


import org.springframework.stereotype.Component;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.dto.ProgramResponse;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.model.ProgramType;

@Component
public class GameMapper {

    public GameResponse gameEntityToDto(GameEntity entity) {
        GameResponse gameResponse = new GameResponse();
        gameResponse.setId(entity.getId());
        gameResponse.setCurrentLines(entity.getCurrentLines());
        gameResponse.setTotalLines(entity.getTotalLines());
        gameResponse.setMoney(entity.getMoney());
        gameResponse.setLinesPerClick(entity.getLinesPerClick());
        gameResponse.setLinesPerSecond(entity.getLinesPerSecond());
        gameResponse.setStage(entity.getStage());
        gameResponse.setAi(entity.getAi());

        return gameResponse;
    }

    public ProgramResponse programToDto(ProgramType programType, GameEntity entity) {
        ProgramResponse response = new ProgramResponse();
        response.setType(programType);
        response.setRequiredLines(programType.getRequiredLines());
        response.setRewardMoney(programType.getRewardMoney());
        response.setRequiredStage(programType.getRequiredStage());
        response.setAvailable(entity.getStage().allows(programType.getRequiredStage()));

        return response;
    }



}
