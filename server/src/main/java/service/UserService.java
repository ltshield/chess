package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import server.Server;

import javax.xml.crypto.Data;

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

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        try {
            String authToken = logoutRequest.authToken();
            AuthData authData = server.db.authDataDAO.getAuth(authToken);
            server.db.authDataDAO.deleteAuth(authData);
        } catch (DataAccessException e) {
            throw e;
        }
    }
}
