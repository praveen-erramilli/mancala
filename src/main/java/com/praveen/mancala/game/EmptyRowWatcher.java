package com.praveen.mancala.game;

import com.praveen.mancala.model.*;

import java.util.List;

public class EmptyRowWatcher {
    private final Game game;
    public EmptyRowWatcher(Game game) {
        this.game = game;
    }

    public void watchAndHandleEmptyRow() {
        Board board = game.getBoard();
        boolean foundEmpty0 = isEmptyRow(board.getPitsForPlayerZero());
        boolean foundEmpty1 = isEmptyRow(board.getPitsForPlayerOne());

        if(foundEmpty0) {
            game.setGameStatus(GameStatus.GAMEOVER);
            pushAllCoinsToMancala(game, board.getPitsForPlayerOne(), board.getBigPitForPlayerOne());
        } else if(foundEmpty1) {
            game.setGameStatus(GameStatus.GAMEOVER);
            pushAllCoinsToMancala(game, board.getPitsForPlayerZero(), board.getBigPitForPlayerZero());
        }

        if(foundEmpty0 || foundEmpty1) {
            BigPit bigPit0 = board.getBigPitForPlayerZero();
            BigPit bigPit1 = board.getBigPitForPlayerOne();

            if(bigPit0.getCoinsCount() == bigPit1.getCoinsCount()) {
                game.setTie(true);
            } else if(bigPit0.getCoinsCount() < bigPit1.getCoinsCount()) {
                game.setWinner(game.getPlayerOne());
            } else {
                game.setWinner(game.getPlayerZero());
            }
        }
    }

    private boolean isEmptyRow(List<Pit> pits) {
        return pits.stream().noneMatch(pit -> pit.getCoinsCount() != 0);
    }

    private void pushAllCoinsToMancala(Game game, List<Pit> pits, BigPit bigPit) {
        for (Pit pit : pits) {
            int pickedCoins = pit.pickCoins(game);
            bigPit.insertMultipleCoins(game, pickedCoins);
        }
    }
}
