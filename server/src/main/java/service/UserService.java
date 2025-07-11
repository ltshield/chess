package service;

import dataaccess.DataAccessException;
import model.UserData;
import server.Server;

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
            // return 403 {"message": "Error: already taken"}
            throw e;
        }
        try {
            server.db.userDataDAO.insertUser(user);
        } catch (DataAccessException e) {
            // return 400 {"message": "Error: bad request"}
            throw e;
        }
        try {
            String authToken = server.db.authDataDAO.createAuth(user.username());
            return new RegisterResult(user.username(), authToken);
        } catch (DataAccessException e) {
            // return 400 {"message": "Error: bad request"}
            throw e;
        }
    }
//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
