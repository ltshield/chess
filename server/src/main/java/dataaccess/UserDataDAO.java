package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class UserDataDAO{

    public Collection<UserData> users = new ArrayList<>();

    //CRUD
    public void insertUser(UserData u) throws DataAccessException{
        if (u.username() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (u.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (u.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        users.add(u);
        if (!users.contains(u)) {
            throw new DataAccessException("Error: Unable to add the user.");
        }
    }

    public void getUser(String username) throws DataAccessException{
        for (UserData user : users) {
            if (user.username().equals(username)) {
                throw new DataAccessException("Error: already taken");
            }
        }
    }

    // idk if I actually need this one...
    public void deleteUser() throws DataAccessException{}
    public void clear(){users = new ArrayList<>();}

}
