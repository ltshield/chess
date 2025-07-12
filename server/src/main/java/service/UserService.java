package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.Server;
import spark.Request;

import javax.xml.crypto.Data;
import java.util.Collection;

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
//            String authToken = logoutRequest.authToken();
            AuthData authData = server.db.authDataDAO.getAuth(authToken);
            server.db.authDataDAO.deleteAuth(authData);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public void clear() {
//        try {
//            server.db.authDataDAO.getAuth(clearRequest.authToken());
            server.db.clear();
//        } catch (DataAccessException e) {
//            throw e;
//        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        try {
            return server.db.gameDataDAO.listGames(authToken);
        } catch (DataAccessException e){
            throw e;
        }
    }

    public CreateGameResponse createGame(Request gameRequest) throws DataAccessException {
        try {
            String authToken = gameRequest.headers("Authorization");
            var request = new Gson().fromJson(gameRequest.body(), CreateGameRequest.class);
            int result = server.db.gameDataDAO.createGame(request.gameName(), authToken);
            return new CreateGameResponse(result);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public void joinGame(Request gameRequest) throws DataAccessException {
        try {
            String authToken = gameRequest.headers("Authorization");
            var request = new Gson().fromJson(gameRequest.body(), JoinGameRequest.class);
            server.db.gameDataDAO.addUserToGame(authToken, request.gameID(), request.playerColor());
        } catch (DataAccessException e) {
            throw e;
        }
    }
}
