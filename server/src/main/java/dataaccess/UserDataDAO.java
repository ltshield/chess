package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class UserDataDAO extends DataAccessObject{

    private Collection<AuthData> users = new ArrayList<>();

    //CRUD
    void insertUser(UserData u) throws DataAccessException{}

    void getUser() throws DataAccessException{}

    void deleteUser() throws DataAccessException{}
    void clear(){users = new ArrayList<>();}
}
