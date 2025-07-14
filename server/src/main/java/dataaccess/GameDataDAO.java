package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import server.Server;

import java.util.ArrayList;
import java.util.Collection;

public class GameDataDAO{

    public Server server;
    public Collection<GameData> currentGameData = new ArrayList<>();

    private int id = 1;

    public GameDataDAO(Server server) {
        this.server = server;
    }

    //CRUD
    public int createGame(String gameName, String authToken) throws DataAccessException{
        try {
            server.db.authDataDAO.getAuth(authToken);
            if (gameName == null) {
                throw new DataAccessException("Error: bad request");
            }
            for (GameData game : currentGameData) {
                if (game.gameName().equals(gameName)) {
                    throw new DataAccessException("Error: game name taken");
                }
            }

            GameData newGame = new GameData(id, null, null, gameName, new ChessGame());
            id++;
            currentGameData.add(newGame);
            return newGame.gameID();
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException{
        try {
            server.db.authDataDAO.getAuth(authToken);
            return currentGameData;
        } catch (DataAccessException e){
            throw e;
        }
    }

    public void addUserToGame(String authToken, int gameID, String playerColor) throws DataAccessException{
        try {
            Collection<String> availableColors = new ArrayList<>();
            availableColors.add("WHITE");
            availableColors.add("BLACK");
            AuthData user = server.db.authDataDAO.getAuth(authToken);
            // cannot join more than one game at a time?
            if (playerColor == null || !availableColors.contains(playerColor)) {
                throw new DataAccessException("Error: bad request");
            }
            boolean inCollection = false;
            for (GameData gameTest : currentGameData) {
                if (gameTest.gameID() == gameID) {
                    inCollection = true;
                    int ogID = gameTest.gameID();
                    String ogWhite = gameTest.whiteUsername();
                    String ogBlack = gameTest.blackUsername();
                    String ogName = gameTest.gameName();
                    ChessGame ogGame = gameTest.game();
                    if (playerColor.equals("WHITE")) {
                        if (gameTest.whiteUsername() == null) {
                                currentGameData.remove(gameTest);
                                currentGameData.add(new GameData(ogID, user.username(), ogBlack, ogName, ogGame));
                                break;
                            }
                            else {
                                throw new DataAccessException("Error: already taken");
                            }
                    }
                    if (playerColor.equals("BLACK")) {
                        if (gameTest.blackUsername() == null) {
                                currentGameData.remove(gameTest);
                                currentGameData.add(new GameData(ogID, ogWhite, user.username(), ogName, ogGame));
                                break;
                            } else {
                                throw new DataAccessException("Error: already taken");
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
