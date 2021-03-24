package com.praveen.mancala.payload;

import lombok.Data;

@Data
public class PitDto {
    private Long id;

    private int coinsCount;

    private Long next;

    private PlayerDto owner;

    private int playerNumber;

    private Long opposite;
}
