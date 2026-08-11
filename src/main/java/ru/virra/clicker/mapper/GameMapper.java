package ru.virra.clicker.mapper;


import org.springframework.stereotype.Component;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.entity.GameEntity;

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

        return gameResponse;
    }
}
