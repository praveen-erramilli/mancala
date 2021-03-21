package com.praveen.mancala.controller;

import com.praveen.mancala.model.Game;
import com.praveen.mancala.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public Game getGame(@PathVariable(value = "id")  Long id) {
        return gameService.getGame(id);
    }

    @PostMapping
    public Game createGame() {
        return gameService.createGame();
    }

    @PutMapping("/{id}")
    public Game makeMove(@PathVariable("id") Long id, @RequestParam("pit_id") Long pitID) {
        return gameService.makeMove(id, pitID);
    }
}
