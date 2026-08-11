package ru.virra.clicker.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.model.AiTier;
import ru.virra.clicker.model.ProgramType;
import ru.virra.clicker.model.UpgradeType;
import ru.virra.clicker.service.GameService;
import ru.virra.clicker.service.UpgradeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final UpgradeService upgradeService;

    @PostMapping
    public GameResponse createGame() {
        return gameService.create();
    }

    @GetMapping("/{id}")
    public GameResponse getGame(@PathVariable UUID id) {
        return gameService.findById(id);
    }

    @PostMapping("/{id}/click")
    public GameResponse click(@PathVariable UUID id) {
        return gameService.click(id);
    }

    @PostMapping("/{id}/programs/{programType}/sell")
    public GameResponse sell(@PathVariable UUID id, @PathVariable ProgramType programType) {
        return gameService.sell(id, programType);
    }

    @PostMapping("/{id}/upgrades/{type}/buy")
    public GameResponse buyUpgrade(
            @PathVariable UUID id,
            @PathVariable UpgradeType type) {

        return upgradeService.buyUpgrade(id, type);
    }

    @PostMapping("/{id}/ai/{tier}/buy")
    public GameResponse buyAi(@PathVariable UUID id, @PathVariable AiTier tier) {
        if (tier == AiTier.FREE_AI) {
            return upgradeService.buyFreeAi(id);
        } else {
        return upgradeService.buyAi(id, tier);
        }
    }
}