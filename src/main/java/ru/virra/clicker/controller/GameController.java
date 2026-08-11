package ru.virra.clicker.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.virra.clicker.dto.GameResponse;
import ru.virra.clicker.service.GameService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/games")
    public GameResponse createGame() {
        return gameService.create();
    }

    @GetMapping("/games/{id}")
    public GameResponse getGame(@PathVariable UUID id) {
        return gameService.findById(id);
    }

    @PostMapping("/games/{id}/click")
    public GameResponse click(@PathVariable UUID id) {
        return gameService.click(id);
    }
}