package ru.virra.clicker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.exception.GameNotFoundException;
import ru.virra.clicker.exception.MaxUpgradeLevelException;
import ru.virra.clicker.exception.NotEnoughMoneyException;
import ru.virra.clicker.mapper.GameMapper;
import ru.virra.clicker.model.KeyboardTier;
import ru.virra.clicker.model.UpgradeType;
import ru.virra.clicker.repository.GameRepository;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UpgradeService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    private static final int MAX_MONITORS = 6;
    private static final long MONITOR_PRICE = 300;

    @Transactional
    public GameResponse buyUpgrade(UUID id, UpgradeType type) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        switch (type) {
            case KEYBOARD -> buyKeyboard(entity);
            case MONITOR -> buyMonitor(entity);
        }

        gameRepository.save(entity);
        return gameMapper.gameEntityToDto(entity);
    }

    public void buyKeyboard(GameEntity entity) {
        KeyboardTier nextKeyboard = entity.getKeyboard().next();

        if (nextKeyboard == null) {
            throw new MaxUpgradeLevelException("Keyboard is already max level");
        }

        if (entity.getMoney() < nextKeyboard.getPrice()) {
            throw new NotEnoughMoneyException("Not enough money");
        }

        entity.setMoney(entity.getMoney() - nextKeyboard.getPrice());
        entity.setKeyboard(nextKeyboard);
    }

    private void buyMonitor(GameEntity entity) {
        if (entity.getMonitorCount() >= MAX_MONITORS) {
            throw new MaxUpgradeLevelException("Maximum monitor count reached");
        }

        if (entity.getMoney() < MONITOR_PRICE) {
            throw new NotEnoughMoneyException("Not enough money");
        }

        entity.setMoney(entity.getMoney() - MONITOR_PRICE);
        entity.setMonitorCount(entity.getMonitorCount() + 1);
    }
}
