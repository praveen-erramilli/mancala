package com.praveen.mancala.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

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

    @OneToOne(cascade = CascadeType.ALL)
    private Player player0;

    @OneToOne(cascade = CascadeType.ALL)
    private Player player1;

    @Enumerated
    private GameStatus gameStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private Player currentPlayer;

    @OneToOne(cascade = CascadeType.ALL)
    private Player winner;

    private boolean isTie;

    @OneToOne(cascade = CascadeType.ALL)
    private Pit lastInsertedPit;

    @JsonIgnore
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
