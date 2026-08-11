package ru.virra.clicker.exception;

public class ContentLockedException extends RuntimeException {
    public ContentLockedException(String message) {
        super(message);
    }
}
