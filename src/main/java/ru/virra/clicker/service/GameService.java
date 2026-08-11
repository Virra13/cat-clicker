package ru.virra.clicker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.exception.GameNotFoundException;
import ru.virra.clicker.mapper.GameMapper;
import ru.virra.clicker.repository.GameRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameResponse create() {
        return gameMapper.gameEntityToDto(createNewGame());
    }

    public GameResponse findById(UUID id) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        return gameMapper.gameEntityToDto(entity);
    }

    public GameResponse click(UUID id) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        int linesPerClick = entity.getLinesPerClick();

        entity.setCurrentLines(entity.getCurrentLines() + linesPerClick);
        entity.setTotalLines(entity.getTotalLines() + linesPerClick);

        gameRepository.save(entity);
        return gameMapper.gameEntityToDto(entity);
    }

    private GameEntity createNewGame() {
        GameEntity newGame = new GameEntity();
        newGame.setCurrentLines(0L);
        newGame.setTotalLines(0L);
        newGame.setMoney(0L);
        newGame.setLinesPerClick(1);
        newGame.setLinesPerSecond(0);
        newGame.setStage(Stage.BEGINNER);
        gameRepository.save(newGame);
        return newGame;
    }
}