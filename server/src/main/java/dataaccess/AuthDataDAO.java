package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class AuthDataDAO extends DataAccessObject {

    private Collection<AuthData> currentUsers = new ArrayList<>();

    //CRUD
    void createAuth(AuthData authData) throws DataAccessException{}

    void getAuth() throws DataAccessException{}

    void deleteAuth() throws DataAccessException{}

    void clear(){currentUsers = new ArrayList<>();}
}
