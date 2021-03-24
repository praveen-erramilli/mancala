package com.praveen.mancala.payload;

import com.praveen.mancala.model.GameStatus;
import lombok.Data;

@Data
public class GameDto {
    private Long id;

    private BoardDto board;

    private PlayerDto playerZero;

    private PlayerDto playerOne;

    private GameStatus gameStatus;

    private PlayerDto currentPlayer;

    private PlayerDto winner;

    private boolean isTie;

    private PitDto lastInsertedPit;

}
