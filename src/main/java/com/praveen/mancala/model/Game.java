package com.praveen.mancala.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @GeneratedValue
    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Board board;

    @OneToOne
    private Player player0;

    @OneToOne
    private Player player1;

    @Enumerated
    private GameStatus gameStatus;

    @OneToOne
    private Player currentPlayer;

    @OneToOne
    private Player winner;

    private boolean isTie;

    @OneToOne
    private Pit lastInsertedPit;

    public Mancala getCurrentPlayerMancala() {
        if(currentPlayer.getPlayerNumber() == 0) {
            return board.getMancala0();
        }
        return board.getMancala1();
    }

    public void switchPlayer() {
        if(getCurrentPlayer().equals(player0)) {
            setCurrentPlayer(player1);
        } else {
            setCurrentPlayer(player0);
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", player0=" + player0 +
                ", player1=" + player1 +
                ", gameStatus=" + gameStatus +
                ", currentPlayer=" + currentPlayer +
                ", winner=" + winner +
                ", isTie=" + isTie +
                ", lastInsertedPit=" + lastInsertedPit +
                '}';
    }
}
