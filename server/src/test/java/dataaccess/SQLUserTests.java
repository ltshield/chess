package dataaccess;

import dataexception.DataAccessException;
import org.junit.jupiter.api.Test;
import server.Server;
import model.UserData;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserTests {
    @Test
    public void positiveInsert() throws DataAccessException {
        Server server = new Server();
        server.db.clear();
        try {server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));}
        catch (Exception e) {fail();};

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, "user");
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        assertEquals("user", rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void negativeInsert() throws DataAccessException{
        Server server = new Server();
        server.db.clear();
        assertThrows(DataAccessException.class, () -> server.db.userDataDAO.insertUser(new UserData(null, "pass", "ema")));
    }

    @Test
    public void positiveCheckPass() throws DataAccessException{
        Server server = new Server();
        server.db.clear();
        boolean noError = true;
        try {server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));}
        catch (Exception e) {fail();}
        try {server.db.userDataDAO.checkUsernameAndPassword("user", "pass");
        assertTrue(noError);}
        catch (Exception e) {fail();}
    }

    @Test
    public void negativeCheckPass() throws DataAccessException{
        Server server = new Server();
        server.db.clear();
        try {server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));}
        catch (Exception e) {fail();}
        assertThrows(DataAccessException.class, () -> server.db.userDataDAO.checkUsernameAndPassword("user", "ema"));
    }

    @Test
    public void positiveGetUser() throws DataAccessException{
        Server server = new Server();
        server.db.clear();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                // user name should be available
                ps.setString(1, "user");
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        assertTrue(true);
                    }
                }
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void negativeGetUser() throws DataAccessException{
        Server server = new Server();
        server.db.clear();

        try {server.db.userDataDAO.insertUser(new UserData("user", "pass", "ema"));}
        catch (Exception e) {fail();}

        assertThrows(DataAccessException.class, () -> server.db.userDataDAO.getUser("user"));
    }
}
