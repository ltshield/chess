package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import server.Server;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameDataDAO{

    public Server server;
    public Collection<GameData> currentGameData = new ArrayList<>();

    private int id = 0;

    public GameDataDAO(Server server) {
        this.server = server;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    //CRUD
    public int createGame(String gameName, String authToken) throws DataAccessException{
        try {
            server.db.authDataDAO.getAuth(authToken);
            for (GameData game : currentGameData) {
                if (game.gameName().equals(gameName)) {
                    throw new DataAccessException("Game name taken");
                }
            }
//            int i = 0;
//            for (int j = 0; j < 100; j++) {
//                for (GameData game : currentGameData) {
//                    if (game.gameID() == i) {
//                        i++;
//                        break;
//                    }
//                }
//                break;
//            }

            GameData newGame = new GameData(id, null, null, gameName, new ChessGame());
            id++;
            currentGameData.add(newGame);
            return newGame.gameID();
        } catch (DataAccessException e) {
            throw e;
        }
    }
//    public boolean checkGameID(int gameID) throws DataAccessException{
//        for (GameData query : currentGameData) {
//            if (query.gameID() == gameID) {
//                return true;
//            }
//        }
//        return false;
//    }
    public Collection<GameData> listGames(String authToken) throws DataAccessException{
        try {
            server.db.authDataDAO.getAuth(authToken);
            return currentGameData;
        } catch (DataAccessException e){
            throw e;
        }
    }
    public void updateGame(String authToken, int gameID) throws DataAccessException{
        AuthData user = server.db.authDataDAO.getAuth(authToken);
        GameData game = null;
        for (GameData query : currentGameData) {
            if (query.gameID() == gameID) {
                game = query;
            }
        }
        if (game == null) {
            throw new DataAccessException("Game doesn't exist.");
        }
        if (user.username().equals(game.whiteUsername())) {
            if (game.game().getTeamTurn() == WHITE) {
                // make chess move?
            }
        }
        if (user.username().equals(game.blackUsername())) {
            if (game.game().getTeamTurn() == BLACK) {
                // make chess move?
            }
        }
        else {
            throw new DataAccessException("Not in this game.");
        }
    }

    public void addUserToGame(String authToken, int gameID, String playerColor) throws DataAccessException{
        try {
            Collection<String> availableColors = new ArrayList<>();
            availableColors.add("WHITE");
            availableColors.add("BLACK");
            AuthData user = server.db.authDataDAO.getAuth(authToken);
            // cannot join more than one game at a time?
//            for (GameData game : currentGameData) {
//                if (game.blackUsername() != null && game.whiteUsername() != null) {
//                    if (game.blackUsername().equals(user.username()) || game.whiteUsername().equals(user.username())) {
//                        throw new DataAccessException("Error: bad request");
//                    }
//                }
//                if (game.blackUsername() == null && game.whiteUsername() != null) {
//                    if(game.whiteUsername().equals(user.username())) {
//                        throw new DataAccessException("Error: bad request");
//                    }
//                }
//                if (game.whiteUsername() == null && game.blackUsername() != null) {
//                    if (game.blackUsername().equals(user.username())) {
//                        throw new DataAccessException("Error: bad request");
//                    }
//                }
//            }
            if (playerColor == null || !availableColors.contains(playerColor)) {
                throw new DataAccessException("Error: bad request");
            }
            boolean inCollection = false;
            for (GameData gameTest : currentGameData) {
                if (gameTest.gameID() == gameID) {
                    inCollection = true;
                    System.out.println(gameTest);
                    GameData newGame = new GameData(gameTest.gameID(), gameTest.whiteUsername(), gameTest.blackUsername(), gameTest.gameName(), gameTest.game());
                    System.out.println(newGame);
                    if (playerColor.equals("WHITE")) {
//                        if (gameTest.whiteUsername() == null) {
                            if (gameTest.blackUsername() == null) {
                                currentGameData.remove(gameTest);
                                currentGameData.add(new GameData(newGame.gameID(), user.username(), null, newGame.gameName(), newGame.game()));
                                break;
                            }
                            else {
                                    currentGameData.remove(gameTest);
                                    currentGameData.add(new GameData(newGame.gameID(), user.username(), newGame.blackUsername(), newGame.gameName(), newGame.game()));
                                    break;
                            }
                    }
                    // TODO: you cannot be both users in a game
                    if (playerColor.equals("BLACK")) {
//                        if (gameTest.blackUsername() == null) {
                            if (gameTest.whiteUsername() == null) {
                                currentGameData.remove(gameTest);
                                currentGameData.add(new GameData(newGame.gameID(), null, user.username(), newGame.gameName(), newGame.game()));
                                break;
                            } else {
                                    // && !game.whiteUsername().equals(user.username()))
                                    currentGameData.remove(gameTest);
                                    currentGameData.add(new GameData(newGame.gameID(), newGame.whiteUsername(), user.username(), newGame.gameName(), newGame.game()));
                                    break;
                        }
                    } else {
                        throw new DataAccessException("Error: already taken");
                    }
                }
            }
            if (!inCollection) {
                throw new DataAccessException("Error: bad request");
            }
        }
        catch (DataAccessException e) {
            throw e;
        }
    }
    public void deleteGame(String authToken, int gameID) throws DataAccessException{
        try {
            server.db.authDataDAO.getAuth(authToken);
            boolean inCollection = false;
            for (GameData game : currentGameData) {
                if (game.gameID() == gameID) {
                    inCollection = true;
                    currentGameData.remove(game);
                }
            }
            if (!inCollection) {
                throw new DataAccessException("Something went wrong.");
            }
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public void clear() {currentGameData = new ArrayList<>();}
}
