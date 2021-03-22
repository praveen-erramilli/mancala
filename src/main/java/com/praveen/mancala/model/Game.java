package com.praveen.mancala.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {
    @GeneratedValue
    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Board board;

    @OneToOne(cascade = CascadeType.ALL)
    private Player playerZero;

    @OneToOne(cascade = CascadeType.ALL)
    private Player playerOne;

    @Enumerated
    private GameStatus gameStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @Setter(value = AccessLevel.PRIVATE)
    private Player currentPlayer;

    @OneToOne(cascade = CascadeType.ALL)
    private Player winner;

    private boolean isTie;

    @OneToOne(cascade = CascadeType.ALL)
    private Pit lastInsertedPit;

    @JsonIgnore
    public BigPit getCurrentPlayerMancala() {
        if(currentPlayer.getPlayerNumber() == 0) {
            return board.getBigPitForPlayerZero();
        }
        return board.getBigPitForPlayerOne();
    }

    public void switchPlayer() {
        if(getCurrentPlayer().equals(playerZero)) {
            setCurrentPlayer(playerOne);
        } else {
            setCurrentPlayer(playerZero);
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", player0=" + playerZero +
                ", player1=" + playerOne +
                ", gameStatus=" + gameStatus +
                ", currentPlayer=" + currentPlayer +
                ", winner=" + winner +
                ", isTie=" + isTie +
                ", lastInsertedPit=" + lastInsertedPit +
                '}';
    }
}
