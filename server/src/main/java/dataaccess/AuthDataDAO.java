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
        for (AuthData query : currentUsers) {
            if (query.authToken().equals(data.authToken())) {
                currentUsers.remove(data);
                break;
            }
        }
        for (AuthData query : currentUsers) {
            if (query.authToken().equals(data.authToken())) {
                throw new DataAccessException("Still here?");
            }
        }
    }

    public void clear(){currentUsers = new ArrayList<>();}
}
