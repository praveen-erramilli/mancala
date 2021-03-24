package com.praveen.mancala.controller;

import com.praveen.mancala.exception.GameNotFoundException;
import com.praveen.mancala.model.Game;
import com.praveen.mancala.model.GameStatus;
import com.praveen.mancala.service.GameService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = GameController.class)
class GameControllerTest {

    @MockBean
    private GameService gameService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getGame() throws Exception {
        Mockito.when(gameService.getGame(Mockito.anyLong()))
               .thenReturn(Game.builder().id(123L).gameStatus(GameStatus.IN_PROGRESS).build());

        mockMvc.perform(get("/api/v1/game/123"))
               .andExpect(status().is(200))

               .andExpect(jsonPath("$.id", Matchers.is(123)))
               .andExpect(jsonPath("$.gameStatus", Matchers.is(GameStatus.IN_PROGRESS.name())))
               .andExpect(jsonPath("$.winner", Matchers.nullValue()))
               .andExpect(jsonPath("$.tie", Matchers.is(Boolean.FALSE)))
               .andExpect(jsonPath("$.lastInsertedPit", Matchers.nullValue()));
    }

    @Test
    public void getGameWithInvalidID() throws Exception {
        Mockito.when(gameService.getGame(Mockito.anyLong()))
               .thenThrow(new GameNotFoundException("Invalid argument passed for id. No such game found"));

        mockMvc.perform(get("/api/v1/game/123"))
               .andExpect(status().is(404))

               .andExpect(jsonPath("$.status", Matchers.is("Not Found")))
               .andExpect(jsonPath("$.error", Matchers.is("Invalid argument passed for id. No such game found")));
    }

    @Test
    public void createGame() throws Exception {
        Mockito.when(gameService.createGame())
               .thenReturn(Game.builder().id(234L).gameStatus(GameStatus.IN_PROGRESS).build());

        mockMvc.perform(post("/api/v1/game"))
               .andExpect(status().is(201))
               .andExpect(jsonPath("$.id", Matchers.is(234)))
               .andExpect(jsonPath("$.gameStatus", Matchers.is(GameStatus.IN_PROGRESS.name())))
               .andExpect(jsonPath("$.winner", Matchers.nullValue()))
               .andExpect(jsonPath("$.tie", Matchers.is(Boolean.FALSE)))
               .andExpect(jsonPath("$.lastInsertedPit", Matchers.nullValue()));
    }

    @Test
    public void makeMove() throws Exception {
        Mockito.when(gameService.makeMove(234L, 12L))
               .thenReturn(Game.builder().id(234L).gameStatus(GameStatus.IN_PROGRESS).build());

        mockMvc.perform(put("/api/v1/game/234").param("pit_id", "12"))
               .andExpect(status().is(200))

               .andExpect(jsonPath("$.id", Matchers.is(234)))
               .andExpect(jsonPath("$.gameStatus", Matchers.is(GameStatus.IN_PROGRESS.name())))
               .andExpect(jsonPath("$.winner", Matchers.nullValue()))
               .andExpect(jsonPath("$.tie", Matchers.is(Boolean.FALSE)))
               .andExpect(jsonPath("$.lastInsertedPit", Matchers.nullValue()));
    }

    @Test
    public void makeMoveWithGameNotInProgress() throws Exception {
        Mockito.when(gameService.makeMove(234L, 12L))
               .thenThrow(new IllegalStateException("Cannot run the game that is not in progress"));

        mockMvc.perform(put("/api/v1/game/234").param("pit_id", "12"))
               .andExpect(status().is(400))

               .andExpect(jsonPath("$.status", Matchers.is("Invalid State")))
               .andExpect(jsonPath("$.error", Matchers.is("Cannot run the game that is not in progress")));
    }


    @Test
    public void makeMoveWithGameIDInvalid() throws Exception {
        Mockito.when(gameService.makeMove(234L, 12L))
               .thenThrow(new GameNotFoundException("Invalid argument passed for id. No such game found"));

        mockMvc.perform(put("/api/v1/game/234").param("pit_id", "12"))
               .andExpect(status().is(404))

               .andExpect(jsonPath("$.status", Matchers.is("Not Found")))
               .andExpect(jsonPath("$.error", Matchers.is("Invalid argument passed for id. No such game found")));
    }
}