package ru.virra.clicker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.virra.clicker.model.KeyboardTier;

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

    private int linesPerSecond = 0;

    @Enumerated(EnumType.STRING)
    private KeyboardTier keyboard = KeyboardTier.BASIC;

    private int monitorCount = 0;

    @Enumerated(EnumType.STRING)
    private Stage stage = Stage.BEGINNER;

    @Transient
    public int getLinesPerClick() {
        return keyboard.getLinesPerClick() + monitorCount * 5;
    }

}
