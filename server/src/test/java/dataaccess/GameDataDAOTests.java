package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataDAOTests {

    @Test
    void createGameTest() {
        Server server = new Server();
        String authToken = "1234";
        server.db.authDataDAO.currentUsers.add(new AuthData(authToken, "username"));
        boolean worked = false;
        try {
            int gameID = server.db.gameDataDAO.createGame("gameName", authToken);
            for (GameData query : server.db.gameDataDAO.currentGameData) {
                if (query.gameID() == gameID) {
                    worked = true;
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
        assertTrue(worked);
    }

    @Test
    void listGamesTest() {
        Server server = new Server();
        String authToken = "1234";
        server.db.authDataDAO.currentUsers.add(new AuthData(authToken, "username"));
        server.db.gameDataDAO.currentGameData.add(new GameData(0, null, null, "game0", new ChessGame()));
        server.db.gameDataDAO.currentGameData.add(new GameData(1, null, null, "game1", new ChessGame()));

        try {
            Collection<GameData> games = server.db.gameDataDAO.listGames(authToken);
            System.out.println(games);
            assertFalse(games.isEmpty());
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
    }

    @Test
    void clearTest() {
        Server server = new Server();
        String authToken = "1234";
        server.db.authDataDAO.currentUsers.add(new AuthData(authToken, "username"));
        server.db.gameDataDAO.currentGameData.add(new GameData(0, null, null, "game0", new ChessGame()));
        server.db.gameDataDAO.currentGameData.add(new GameData(1, null, null, "game1", new ChessGame()));

        try {
            Collection<GameData> games = server.db.gameDataDAO.listGames(authToken);
            System.out.println(games);
            server.db.gameDataDAO.clear();
            assertTrue(games.isEmpty());
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
    }

    @Test
    void deleteGameTest() {
        Server server = new Server();
        String authToken = "1234";
        server.db.authDataDAO.currentUsers.add(new AuthData(authToken, "username"));
        server.db.gameDataDAO.currentGameData.add(new GameData(0, null, null, "game0", new ChessGame()));
        server.db.gameDataDAO.currentGameData.add(new GameData(1, null, null, "game1", new ChessGame()));

        try {
            Collection<GameData> games = server.db.gameDataDAO.listGames(authToken);
            System.out.println(games);
            server.db.gameDataDAO.deleteGame(authToken, 0);
            System.out.println(games);
            boolean inIt = false;
            for (GameData game : server.db.gameDataDAO.currentGameData) {
                if (game.gameID() == 0) {
                    inIt = true;
                }
            }
            assertFalse(inIt);
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
    }

    @Test
    void addUsertoGame() {
        Server server = new Server();
        String authToken = "1234";
        server.db.authDataDAO.currentUsers.add(new AuthData(authToken, "username"));
        server.db.gameDataDAO.currentGameData.add(new GameData(0, null, null, "game0", new ChessGame()));

        try {
            boolean whitePlayerAdded = false;
            server.db.gameDataDAO.addUserToGame(authToken,0, "White");
            System.out.println(server.db.gameDataDAO.currentGameData);
            for (GameData game : server.db.gameDataDAO.currentGameData) {
                if (game.whiteUsername() != null) {
                    whitePlayerAdded = true;
                }
            }
            assertTrue(whitePlayerAdded);
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
    }

}
