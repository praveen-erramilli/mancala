package com.praveen.mancala.integrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.praveen.mancala.AppEnv;
import com.praveen.mancala.MancalaApplication;
import com.praveen.mancala.cache.IGameCache;
import com.praveen.mancala.model.*;
import com.praveen.mancala.payload.BoardDto;
import com.praveen.mancala.payload.GameDto;
import com.praveen.mancala.payload.PitDto;
import com.praveen.mancala.payload.PlayerDto;
import com.praveen.mancala.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MancalaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IGameCache gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppEnv appEnv;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testCreatingGame() throws Exception {
        GameDto game = createANewGame();

        //check num of coins in big pits
        assertEquals(0, game.getBoard().getBigPitForPlayerZero().getCoinsCount());
        assertEquals(0, game.getBoard().getBigPitForPlayerOne().getCoinsCount());
        assertEquals(game.getPlayerZero(), game.getCurrentPlayer());
        assertFalse(game.isTie());
        assertNull(game.getWinner());
        checkBoardIntegrity(gameRepository.getGame(game.getId()).get());
    }

    @Test
    public void testGettingAGame() throws Exception {
        GameDto game = createANewGame();

        GameDto gameFromAPI = getGame(game.getId());
        assertEquals(game.getId(), gameFromAPI.getId());
        assertEquals(GameStatus.IN_PROGRESS, gameFromAPI.getGameStatus());
        assertNull(gameFromAPI.getWinner());
        assertFalse(gameFromAPI.isTie());
        assertEquals(game.getPlayerZero(), gameFromAPI.getCurrentPlayer());
    }


    @Test
    public void testGettingAnInvalidGame() throws Exception {
        GameDto game = createANewGame();
        ResponseEntity<ErrorModel> responseEntity = restTemplate.getForEntity(gameURL() + "/123412342", ErrorModel.class);
        assertEquals(404, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals("Not Found", responseEntity.getBody().getStatus());
        assertEquals("Invalid argument passed for id. No such game found", responseEntity.getBody().getError());
    }

    @Test
    public void testAnotherChanceForPlayerZero() throws Exception {
        GameDto game = createANewGame();
        PitDto firstPitForPlayerZero = game.getBoard().getPitsForPlayerZero().get(0);

        GameDto gameResponse = makeAMove(game.getId(), firstPitForPlayerZero.getId());
        assertEquals(GameStatus.IN_PROGRESS, gameResponse.getGameStatus());
        assertNull(gameResponse.getWinner());
        assertFalse(gameResponse.isTie());
        assertEquals(game.getBoard().getBigPitForPlayerZero().getId().intValue(), gameResponse.getLastInsertedPit().getId());
        assertEquals(game.getPlayerZero(), gameResponse.getCurrentPlayer());

        Game gameFromDB = gameRepository.getGame(game.getId()).get();
        checkBoardIntegrity(gameFromDB);
    }

    @Test
    public void testAnotherChanceForPlayerOne() throws Exception {
        GameDto game = createANewGame();
        PitDto firstPitForPlayerZero = game.getBoard().getPitsForPlayerZero().get(0);

        makeAMove(game.getId(), firstPitForPlayerZero.getId());
        makeAMove(game.getId(), firstPitForPlayerZero.getNext());

        //chance now goes to player one
        Game gameFromDB = gameRepository.getGame(game.getId()).get();
        assertEquals(game.getPlayerOne(), modelMapper.map(gameFromDB.getCurrentPlayer(), PlayerDto.class));
        checkBoardIntegrity(gameFromDB);

        List<Pit> pitsForPlayerOne = gameFromDB.getBoard().getPitsForPlayerOne();
        Pit lastPit = pitsForPlayerOne.get(pitsForPlayerOne.size()-1);
        Pit firstPit = pitsForPlayerOne.get(0);
        firstPit.setCoinsCount(firstPit.getCoinsCount() + lastPit.getCoinsCount() - 1);
        lastPit.setCoinsCount(1);
        gameRepository.saveGame(gameFromDB);

        makeAMove(game.getId(), lastPit.getId());

        gameFromDB = gameRepository.getGame(game.getId()).get();
        assertEquals(game.getPlayerOne(), modelMapper.map(gameFromDB.getCurrentPlayer(), PlayerDto.class));
        checkBoardIntegrity(gameFromDB);
    }


    @Test
    public void testSelectingOpponentsPitByPlayerZero() throws Exception {
        GameDto game = createANewGame();
        PitDto firstPitForPlayerZero = game.getBoard().getPitsForPlayerZero().get(0);

        makeAMoveExpectError(game.getId(), firstPitForPlayerZero.getOpposite(), 400, "Invalid Operation", "User is not allowed to pick from this pit");
    }

    @Test
    public void testSelectingOpponentsPitByPlayerOne() throws Exception {
        GameDto game = createANewGame();
        PitDto pit = game.getBoard().getPitsForPlayerZero().get(3);

        makeAMove(game.getId(), pit.getId());
        makeAMoveExpectError(game.getId(), pit.getNext(), 400, "Invalid Operation", "User is not allowed to pick from this pit");
    }

    @Test
    public void testWinningMoveForPlayerOne() throws Exception {
        GameDto gameDto = createANewGame();
        Game game = gameRepository.getGame(gameDto.getId()).get();

        List<Pit> pitsForPlayerZero = game.getBoard().getPitsForPlayerZero();
        for (Pit pit : pitsForPlayerZero) {
            Pit opposite = pit.getOpposite();
            opposite.setCoinsCount(opposite.getCoinsCount()+pit.getCoinsCount());
            pit.setCoinsCount(0);
        }
        Pit lastPit = pitsForPlayerZero.get(pitsForPlayerZero.size() - 1);
        Pit firstPitPlayer1 = lastPit.getOpposite();
        firstPitPlayer1.setCoinsCount(firstPitPlayer1.getCoinsCount()-3);
        lastPit.setCoinsCount(3);

        gameRepository.saveGame(game);

        makeAMove(game.getId(), lastPit.getId());

        Game game1 = gameRepository.getGame(game.getId()).get();
        assertFalse(game1.isTie());
        assertEquals(game.getPlayerOne(), game1.getWinner());
        checkBoardIntegrity(game1);
    }

    @Test
    public void testMovingWithGameNotInProgress() throws Exception {
        GameDto gameDto = createANewGame();
        Game game = gameRepository.getGame(gameDto.getId()).get();

        List<Pit> pitsForPlayerZero = game.getBoard().getPitsForPlayerZero();
        for (Pit pit : pitsForPlayerZero) {
            Pit opposite = pit.getOpposite();
            opposite.setCoinsCount(opposite.getCoinsCount()+pit.getCoinsCount());
            pit.setCoinsCount(0);
        }
        Pit lastPit = pitsForPlayerZero.get(pitsForPlayerZero.size() - 1);
        Pit firstPitPlayer1 = lastPit.getOpposite();
        firstPitPlayer1.setCoinsCount(firstPitPlayer1.getCoinsCount()-3);
        lastPit.setCoinsCount(3);

        gameRepository.saveGame(game);

        makeAMove(game.getId(), lastPit.getId());

        Game game1 = gameRepository.getGame(game.getId()).get();

        makeAMoveExpectError(game.getId(), lastPit.getId(), 400, "Invalid State", "Cannot run the game that is not in progress");
    }

    @Test
    public void testStealing() throws Exception {
        GameDto gameDto = createANewGame();
        Game game = gameRepository.getGame(gameDto.getId()).get();

        List<Pit> pitsForPlayerZero = game.getBoard().getPitsForPlayerZero();
        Pit secondPit = pitsForPlayerZero.get(1);
        Pit secondOpp = secondPit.getOpposite();

        Pit thirdPit = pitsForPlayerZero.get(2);
        Pit thirdOpp = thirdPit.getOpposite();

        secondOpp.setCoinsCount(secondOpp.getCoinsCount() + secondPit.getCoinsCount() - 1);
        secondPit.setCoinsCount(1);

        int coinsCount = thirdOpp.getCoinsCount() + thirdPit.getCoinsCount();
        thirdOpp.setCoinsCount(coinsCount);
        thirdPit.setCoinsCount(0);
        gameRepository.saveGame(game);

        makeAMove(game.getId(), secondPit.getId());

        game = gameRepository.getGame(game.getId()).get();
        checkBoardIntegrity(game);

        assertEquals(game.getPlayerOne(), game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayerBigPit(), game.getBoard().getBigPitForPlayerOne());

        BigPit bigPitPlayerZero = game.getBoard().getBigPitForPlayerZero();
        pitsForPlayerZero = game.getBoard().getPitsForPlayerZero();
        thirdPit = pitsForPlayerZero.get(2);
        thirdOpp = thirdPit.getOpposite();
        secondPit = pitsForPlayerZero.get(1);

        assertEquals(0, thirdOpp.getCoinsCount());
        assertEquals(0, thirdPit.getCoinsCount());
        assertEquals(0, secondPit.getCoinsCount());
        assertEquals(coinsCount+1, bigPitPlayerZero.getCoinsCount());
    }

    @Test
    public void testInsertingToBigPit() throws Exception {
        GameDto gameDto = createANewGame();
        Game game = gameRepository.getGame(gameDto.getId()).get();

        BigPit bigPitForPlayerOne = game.getBoard().getBigPitForPlayerOne();

        Assertions.assertThrows(UnsupportedOperationException.class, () ->
        bigPitForPlayerOne.insertMultipleCoins(game, 10));

        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                bigPitForPlayerOne.onLastCoinInsert(game));

        Assertions.assertThrows(UnsupportedOperationException.class, () ->
        bigPitForPlayerOne.pickCoins(game));
    }

    @Test
    public void fetchBoardForInvalidID() throws Exception {
        GameDto gameDto = createANewGame();
        Game game = gameRepository.getGame(gameDto.getId()).get();

        Assertions.assertThrows(IllegalArgumentException.class, () ->
        game.getBoard().fetchPit(-1L));
    }

    @Test
    public void insertIntoInvalidPit() throws Exception {
        GameDto gameDto = createANewGame();
        Game game = gameRepository.getGame(gameDto.getId()).get();

        BigPit pit = game.getBoard().getBigPitForPlayerOne();
        assertThrows(UnsupportedOperationException.class , () -> pit.insertCoin(game));
        assertThrows(UnsupportedOperationException.class , () -> pit.onLastCoinInsert(game));

        pit.setCoinsCount(0);
        assertFalse(pit.canPickCoins(game));
    }

    private GameDto createANewGame() throws Exception {
        ResponseEntity<GameDto> gameResponseEntity = restTemplate.postForEntity(gameURL(), null, GameDto.class);
        assertEquals(201, gameResponseEntity.getStatusCode().value());
        GameDto game = gameResponseEntity.getBody();
        assertNotNull(game);
        assertNotNull(game.getId());
        assertEquals(game.getGameStatus(), GameStatus.IN_PROGRESS);
        assertNull(game.getWinner());
        assertFalse(game.isTie());
        assertNull(game.getLastInsertedPit());
        return game;
    }

    private GameDto getGame(Long id) {
        ResponseEntity<GameDto> responseEntity = restTemplate.getForEntity(gameURL() + "/" + id, GameDto.class);
        assertEquals(200, responseEntity.getStatusCode().value());
        return responseEntity.getBody();
    }

    private GameDto makeAMove(Long gameId, Long pitId) {
        ResponseEntity<GameDto> game = callPutAPI(gameId, pitId, GameDto.class);
        assertEquals(200, game.getStatusCode().value());
        assertNotNull(game.getBody());
        assertEquals(gameId, game.getBody().getId());

        return game.getBody();
    }

    private ErrorModel makeAMoveExpectError(Long gameId, Long pitId, int expectedErrorCode, String expectedStatus, String expectedMessage) {
        ResponseEntity<ErrorModel> errorModel = callPutAPI(gameId, pitId, ErrorModel.class);
        assertEquals(expectedErrorCode, errorModel.getStatusCode().value());
        assertNotNull(errorModel.getBody());
        assertEquals(expectedStatus, errorModel.getBody().getStatus());
        assertEquals(expectedMessage, errorModel.getBody().getError());
        return errorModel.getBody();
    }

    private <T> ResponseEntity<T> callPutAPI(Long gameId, Long pitId, Class<T> entityType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("pit_id", pitId.toString());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.exchange(gameURL() + "/{id}", HttpMethod.PUT, request, entityType
                , gameId);
    }

    private String gameURL() {
        return "http://localhost:"+port+"/api/v1/game";
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
