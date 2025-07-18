package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DatabaseTests {
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
