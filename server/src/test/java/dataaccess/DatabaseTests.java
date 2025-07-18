package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
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
//            sqlDao.sqlUserData.addUser("user5", "pase", "email");
            assertTrue(sqlDao.sqlUserData.checkUsernameAndPassword("user5", "pase"));
        } catch (DataAccessException e) {
            System.out.println("Whoops");
        }
    }

    @Test
    void addAuthTableTest() {
        try {
            SQLDao sqlDao = new SQLDao();
            sqlDao.sqlAuth.addAuth("username");
        } catch (DataAccessException e) {
            System.out.println("Whoops");
        }
    }

    @Test
    void addGameTableTest() {
        try {
            SQLDao sqlDao = new SQLDao();
            sqlDao.sqlGameData.createGame("NameGame");
            System.out.println("Here");
        } catch (DataAccessException e) {
            System.out.println("Whoops");
        }
    }

}
