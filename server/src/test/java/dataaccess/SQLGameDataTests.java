package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDataTests {

    @Test
    public void posCreate() throws DataAccessException{
        Server server = new Server();
        server.db.clear();
        try {
            server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));
            String authToken = server.db.authDataDAO.createAuth("user");
            server.db.gameDataDAO.createGame("gameName", authToken);

            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT gameName FROM game WHERE gameName=?";
                assertTrue(posCreateHelper(conn, statement));
            } catch (Exception e) {
                fail();
            }

        } catch (DataAccessException e) {
            fail();
        }
    }

    public boolean posCreateHelper(java.sql.Connection conn, String statement) {
        boolean worked = false;
        try (var ps = conn.prepareStatement(statement)) {
            ps.setString(1, "gameName");
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    worked = true;
                }
            }
        }
        catch (Exception e) {
            return false;
        }
        return worked;
    }
    @Test
    public void negCreate() throws DataAccessException{
        Server server = new Server();
        server.db.clear();
        try {
            server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));
            assertThrows(DataAccessException.class, () -> server.db.gameDataDAO.createGame("gameName", "fakeAuthToken"));
        } catch (DataAccessException e) {
            fail();
        }
    }
    @Test
    public void posAdd() throws DataAccessException{
        Server server = new Server();
        server.db.clear();
        try {
            server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));
            String authToken = server.db.authDataDAO.createAuth("user");
            server.db.gameDataDAO.createGame("gameName", authToken);
            server.db.gameDataDAO.addUserToGame(authToken, 1, "WHITE");
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT whiteUsername FROM game WHERE gameName=?";
                assertTrue(posAddHelper(conn, statement));
            } catch (Exception e) {
                fail();
            }
        } catch (DataAccessException e) {
            fail();
        }
    }

    public boolean posAddHelper(java.sql.Connection conn, String statement) {
        boolean worked = false;
        try (var ps = conn.prepareStatement(statement)) {
            ps.setString(1, "gameName");
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    worked = rs.getString("whiteUsername").equals("user");
                }
            }
        }
        catch (Exception e) {
            return false;
        }
        return worked;
    }
    @Test
    public void negAdd() throws DataAccessException{
        Server server = new Server();
        server.db.clear();
        try {
            server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));
            String authToken = server.db.authDataDAO.createAuth("user");
            server.db.gameDataDAO.createGame("gameName", authToken);
            server.db.gameDataDAO.addUserToGame(authToken, 1, "WHITE");
            assertThrows(DataAccessException.class, () -> server.db.gameDataDAO.addUserToGame(authToken, 1, "WHITE"));
        } catch (DataAccessException e) {
            fail();
        }
    }
    @Test
    public void posList() throws DataAccessException {
        Server server = new Server();
        server.db.clear();
        try {
            server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));
            String authToken = server.db.authDataDAO.createAuth("user");
            server.db.gameDataDAO.createGame("gameName", authToken);
            server.db.gameDataDAO.addUserToGame(authToken, 1, "WHITE");
            Collection<GameData> collGames = server.db.gameDataDAO.listGames(authToken);
            Collection<GameData> expected = new ArrayList<>();
            expected.add(new GameData(1, "user", null, "gameName", new ChessGame()));
            assertEquals(collGames, expected);
        } catch (DataAccessException e) {
            fail();
        }
    }
    @Test
    public void negList() throws DataAccessException {
        Server server = new Server();
        server.db.clear();
        try {
            server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));
            String authToken = server.db.authDataDAO.createAuth("user");
            server.db.gameDataDAO.createGame("gameName", authToken);
            server.db.gameDataDAO.addUserToGame(authToken, 1, "WHITE");
            server.db.gameDataDAO.addUserToGame(authToken, 1, "BLACK");
            assertThrows(DataAccessException.class, () -> server.db.gameDataDAO.listGames("fakeAuth"));
        } catch (DataAccessException e) {
            fail();
        }
    }
}
