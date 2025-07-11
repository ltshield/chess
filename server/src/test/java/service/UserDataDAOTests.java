package service;

import dataaccess.DataAccessException;
import dataaccess.UserDataDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataDAOTests {

    @Test
    void addSingleUserTest() {
        UserDataDAO userDataDAO = new UserDataDAO();
        UserData user = new UserData("username", "password", "email");

        try {
            userDataDAO.getUser(user.username());
            userDataDAO.insertUser(user);
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
        assertTrue(userDataDAO.users.contains(user));
    }

    @Test
    void addRedundantUserTest() {
        UserDataDAO userDataDAO = new UserDataDAO();
        UserData user = new UserData("username", "password", "email");
        userDataDAO.users.add(user);

        assertThrows(DataAccessException.class, () -> userDataDAO.getUser(user.username()));
    }

    @Test
    void clearTest() {
        UserDataDAO userDataDAO = new UserDataDAO();
        UserData user = new UserData("username", "password", "email");
        userDataDAO.users.add(user);
        System.out.println(userDataDAO.users);

        userDataDAO.clear();

        System.out.println(userDataDAO.users);
        assertTrue(userDataDAO.users.isEmpty());
    }
}
