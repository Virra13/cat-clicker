package ru.virra.clicker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.virra.clicker.entity.Stage;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class GameResponse {

    private UUID id;
    private Long currentLines;
    private Long money;
    private Stage stage;
    private Long totalLines;
    private int linesPerClick;
    private int linesPerSecond;

}
