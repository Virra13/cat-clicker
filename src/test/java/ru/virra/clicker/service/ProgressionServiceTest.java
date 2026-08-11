package ru.virra.clicker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.model.AiTier;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ProgressionServiceTest {

    private ProgressionService progressionService;
    private GameEntity game;

    @BeforeEach
    void setUp() {
        progressionService = new ProgressionService();

        game = new GameEntity();
        game.setCurrentLines(100L);
        game.setTotalLines(500L);
        game.setAi(AiTier.FREE_AI);
        game.setStage(Stage.BEGINNER);
    }

    @Test
    void shouldUpdateStageToJunior() {
        game.setTotalLines(Stage.JUNIOR.getRequiredTotalLines());
        progressionService.updateStage(game);
        assertEquals(Stage.JUNIOR, game.getStage());
    }

    @Test
    void shouldUpdateStageToHighestAvailableStage() {
        game.setTotalLines(Stage.SENIOR.getRequiredTotalLines());
        progressionService.updateStage(game);
        assertEquals(Stage.SENIOR, game.getStage());
    }

    @Test
    void shouldApplyPassiveIncomeForTenSeconds() {
        game.setPassiveIncomeCalculatedAt(Instant.now().minusSeconds(10));
        progressionService.applyPassiveIncome(game);

        assertEquals(110L, game.getCurrentLines());
        assertEquals(510L, game.getTotalLines());
    }

    @Test
    void shouldLimitPassiveIncomeToFifteenMinutes() {
        game.setPassiveIncomeCalculatedAt(Instant.now().minusSeconds(2000));

        progressionService.applyPassiveIncome(game);

        assertEquals(1000L, game.getCurrentLines());
        assertEquals(1400L, game.getTotalLines());
    }
}