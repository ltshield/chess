package service;

import dataaccess.DataAccessException;

import model.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @Test
    void registerTest() {
        Server server = new Server();
        RegisterRequest req = new RegisterRequest("username", "password", "email");
        UserData user = new UserData("username", "password", "email");
        UserService userService = new UserService(server);

        try {
            userService.register(req);
            assertTrue(server.db.userDataDAO.users.contains(user));
        } catch (DataAccessException e) {
            System.out.println("Uhoh");
        }
    }

    @Test
    void registerWithoutUsername() {
        Server server = new Server();
        RegisterRequest req = new RegisterRequest(null, "password", "email");
        UserService userService = new UserService(server);

        assertThrows(DataAccessException.class, () -> userService.register(req));
    }

}
