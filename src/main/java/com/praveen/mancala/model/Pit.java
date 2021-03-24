package com.praveen.mancala.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pit {
    @GeneratedValue
    @Id
    private Long id;

    private int coinsCount;

    @OneToOne
    private Pit next;

    @OneToOne(cascade = CascadeType.ALL)
    private Player owner;

    private int playerNumber;

    @OneToOne
    private Pit opposite;

    @ManyToOne
    @JsonBackReference
    private Board board;

    public boolean canInsert(Game game) {
        return true;
    }

    public void insertCoin(Game game) {
        if(canInsert(game)) {
            this.coinsCount++;
        } else {
            throw new UnsupportedOperationException("User is not allowed to add coin in this pit");
        }
    }

    public void onLastCoinInsert(Game game) {
        if(!canInsert(game)) {
            throw new UnsupportedOperationException("User is not allowed to add coin in this Mancala");
        }
        if(coinsCount == 1 && game.getCurrentPlayer().equals(owner)) {
            int totalPickedCoins = pickCoins(game) + (opposite.canPickCoins(game) ? opposite.pickCoins(game) : 0);
            game.getCurrentPlayerBigPit().insertMultipleCoins(game, totalPickedCoins);
        }
        game.switchPlayer();
    }

    public boolean canPickCoins(Game game) {
        if(game.getGameStatus().equals(GameStatus.GAMEOVER)) {
            return true;    //When game is over, pick all coins and put in mancala
        }
        if(coinsCount == 0) {
            return false;
        }
        Player currentPlayer = game.getCurrentPlayer();
        Pit lastInsertedPit = game.getLastInsertedPit();

        return owner.equals(currentPlayer) ||
                isLastInsertedInOppositeEmptyPit(currentPlayer, lastInsertedPit);
    }

    private boolean isLastInsertedInOppositeEmptyPit(Player currentPlayer, Pit lastInsertedPit) {
        return opposite.owner.equals(currentPlayer)
                && opposite.equals(lastInsertedPit)
                && opposite.coinsCount == 0;
    }

    public int pickCoins(Game game) {
        if(canPickCoins(game)) {
            int coins = this.coinsCount;
            this.coinsCount = 0;
            return coins;
        } else {
            throw new UnsupportedOperationException("User is not allowed to pick coins from this pit");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pit pit = (Pit) o;
        if ((opposite == null && pit.opposite != null) || (opposite != null && pit.opposite == null)) {
            return false;
        }
        boolean isEqual = coinsCount == pit.coinsCount && playerNumber == pit.playerNumber && id.equals(pit.id) &&
                next.id.equals(pit.next.id) && owner.equals(pit.owner) && Objects.equals(opposite, pit.opposite) && board.getId().equals(pit.board.getId());
        if(opposite != null && pit.opposite != null) {
            isEqual = isEqual && opposite.id.equals(pit.opposite.id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        if(opposite == null) {
            return Objects.hash(id, coinsCount, next.id, owner, playerNumber, board.getId());
        }
        return Objects.hash(id, coinsCount, next.id, owner, playerNumber, opposite.id, board.getId());
    }
}
