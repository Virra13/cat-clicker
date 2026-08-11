package ru.virra.clicker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.exception.*;
import ru.virra.clicker.mapper.GameMapper;
import ru.virra.clicker.model.AiTier;
import ru.virra.clicker.model.KeyboardTier;
import ru.virra.clicker.model.UpgradeType;
import ru.virra.clicker.repository.GameRepository;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UpgradeService {

    private final ProgressionService progressionService;
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    private static final int MAX_MONITORS = 6;
    private static final long MONITOR_PRICE = 300;
    private static final long AI_SECONDS = 900;

    @Transactional
    public GameResponse buyUpgrade(UUID id, UpgradeType type) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        progressionService.updateGameState(entity);

        switch (type) {
            case KEYBOARD -> buyKeyboard(entity);
            case MONITOR -> buyMonitor(entity);
        }

        gameRepository.save(entity);
        return gameMapper.gameEntityToDto(entity);
    }

    @Transactional
    public GameResponse buyAi(UUID id, AiTier aiTier) {

        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        progressionService.updateGameState(entity);

        Instant expiresAt = entity.getAiSubscriptionExpiresAt();

        if (expiresAt != null && Instant.now().isBefore(expiresAt)) {
            throw new AiSubscriptionAlreadyActiveException("AI subscription is already active");
        }

        if (aiTier == AiTier.NONE || aiTier == AiTier.FREE_AI) {
            throw new IllegalArgumentException("Invalid subscription tier");
        }

        if (!entity.isFreeAiUnlocked()) {
            throw new ContentLockedException("Free AI must be unlocked first");
        }

        if (!entity.getStage().allows(aiTier.getRequiredStage())) {
            throw new ContentLockedException("AI is not available at current stage");
        }

        if (entity.getMoney() < aiTier.getPrice()) {
            throw new NotEnoughMoneyException("Not enough money");
        }

        entity.setMoney(entity.getMoney() - aiTier.getPrice());
        entity.setAi(aiTier);
        entity.setAiSubscriptionExpiresAt(Instant.now().plusSeconds(AI_SECONDS));
        gameRepository.save(entity);

        return gameMapper.gameEntityToDto(entity);
    }

    @Transactional
    public GameResponse buyFreeAi(UUID id) {

        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id, "Game not found by id"));

        progressionService.updateGameState(entity);

        if (entity.isFreeAiUnlocked()) {
            throw new MaxUpgradeLevelException("Free AI already unlocked");
        }

        AiTier freeAi = AiTier.FREE_AI;

        if (entity.getMoney() < freeAi.getPrice()) {
            throw new NotEnoughMoneyException("Not enough money");
        }

        entity.setMoney(entity.getMoney() - freeAi.getPrice());
        entity.setFreeAiUnlocked(true);
        entity.setAi(AiTier.FREE_AI);
        gameRepository.save(entity);

        return gameMapper.gameEntityToDto(entity);
    }

    public void buyKeyboard(GameEntity entity) {
        KeyboardTier nextKeyboard = entity.getKeyboard().next();

        if (nextKeyboard == null) {
            throw new MaxUpgradeLevelException("Keyboard is already max level");
        }

        if (!entity.getStage().allows(nextKeyboard.getRequiredStage())) {
            throw new ContentLockedException("Keyboard is not available at current stage");
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