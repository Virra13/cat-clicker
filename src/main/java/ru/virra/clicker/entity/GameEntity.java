package ru.virra.clicker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Long currentLines;
    private Long totalLines;
    private Long money;
    private int linesPerClick;
    private int linesPerSecond;

    @Enumerated(EnumType.STRING)
    private Stage stage;

}
