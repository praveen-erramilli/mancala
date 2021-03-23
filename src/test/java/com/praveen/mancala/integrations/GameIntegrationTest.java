package com.praveen.mancala.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.praveen.mancala.AppEnv;
import com.praveen.mancala.cache.IGameCache;
import com.praveen.mancala.model.Board;
import com.praveen.mancala.model.Game;
import com.praveen.mancala.model.GameStatus;
import com.praveen.mancala.model.Pit;
import com.praveen.mancala.repository.GameRepository;
import com.praveen.mancala.service.GameService;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class GameIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IGameCache gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppEnv appEnv;

    @Test
    public void testCreatingGame() throws Exception {
        Game game = createANewGame();

        //check num of coins in big pits
        assertEquals(0, game.getBoard().getBigPitForPlayerZero().getCoinsCount());
        assertEquals(0, game.getBoard().getBigPitForPlayerOne().getCoinsCount());
        assertEquals(game.getPlayerZero(), game.getCurrentPlayer());
        assertFalse(game.isTie());
        assertNull(game.getWinner());
        checkBoardIntegrity(game);
    }

    @Test
    public void testAnotherChanceForPlayerZero() throws Exception {
        Game game = createANewGame();
        Pit firstPitForPlayerZero = game.getBoard().getPitsForPlayerZero().get(0);

        mockMvc.perform(put("/api/v1/game/"+game.getId()).param("pit_id",firstPitForPlayerZero.getId().toString()))
               .andExpect(status().is(200))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.id", Matchers.is(game.getId().intValue())))
               .andExpect(jsonPath("$.gameStatus", Matchers.is(GameStatus.IN_PROGRESS.name())))
               .andExpect(jsonPath("$.winner", Matchers.nullValue()))
               .andExpect(jsonPath("$.tie", Matchers.is(Boolean.FALSE)))
               .andExpect(jsonPath("$.lastInsertedPit.id", Matchers.is(game.getBoard().getBigPitForPlayerZero().getId().intValue())))
               .andExpect(jsonPath("$.currentPlayer.playerNumber", Matchers.is(game.getPlayerZero().getPlayerNumber())))
               .andExpect(jsonPath("$.currentPlayer.id", Matchers.is(game.getPlayerZero().getId().intValue())));
        Game gameFromDB = gameRepository.getGame(game.getId()).get();
        checkBoardIntegrity(gameFromDB);
    }

    @Test
    public void testAnotherChanceForPlayerOne() throws Exception {
        Game game = createANewGame();
        Pit firstPitForPlayerZero = game.getBoard().getPitsForPlayerZero().get(0);

        mockMvc.perform(put("/api/v1/game/"+game.getId()).param("pit_id",firstPitForPlayerZero.getId().toString()))
               .andExpect(status().is(200))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.id", Matchers.is(game.getId().intValue())));
        mockMvc.perform(put("/api/v1/game/"+game.getId()).param("pit_id",firstPitForPlayerZero.getNext().getId().toString()))
               .andExpect(status().is(200))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.id", Matchers.is(game.getId().intValue())));

        //chance now goes to player one
        Game gameFromDB = gameRepository.getGame(game.getId()).get();
        assertEquals(game.getPlayerOne(), gameFromDB.getCurrentPlayer());

        List<Pit> pitsForPlayerOne = gameFromDB.getBoard().getPitsForPlayerOne();
        Pit lastPit = pitsForPlayerOne.get(pitsForPlayerOne.size()-1);
        Pit firstPit = pitsForPlayerOne.get(0);
        firstPit.setCoinsCount(firstPit.getCoinsCount() + lastPit.getCoinsCount());
        lastPit.setCoinsCount(1);
        gameRepository.saveGame(gameFromDB);

        mockMvc.perform(put("/api/v1/game/"+game.getId()).param("pit_id",lastPit.getId().toString()))
               .andExpect(status().is(200))
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
               .andExpect(jsonPath("$.id", Matchers.is(game.getId().intValue())));
        gameFromDB = gameRepository.getGame(game.getId()).get();
        assertEquals(game.getPlayerOne(), gameFromDB.getCurrentPlayer());
    }

    private Game createANewGame() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/game"))
                                     .andExpect(status().is(200))
                                     .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                                     .andExpect(jsonPath("$.id", Matchers.notNullValue(Long.class)))
                                     .andExpect(jsonPath("$.gameStatus",
                                             Matchers.is(GameStatus.IN_PROGRESS.name())))
                                     .andExpect(jsonPath("$.winner", Matchers.nullValue()))
                                     .andExpect(jsonPath("$.tie", Matchers.is(Boolean.FALSE)))
                                     .andExpect(jsonPath("$.lastInsertedPit", Matchers.nullValue()))
                                     .andReturn();

        JSONObject gameJson = new JSONObject(mvcResult.getResponse().getContentAsString());
        Game game = gameRepository.getGame(gameJson.getLong("id")).get();
        return game;
    }

    private void checkBoardIntegrity(Game game) {
        int numPits = appEnv.getNumberOfPits();
        int initialCoins = appEnv.getPitInitialCoins();

        //check num of pits
        Board board = game.getBoard();
        assertEquals(numPits, board.getPitsForPlayerZero().size());
        assertEquals(numPits, board.getPitsForPlayerOne().size());

        //player zero bigpit -> player one first pit
        assertEquals(board.getBigPitForPlayerZero().getNext(), board.getPitsForPlayerOne().get(0));
        //player one bigpit -> player zero first pit
        assertEquals(board.getBigPitForPlayerOne().getNext(), board.getPitsForPlayerZero().get(0));

        //player zero is owner for all pits in his list
        board.getPitsForPlayerZero().forEach(pit -> assertEquals(pit.getOwner(), game.getPlayerZero()));
        //player one is owner for all pits in his list
        board.getPitsForPlayerOne().forEach(pit -> assertEquals(pit.getOwner(), game.getPlayerOne()));

        //check total coins
        int totalCoins = numPits * initialCoins * 2;
        int playerZeroTotalCoins = board.getPitsForPlayerZero().stream().mapToInt(Pit::getCoinsCount).reduce(0, Integer::sum);
        int playerOneTotalCoins = board.getPitsForPlayerOne().stream().mapToInt(Pit::getCoinsCount).reduce(0, Integer::sum);
        int playerZeroBigPitTotalCoins = board.getBigPitForPlayerZero().getCoinsCount();
        int playerOneBigPitTotalCoins = board.getBigPitForPlayerOne().getCoinsCount();

        assertEquals(totalCoins, playerOneTotalCoins + playerZeroTotalCoins + playerZeroBigPitTotalCoins + playerOneBigPitTotalCoins);

        //check next link
        int totalRounds = 2 + (2 * numPits);
        Pit headPit = board.getPitsForPlayerZero().get(0);
        Pit currentPit = headPit;
        for (int i = 0; i < totalRounds; i++) {
            currentPit = currentPit.getNext();
        }
        assertEquals(headPit, currentPit);

        //check opposite link
        for(int i=0; i<numPits; i++) {
            Pit p0 = board.getPitsForPlayerZero().get(i);
            Pit p1 = board.getPitsForPlayerOne().get(numPits-1-i);;

            assertEquals(p0.getOpposite(), p1);
            assertEquals(p1.getOpposite(), p0);
        }
    }
}