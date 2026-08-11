package ru.virra.clicker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.exception.GameNotFoundException;
import ru.virra.clicker.exception.NotEnoughLinesException;
import ru.virra.clicker.mapper.GameMapper;
import ru.virra.clicker.model.KeyboardTier;
import ru.virra.clicker.model.ProgramType;
import ru.virra.clicker.repository.GameRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameResponse create() {
        GameEntity newGame = gameRepository.save(new GameEntity());
        gameRepository.save(newGame);

        return gameMapper.gameEntityToDto(newGame);
    }

    public GameResponse findById(UUID id) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        return gameMapper.gameEntityToDto(entity);
    }

    @Transactional
    public GameResponse click(UUID id) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        int linesPerClick = entity.getLinesPerClick();

        entity.setCurrentLines(entity.getCurrentLines() + linesPerClick);
        entity.setTotalLines(entity.getTotalLines() + linesPerClick);

        gameRepository.save(entity);
        return gameMapper.gameEntityToDto(entity);
    }

    @Transactional
    public GameResponse sell(UUID id, ProgramType programType) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        long requiredLines = programType.getRequiredLines();

        if (entity.getCurrentLines() < requiredLines) {
            throw new NotEnoughLinesException("Work better!");
        }

        entity.setCurrentLines(entity.getCurrentLines() - requiredLines);
        entity.setMoney(entity.getMoney() + programType.getRewardMoney());
        gameRepository.save(entity);

        return gameMapper.gameEntityToDto(entity);
    }
}