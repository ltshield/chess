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

    public void checkUsernameAndPassword(String username, String password) throws DataAccessException{
        if (username == null || password == null) {
            throw new DataAccessException("Error: bad request");
        }
        boolean validUsername = false;
        System.out.println(users);
        for (UserData user : users) {
            if (user.username().equals(username)) {
//                UserData query = user;
                validUsername = true;
                if (!user.password().equals(password)) {
                    System.out.println("here");
                    throw new DataAccessException("Error: unauthorized");
                }
                break;
            }
        }
//        System.out.println(query);
        if (!validUsername) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    // idk if I actually need this one...
    public void deleteUser() throws DataAccessException{}
    public void clear(){users = new ArrayList<>();}

}
