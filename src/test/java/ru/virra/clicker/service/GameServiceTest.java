package ru.virra.clicker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.exception.GameNotFoundException;
import ru.virra.clicker.exception.NotEnoughLinesException;
import ru.virra.clicker.mapper.GameMapper;
import ru.virra.clicker.model.KeyboardTier;
import ru.virra.clicker.model.ProgramType;
import ru.virra.clicker.repository.GameRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private GameService gameService;

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
        game.setMoney(200L);
        game.setKeyboard(KeyboardTier.BASIC);
        game.setMonitorCount(0);
        game.setLinesPerSecond(0);
        game.setStage(Stage.BEGINNER);

        response = new GameResponse();
        response.setId(gameId);
        response.setCurrentLines(game.getCurrentLines());
        response.setMoney(game.getMoney());
        response.setStage(game.getStage());
    }

    @Test
    void shouldCreateGame() {
        when(gameRepository.save(any(GameEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(gameMapper.gameEntityToDto(any(GameEntity.class)))
                .thenReturn(response);

        GameResponse result = gameService.create();
        assertSame(response, result);

        ArgumentCaptor<GameEntity> captor = ArgumentCaptor.forClass(GameEntity.class);

        verify(gameRepository).save(captor.capture());

        GameEntity savedGame = captor.getValue();

        assertEquals(0L, savedGame.getCurrentLines());
        assertEquals(0L, savedGame.getTotalLines());
        assertEquals(0L, savedGame.getMoney());
        assertEquals(KeyboardTier.BASIC, savedGame.getKeyboard());
        assertEquals(Stage.BEGINNER, savedGame.getStage());
        assertEquals(0, savedGame.getMonitorCount());

        verify(gameRepository).save(any(GameEntity.class));
        verify(gameMapper).gameEntityToDto(any(GameEntity.class));
    }

    @Test
    void shouldFindGameById() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        when(gameMapper.gameEntityToDto(game)).thenReturn(response);

        GameResponse result = gameService.findById(gameId);

        assertSame(response, result);
        verify(gameRepository).findById(gameId);
        verify(gameMapper).gameEntityToDto(game);
    }

    @Test
    void shouldThrowExceptionWhenGameNotFound() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(
                GameNotFoundException.class,
                () -> gameService.findById(gameId)
        );

        verify(gameRepository).findById(gameId);
        verifyNoInteractions(gameMapper);
    }

    @Test
    void shouldIncreaseLinesAfterClick() {
        game.setKeyboard(KeyboardTier.MECHANICAL);
        game.setMonitorCount(2);
        // +13 клик

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameMapper.gameEntityToDto(game)).thenReturn(response);

        gameService.click(gameId);

        assertEquals(113L, game.getCurrentLines());
        assertEquals(513L, game.getTotalLines());

        verify(gameRepository).save(game);
    }

    @Test
    void shouldThrowExceptionWhenClickGameNotFound() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(
                GameNotFoundException.class,
                () -> gameService.click(gameId)
        );

        verify(gameRepository, never()).save(any());
    }

    @Test
    void shouldSellProgram() {
        game.setCurrentLines(500L);
        game.setMoney(200L);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        when(gameMapper.gameEntityToDto(game)).thenReturn(response);

        gameService.sell(gameId, ProgramType.HELLO_WORLD);

        assertEquals(500L - ProgramType.HELLO_WORLD.getRequiredLines(), game.getCurrentLines());
        assertEquals(200L + ProgramType.HELLO_WORLD.getRewardMoney(), game.getMoney());
        verify(gameRepository).save(game);
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughLinesForProgram() {
        game.setCurrentLines(10L);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        assertThrows(
                NotEnoughLinesException.class,
                () -> gameService.sell(gameId, ProgramType.HELLO_WORLD)
        );

        assertEquals(10L, game.getCurrentLines());
        assertEquals(200L, game.getMoney());

        verify(gameRepository, never()).save(any());
    }

}