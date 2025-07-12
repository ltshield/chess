package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class AuthDataDAO{

    public Collection<AuthData> currentUsers = new ArrayList<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    //CRUD
    public String createAuth(String username) throws DataAccessException{
        // TODO: if already logged in cannot log in again?
        for (AuthData user : currentUsers) {
            if (user.username().equals(username)) {
                throw new DataAccessException("Error: bad request");
            }
        }
        AuthData newAuth = new AuthData(generateToken(), username);
        currentUsers.add(newAuth);
        if (!currentUsers.contains(newAuth)) {
            throw new DataAccessException("Something went wrong making new auth.");
        }
        return newAuth.authToken();
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        for (AuthData data : currentUsers) {
            if (data.authToken().equals(authToken)) {
                return data;
            }
        }
        throw new DataAccessException("Error: not authorized");
    }

    public void deleteAuth(AuthData data) throws DataAccessException{
        boolean deleted = false;
        for (AuthData query : currentUsers) {
            if (query.authToken().equals(data.authToken())) {
                currentUsers.remove(data);
                deleted=true;
                break;
            }
        }
        if (deleted) {
            ;
        } else {
            throw new DataAccessException("Error: something went wrong");
        }
    }

    public void clear(){currentUsers = new ArrayList<>();}
}
