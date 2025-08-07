package service;

import chess.ChessGame;
import dataexception.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.Server;
import service.beforelogin.LoginRequest;
import service.beforelogin.LoginResult;
import service.beforelogin.RegisterRequest;
import service.beforelogin.RegisterResult;

import java.util.*;

public class UserService {
    public final Server server;
    public UserService(Server server) {
        this.server = server;
    }
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{

        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        try {
            server.db.userDataDAO.getUser(user.username());
        } catch (DataAccessException e) {
            throw e;
        }
        try {
            server.db.userDataDAO.insertUser(user);
        } catch (DataAccessException e) {
            throw e;
        }
        // are they supposed to be logged in immediately after registering?
        try {
            String authToken = server.db.authDataDAO.createAuth(user.username());
            return new RegisterResult(user.username(), authToken);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        try {
            server.db.userDataDAO.checkUsernameAndPassword(loginRequest.username(), loginRequest.password());
            String authToken = server.db.authDataDAO.createAuth(loginRequest.username());
            return new LoginResult(loginRequest.username(), authToken);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public void logout(String authToken) throws DataAccessException{
        try {
            AuthData authData = server.db.authDataDAO.getAuth(authToken);
            server.db.authDataDAO.deleteAuth(authData);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public void clear() throws DataAccessException{
            server.db.clear();
    }

    public ChessGame getGameBoard(int gameID) throws DataAccessException {
        return server.db.gameDataDAO.getGameBoard(gameID);
    }

    public ListGamesResponse listGames(String authToken) throws DataAccessException {
        try {
            Collection<GameData> games = server.db.gameDataDAO.listGames(authToken);
            Collection<ListGameResponse> gamesList = new ArrayList<>();
            for (GameData game : games) {
                gamesList.add(new ListGameResponse(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            ListGamesResponse gameList = new ListGamesResponse(gamesList);
            return gameList;
        } catch (DataAccessException e){
            throw e;
        }
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException {
        try {
            int result = server.db.gameDataDAO.createGame(request.gameName(), request.authToken());
            return new CreateGameResponse(result);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public UpdateGameResponse updateGame(UpdateGameRequest gameRequest) throws DataAccessException {
        server.db.gameDataDAO.updateGameBoard(gameRequest.gameID(), gameRequest.game(), gameRequest.username(), gameRequest.playerColor());
        return new UpdateGameResponse(gameRequest.game());
    }

    public JoinGameResponse joinGame(JoinGameRequest gameRequest) throws DataAccessException {
        try {
            if (server.db.gameDataDAO.checkIfInGame(gameRequest.authToken(), gameRequest.gameID(), gameRequest.playerColor())) {
                return new JoinGameResponse(gameRequest.gameID(), getGameBoard(gameRequest.gameID()));
            } else {
                server.db.gameDataDAO.addUserToGame(gameRequest.authToken(), gameRequest.gameID(), gameRequest.playerColor());
                return new JoinGameResponse(gameRequest.gameID(), getGameBoard(gameRequest.gameID()));
            }
        } catch (DataAccessException e) {
            if (gameRequest.playerColor() != null && gameRequest.playerColor().equals("OBSERVING")) {
                return new JoinGameResponse(gameRequest.gameID(), getGameBoard(gameRequest.gameID()));
            }
            throw e;
        }
    }
}
