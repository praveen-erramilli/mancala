package com.praveen.mancala.controller;

import com.praveen.mancala.model.Game;
import com.praveen.mancala.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get details of a game", response = Game.class, notes =
            "API to get the details of a game using its id")
    @ApiResponses(
            value = {
                    @ApiResponse(message = "OK", code = 200),
                    @ApiResponse(message = "Invalid argument passed for id. No such game found", code = 404)
            }
    )
    public Game getGame(@PathVariable(value = "id") Long id) {
        return gameService.getGame(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new game", response = Game.class, notes =
            "API to create a new game")
    @ApiResponses(
            value = {
                    @ApiResponse(message = "OK", code = 200),
            }
    )
    public Game createGame() {
        return gameService.createGame();
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Make a move", response = Game.class, notes =
            "API to make a move by selecting any of the allowed pits")
    @ApiResponses(
            value = {
                    @ApiResponse(message = "OK", code = 200),
                    @ApiResponse(message = "Invalid argument passed for id. No such game found", code = 404),
                    @ApiResponse(message = "Cannot run the game that is not in progress", code = 400),
                    @ApiResponse(message = "User is not allowed to pick from this pit", code = 400)
            }
    )
    public Game makeMove(@PathVariable("id") Long id, @RequestParam("pit_id") Long pitID) {
        return gameService.makeMove(id, pitID);
    }
}
