package com.praveen.mancala;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppEnv {

    private final Environment env;

    @Autowired
    public AppEnv(Environment env) {
        this.env = env;
    }

    public int getNumberOfPits() {
        return env.getRequiredProperty("mancala.board.num-of-pits", Integer.class);
    }

    public int getPitInitialCoins() {
        return env.getRequiredProperty("mancala.board.initial-coins", Integer.class);
    }
}
