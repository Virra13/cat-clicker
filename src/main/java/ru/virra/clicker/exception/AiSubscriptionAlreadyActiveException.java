package ru.virra.clicker.exception;

public class AiSubscriptionAlreadyActiveException extends RuntimeException {
    public AiSubscriptionAlreadyActiveException(String message) {
        super(message);
    }
}
