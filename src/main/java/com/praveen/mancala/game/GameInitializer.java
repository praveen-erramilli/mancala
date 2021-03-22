package com.praveen.mancala.game;

import com.praveen.mancala.AppEnv;
import com.praveen.mancala.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameInitializer {

    private final AppEnv env;

    @Autowired
    public GameInitializer(AppEnv env) {
        this.env = env;
    }

    public Game init() {
        Player player0 = new Player(null, 0);
        Player player1 = new Player(null, 1);

        List<List<Pit>> pits = initPits(player0, player1);

        //mancala0 next points to player1, pit0
        BigPit bigPit0 = new BigPit(null, 0, pits.get(1).get(0), player0,0, null);
        //mancala1 next points to player0, pit0
        BigPit bigPit1 = new BigPit(null, 0, pits.get(0).get(0), player1,1, null);
        int numberOfPits = env.getNumberOfPits();
        //player1, end pit next points to mancala1
        pits.get(1).get(numberOfPits-1).setNext(bigPit1);
        //player0, end pit next points to mancala0
        pits.get(0).get(numberOfPits-1).setNext(bigPit0);

        Board board = new Board(null, pits.get(0), pits.get(1), bigPit0, bigPit1);
        bigPit0.setBoard(board);
        bigPit1.setBoard(board);

        pits.get(0).forEach(pit -> pit.setBoard(board));
        pits.get(1).forEach(pit -> pit.setBoard(board));

        return new Game(null, board, player0, player1, GameStatus.IN_PROGRESS, player0, null, false, null);
    }

    private List<List<Pit>> initPits(Player player0, Player player1) {
        List<List<Pit>> pits = new ArrayList<>(2);
        pits.add(new ArrayList<>());
        pits.add(new ArrayList<>());

        int numberOfPits = env.getNumberOfPits();
        int pitInitialCoins = env.getPitInitialCoins();

        for(int i=0; i < 2; i++) {
            Player player = i == 0 ? player0 : player1;

            for(int j=0; j < numberOfPits; j ++) {
                Pit pit = new Pit(null, pitInitialCoins, null, player,i, null, null);
                pits.get(i).add(j, pit);
            }
        }
        //fill oppositePit values
        for(int i=0; i<numberOfPits; i++) {
            Pit p0 = pits.get(0).get(i);
            Pit p1 = pits.get(1).get(numberOfPits-1-i);;

            p0.setOpposite(p1);
            p1.setOpposite(p0);
        }

        //fill next pit values
        for(int i=0; i<numberOfPits-1; i++) {
            pits.get(0).get(i).setNext(pits.get(0).get(i+1));
            pits.get(1).get(i).setNext(pits.get(1).get(i+1));
        }
        return pits;
    }

}
