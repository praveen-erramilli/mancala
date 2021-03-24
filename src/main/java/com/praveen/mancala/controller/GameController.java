package com.praveen.mancala.controller;

import com.praveen.mancala.model.Game;
import com.praveen.mancala.payload.GameDto;
import com.praveen.mancala.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/game")
@Slf4j
public class GameController {
    private final GameService gameService;
    private final ModelMapper modelMapper;

    @Autowired
    public GameController(GameService gameService, ModelMapper modelMapper) {
        this.gameService = gameService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get details of a game", response = GameDto.class, notes =
            "API to get the details of a game using its id")
    @ApiResponses(
            value = {
                    @ApiResponse(message = "OK", code = 200),
                    @ApiResponse(message = "Invalid argument passed for id. No such game found", code = 404)
            }
    )
    public GameDto getGame(@PathVariable(value = "id") Long id) {
        log.info("Requested a game having id {}",id);
        Game game = gameService.getGame(id);
        return modelMapper.map(game, GameDto.class);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new game", response = GameDto.class, notes =
            "API to create a new game")
    @ApiResponses(
            value = {
                    @ApiResponse(message = "Created", code = 201),
            }
    )
    public GameDto createGame(HttpServletResponse response) {
        log.info("Create Game request received");
        Game game = gameService.createGame();
        response.setStatus(HttpStatus.CREATED.value());
        log.info("Created a game with id {}",game.getId());
        return modelMapper.map(game, GameDto.class);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Make a move", response = GameDto.class, notes =
            "API to make a move by selecting any of the allowed pits")
    @ApiResponses(
            value = {
                    @ApiResponse(message = "OK", code = 200),
                    @ApiResponse(message = "Invalid argument passed for id. No such game found", code = 404),
                    @ApiResponse(message = "Cannot run the game that is not in progress", code = 400),
                    @ApiResponse(message = "User is not allowed to pick from this pit", code = 400)
            }
    )
    public GameDto makeMove(@PathVariable("id") Long id, @RequestParam("pit_id") Long pitID) {
        log.info("Received a move for the pit {} in the game {} ",pitID, id);
        Game game = gameService.makeMove(id, pitID);
        return modelMapper.map(game, GameDto.class);
    }
}
