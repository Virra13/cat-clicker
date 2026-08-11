package ru.virra.clicker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.virra.clicker.entity.GameEntity;
import ru.virra.clicker.entity.Stage;
import ru.virra.clicker.model.AiTier;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProgressionService {


    private static final long MAX_OFFLINE_SECONDS = 900;


    public void updateGameState(GameEntity entity) {
        applyPassiveIncome(entity);
        refreshAiSubscription(entity);
        updateStage(entity);
    }

    private void updateStage(GameEntity entity) {
        Stage currentStage = entity.getStage();

        for (Stage stage : Stage.values()) {
            if (entity.getTotalLines() >= stage.getRequiredTotalLines() && stage.ordinal() > currentStage.ordinal()) {
                entity.setStage(stage);
            }
        }
    }

    public void applyPassiveIncome(GameEntity entity) {
        Instant now = Instant.now();
        Instant lastCalculated = entity.getPassiveIncomeCalculatedAt();

        long elapsedSeconds = Duration.between(lastCalculated, now).getSeconds();

        if (elapsedSeconds > 0) {

            long effectiveSeconds = Math.min(elapsedSeconds, MAX_OFFLINE_SECONDS);
            Instant incomeUntil = lastCalculated.plusSeconds(effectiveSeconds);
            long earned = 0;

            Instant expiresAt = entity.getAiSubscriptionExpiresAt();

            if (expiresAt != null
                    && expiresAt.isAfter(lastCalculated)
                    && entity.getAi() != AiTier.FREE_AI
                    && entity.getAi() != AiTier.NONE) {

                Instant paidUntil = expiresAt.isBefore(incomeUntil) ? expiresAt : incomeUntil;

                long paidSeconds = Duration.between(lastCalculated, paidUntil).getSeconds();

                earned += paidSeconds * entity.getAi().getLinesPerSecond();

                long freeSeconds = Duration.between(paidUntil, incomeUntil).getSeconds();

                if (entity.isFreeAiUnlocked()) {
                    earned += freeSeconds * AiTier.FREE_AI.getLinesPerSecond();
                }

            } else {
                earned = effectiveSeconds * entity.getLinesPerSecond();
            }

            entity.setCurrentLines(entity.getCurrentLines() + earned);
            entity.setTotalLines(entity.getTotalLines() + earned);
            entity.setPassiveIncomeCalculatedAt(now);
        }
    }

    private void refreshAiSubscription(GameEntity entity) {
        Instant expiresAt = entity.getAiSubscriptionExpiresAt();
        if (expiresAt != null && !Instant.now().isBefore(expiresAt)) {
            entity.setAi(entity.isFreeAiUnlocked() ? AiTier.FREE_AI : AiTier.NONE);
            entity.setAiSubscriptionExpiresAt(null);
        }
    }
}