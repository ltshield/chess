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
            throw new DataAccessException("You need a username.");
        }
        if (u.password() == null) {
            throw new DataAccessException("You need a password.");
        }
        if (u.email() == null) {
            throw new DataAccessException("You need an email");
        }
        users.add(u);
        if (!users.contains(u)) {
            throw new DataAccessException("Something went wrong adding the user");
        }
    }

    public void getUser(String username) throws DataAccessException{
        for (UserData user : users) {
            if (user.username().equals(username)) {
                throw new DataAccessException("Already Taken");
            }
        }
    }

    // idk if I actually need this one...
    public void deleteUser() throws DataAccessException{}
    public void clear(){users = new ArrayList<>();}

}
