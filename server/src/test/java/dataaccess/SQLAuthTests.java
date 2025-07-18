package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.fail;

public class SQLAuthTests {

    @Test
    void clearDBTest() {
        try {
            Server server = new Server();
            UserData user = new UserData("use", "pass", "em");
            server.db.userDataDAO.insertUser(user);
            String authToken = server.db.authDataDAO.createAuth("use");
            server.db.gameDataDAO.createGame("nameGame", authToken);
            server.db.clear();
        } catch (DataAccessException e) {
            fail();
        }
    }

}
