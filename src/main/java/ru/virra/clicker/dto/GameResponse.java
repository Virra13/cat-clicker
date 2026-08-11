package ru.virra.clicker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.model.AiTier;
import ru.virra.clicker.model.KeyboardTier;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class GameResponse {

    private UUID id;
    private Long currentLines;
    private Long totalLines;
    private Long money;

    private int linesPerClick;
    private int linesPerSecond;

    private KeyboardTier keyboard;
    private int monitorCount;

    private AiTier ai;
    private Instant aiSubscriptionExpiresAt;

    private Stage stage;
}
