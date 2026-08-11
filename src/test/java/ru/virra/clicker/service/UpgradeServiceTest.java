package ru.virra.clicker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.exception.ContentLockedException;
import ru.virra.clicker.exception.GameNotFoundException;
import ru.virra.clicker.exception.NotEnoughMoneyException;
import ru.virra.clicker.mapper.GameMapper;
import ru.virra.clicker.model.AiTier;
import ru.virra.clicker.model.KeyboardTier;
import ru.virra.clicker.model.UpgradeType;
import ru.virra.clicker.repository.GameRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.virra.clicker.model.UpgradeType.KEYBOARD;

@ExtendWith(MockitoExtension.class)
class UpgradeServiceTest {


    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private ProgressionService progressionService;

    @InjectMocks
    private UpgradeService upgradeService;

    private UUID gameId;
    private GameEntity game;
    private GameResponse response;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();

        game = new GameEntity();
        game.setId(gameId);
        game.setCurrentLines(100L);
        game.setTotalLines(500L);
        game.setMoney(600L);
        game.setKeyboard(KeyboardTier.BASIC);
        game.setMonitorCount(0);
        game.setAi(AiTier.NONE);
        game.setStage(Stage.BEGINNER);

        response = new GameResponse();
        response.setId(gameId);
        response.setCurrentLines(game.getCurrentLines());
        response.setMoney(game.getMoney());
        response.setStage(game.getStage());
    }

    @Test
    void shouldJuniorBuyGamingKeyboard() {
        game.setStage(Stage.JUNIOR);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameMapper.gameEntityToDto(game)).thenReturn(response);

        upgradeService.buyUpgrade(gameId, KEYBOARD);
        assertEquals(KeyboardTier.MECHANICAL, game.getKeyboard());
        assertEquals(500L, game.getMoney());

        upgradeService.buyUpgrade(gameId, KEYBOARD);
        assertEquals(KeyboardTier.GAMING, game.getKeyboard());
        assertEquals(0L, game.getMoney());

        verify(gameRepository, times(2)).save(game);
    }

    @Test
    void shouldNotBeginnerBuyGamingKeyboard() {

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameMapper.gameEntityToDto(game)).thenReturn(response);

        upgradeService.buyUpgrade(gameId, KEYBOARD);
        assertEquals(KeyboardTier.MECHANICAL, game.getKeyboard());
        assertEquals(500L, game.getMoney());

        assertThrows(
                ContentLockedException.class,
                () ->  upgradeService.buyUpgrade(gameId, UpgradeType.KEYBOARD)
        );

        assertEquals(KeyboardTier.MECHANICAL, game.getKeyboard());
        assertEquals(500L, game.getMoney());

        verify(gameRepository, times(1)).save(game);
    }

    @Test
    void shouldNotBuyKeyboardWhenNotEnoughMoney() {
        game.setMoney(50L);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        assertThrows(
                NotEnoughMoneyException.class,
                () -> upgradeService.buyUpgrade(gameId,UpgradeType.KEYBOARD)
        );

        assertEquals(KeyboardTier.BASIC, game.getKeyboard());
        assertEquals(50L, game.getMoney());

        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldBeginnerBuyFreeAi() {
        game.setMoney(500L);

        upgradeService.buyUpgrade(gameId, UpgradeType.AI);

        assertEquals(AiTier.FREE_AI, game.getAi());
        assertEquals(200L, game.getMoney());
        assertEquals(1, game.getLinesPerSecond());

        verify(progressionService).applyPassiveIncome(game);
        verify(gameRepository).save(game);
    }

    @Test
    void shouldNotBeginnerBuyAiPro() {
        game.setAi(AiTier.FREE_AI);
        game.setMoney(5000L);
        game.setStage(Stage.BEGINNER);

        assertThrows(
                ContentLockedException.class,
                () -> upgradeService.buyUpgrade(gameId, UpgradeType.AI)
        );

        assertEquals(AiTier.FREE_AI, game.getAi());
        assertEquals(5000L, game.getMoney());

        verify(progressionService).applyPassiveIncome(game);
        verify(gameRepository, never()).save(any());
    }

}