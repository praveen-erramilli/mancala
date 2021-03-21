package com.praveen.mancala.game;

import com.praveen.mancala.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.praveen.mancala.AppConstants.NUM_PITS;
import static com.praveen.mancala.AppConstants.PIT_INITIAL_COINS;

@Component
public class GameInitializer {

    public Game init() {
        Player player0 = new Player(null, 0);
        Player player1 = new Player(null, 1);

        List<List<Pit>> pits = initPits(player0, player1);

        //mancala0 next points to player1, pit0
        Mancala mancala0 = new Mancala(null, 0, pits.get(1).get(0), player0,0, null);
        //mancala1 next points to player0, pit0
        Mancala mancala1 = new Mancala(null, 0, pits.get(0).get(0), player1,1, null);
        //player1, end pit next points to mancala1
        pits.get(1).get(NUM_PITS-1).setNext(mancala1);
        //player0, end pit next points to mancala0
        pits.get(0).get(NUM_PITS-1).setNext(mancala0);

        Board board = new Board(null, pits.get(0), pits.get(1), mancala0, mancala1);
        mancala0.setBoard(board);
        mancala1.setBoard(board);

        pits.get(0).forEach(pit -> pit.setBoard(board));
        pits.get(1).forEach(pit -> pit.setBoard(board));

        return new Game(null, board, player0, player1, GameStatus.IN_PROGRESS, player0, null, false, null);
    }

    private List<List<Pit>> initPits(Player player0, Player player1) {
        List<List<Pit>> pits = new ArrayList<>(2);
        pits.add(new ArrayList<>());
        pits.add(new ArrayList<>());

        for(int i=0; i < 2; i++) {
            Player player = i == 0 ? player0 : player1;

            for(int j=0; j < NUM_PITS; j ++) {
                Pit pit = new Pit(null, PIT_INITIAL_COINS, null, player,i, null, null);
                pits.get(i).add(j, pit);
            }
        }
        //fill oppositePit values
        for(int i=0; i<NUM_PITS; i++) {
            Pit p0 = pits.get(0).get(i);
            Pit p1 = pits.get(1).get(NUM_PITS-1-i);;

            p0.setOpposite(p1);
            p1.setOpposite(p0);
        }

        //fill next pit values
        for(int i=0; i<NUM_PITS-1; i++) {
            pits.get(0).get(i).setNext(pits.get(0).get(i+1));
            pits.get(1).get(i).setNext(pits.get(1).get(i+1));
        }
        return pits;
    }

}
