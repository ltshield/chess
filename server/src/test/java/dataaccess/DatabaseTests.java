package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTests {
    @Test
    void createUserTableTest() {
        try {
            SQLDao sqlDao = new SQLDao();
            UserData user = new UserData("username", "pass", "gmail");
            sqlDao.addUser(user);
        } catch (DataAccessException e) {
            System.out.println("Whoops");
        }
    }
}
