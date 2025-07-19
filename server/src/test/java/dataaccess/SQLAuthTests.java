package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthTests {

    @Test
    void createValidAuthTest() {
        try {
            Server server = new Server();
            server.db.clear();

            String username = "";
            UserData user = new UserData("use", "pass", "em");
            server.db.userDataDAO.insertUser(user);
            String authToken = server.db.authDataDAO.createAuth("use");
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username FROM auth WHERE authToken=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, authToken);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            username = rs.getString("username");
                        }
                    }
                }
            } catch (Exception e) {
                fail();
            }
            assertEquals(username, user.username());
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void invalidAuthTest() throws DataAccessException{
        Server server = new Server();
        server.db.clear();

        assertThrows(DataAccessException.class, () -> server.db.authDataDAO.createAuth(null));
    }

    @Test
    void validGetAuthTest() {
        try {
            Server server = new Server();
            server.db.clear();

            UserData user = new UserData("use", "pass", "em");
            server.db.userDataDAO.insertUser(user);
            String authToken = server.db.authDataDAO.createAuth("use");
            String newAuthToken = "";
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, authToken);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            newAuthToken = rs.getString("authToken");
                        }
                    }
                }
            } catch (Exception e) {
                fail();
            }
            assertEquals(newAuthToken, authToken);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void invalidGetAuthTest() {
        try {
            Server server = new Server();
            server.db.clear();

            UserData user = new UserData("use", "pass", "em");
            server.db.userDataDAO.insertUser(user);
            String fakeAuthToken = "oogabooga";
            boolean didntMakeIt = true;
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, fakeAuthToken);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            didntMakeIt = false;
                        }
                    }
                }
            } catch (Exception e) {
                fail();
            }
            assertTrue(didntMakeIt);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void validDeleteAuthTest() {
        try {
            Server server = new Server();
            server.db.clear();
            boolean madeIt = true;
            UserData user = new UserData("use", "pass", "em");
            server.db.userDataDAO.insertUser(user);
            String authToken = server.db.authDataDAO.createAuth("use");
            server.db.authDataDAO.deleteAuth(new AuthData(authToken, "use"));
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, authToken);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            madeIt = false;
                        }
                    }
                }
            } catch (Exception e) {
                fail();
            }
            assertTrue(madeIt);
        } catch (DataAccessException e) {
            fail();
        }
    }

    @Test
    void invalidDeleteAuthTest() {
        try {
            Server server = new Server();
            server.db.clear();
            boolean madeIt = true;
            UserData user = new UserData("use", "pass", "em");
            server.db.userDataDAO.insertUser(user);
            String authToken = server.db.authDataDAO.createAuth("use");
            assertThrows(DataAccessException.class, () -> server.db.authDataDAO.deleteAuth(new AuthData("oogabooga", "use")));
            } catch (Exception e) {
                fail();
        }
    }

    @Test
    void clearDBTest() {
        try {
            Server server = new Server();
            server.db.clear();
            UserData user = new UserData("use", "pass", "em");
            server.db.userDataDAO.insertUser(user);
            String authToken = server.db.authDataDAO.createAuth("use");
            server.db.gameDataDAO.createGame("nameGame", authToken);
            server.db.clear();
            boolean madeItThrough = true;
            try (var conn = DatabaseManager.getConnection()) {
                Collection<String> statements = new ArrayList<>();
                statements.add("SELECT * FROM game");
                statements.add("SELECT * FROM user");
                statements.add("SELECT * FROM auth");
                for (String statement : statements) {
                    try (var ps = conn.prepareStatement(statement)) {
                        try (var rs = ps.executeQuery()) {

                            if (rs.next()) {
                                madeItThrough = false;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException("Error: bad request");
            }
            assertTrue(madeItThrough);
        } catch (DataAccessException e) {
            fail();
        }
    }
}
