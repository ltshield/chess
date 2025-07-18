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
            UserData user = new UserData("username", "pass", "gmail");
//            sqlDao.addUser(user);
        } catch (DataAccessException e) {
            System.out.println("Whoops");
        }
    }

    @Test
    void addAuthTableTest() {
        try {
            SQLDao sqlDao = new SQLDao();
            AuthData user = new AuthData("jejejejeje", "user");
            sqlDao.sqlAuth.addAuth(user);
        } catch (DataAccessException e) {
            System.out.println("Whoops");
        }
    }

    @Test
    void addGameTableTest() {
        try {
            SQLDao sqlDao = new SQLDao();
            GameData user = new GameData(1, null,null, "name", new ChessGame());
            sqlDao.sqlGameData.createGame(user);
            System.out.println("Here");
        } catch (DataAccessException e) {
            System.out.println("Whoops");
        }
    }

}
