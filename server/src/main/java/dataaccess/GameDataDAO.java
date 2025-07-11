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
            GameData newGame = new GameData(Integer.parseInt(generateToken()), null, null, gameName, new ChessGame());
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
            AuthData user = server.db.authDataDAO.getAuth(authToken);
            boolean inCollection = false;
            for (GameData game : currentGameData) {
                if (game.gameID() == gameID) {
                    inCollection = true;
                    GameData newGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                    if (playerColor.equals("White")) {
                        if (game.whiteUsername() == null && !game.blackUsername().equals(user.username())) {
                            currentGameData.remove(game);
                            currentGameData.add(new GameData(newGame.gameID(), user.username(), newGame.blackUsername(), newGame.gameName(), newGame.game()));
                            break;
                        }
                    }
                    // TODO: you cannot be both users in a game
                    if (playerColor.equals("Black")) {
                        if (game.blackUsername() == null && !game.whiteUsername().equals(user.username())) {
                            currentGameData.remove(game);
                            currentGameData.add(new GameData(newGame.gameID(), newGame.whiteUsername(), user.username(), newGame.gameName(), newGame.game()));
                            break;
                        }
                    }
                    else {
                        throw new DataAccessException("Something went wrong.");
                    }
                }
            }
            if (!inCollection) {
                throw new DataAccessException("Not valid?");
            }
        } catch (DataAccessException e) {
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
