package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.Server;
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

    public void clear() {
            server.db.clear();
    }

//    public ListGamesResponse listGames(String authToken) throws DataAccessException {
//        try {
//            Collection<GameData> games = server.db.gameDataDAO.listGames(authToken);
//            Collection<ListGameResponse> gamesList = new ArrayList<>();
//            for (GameData game : games) {
//                gamesList.add(new ListGameResponse(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
//            }
//            ListGamesResponse gameList = new ListGamesResponse(gamesList);
//            for (ListGameResponse resp : gamesList) {
//                System.out.println(resp);
//            }
//            return gameList;
//        } catch (DataAccessException e){
//            throw e;
//        }
//    }
//
//    public CreateGameResponse createGame(String authToken, CreateGameRequest request) throws DataAccessException {
//        try {
//            int result = server.db.gameDataDAO.createGame(request.gameName(), authToken);
//            return new CreateGameResponse(result);
//        } catch (DataAccessException e) {
//            throw e;
//        }
//    }
//
//    public void joinGame(String authToken, JoinGameRequest gameRequest) throws DataAccessException {
//        try {
//            server.db.gameDataDAO.addUserToGame(authToken, gameRequest.gameID(), gameRequest.playerColor());
//        } catch (DataAccessException e) {
//            System.out.println("HERE I AM");
//            throw e;
//        }
//    }
}
