package ru.virra.clicker.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID id, String message) {
        super(message);
    }
}
