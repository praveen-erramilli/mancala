package com.praveen.mancala.web;

import com.praveen.mancala.game.GameStore;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import java.io.IOException;

public class GameFilter implements Filter {

    @Autowired
    private GameStore gameStore;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.gameStore.clear();
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
