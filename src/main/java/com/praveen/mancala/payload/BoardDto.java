package com.praveen.mancala.payload;

import lombok.Data;

import java.util.List;

@Data
public class BoardDto {
    private Long id;

    private List<PitDto> pitsForPlayerZero;

    private List<PitDto> pitsForPlayerOne;

    private BigPitDto bigPitForPlayerZero;

    private BigPitDto bigPitForPlayerOne;
}
