package ru.virra.clicker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.virra.clicker.model.AiTier;
import ru.virra.clicker.model.KeyboardTier;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "game")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long currentLines = 0L;
    private Long totalLines = 0L;
    private Long money = 0L;

    @Enumerated(EnumType.STRING)
    private KeyboardTier keyboard = KeyboardTier.BASIC;

    private int monitorCount = 0;

    private Instant passiveIncomeCalculatedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    private AiTier ai = AiTier.NONE;
    private boolean freeAiUnlocked = false;
    private Instant aiSubscriptionExpiresAt;

    @Enumerated(EnumType.STRING)
    private Stage stage = Stage.BEGINNER;

    @Transient
    public int getLinesPerClick() {
        return keyboard.getLinesPerClick() + monitorCount * 5;
    }

    @Transient
    public int getLinesPerSecond() {

        if (ai != null && ai != AiTier.NONE && ai != AiTier.FREE_AI
                && aiSubscriptionExpiresAt != null
                && Instant.now().isBefore(aiSubscriptionExpiresAt)) {

            return ai.getLinesPerSecond();
        }

        if (freeAiUnlocked) {
            return AiTier.FREE_AI.getLinesPerSecond();
        }

        return 0;
    }

}
