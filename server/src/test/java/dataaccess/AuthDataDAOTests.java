//package dataaccess;
//
//import dataaccess.DataAccessException;
//import model.AuthData;
//import org.junit.jupiter.api.Test;
//import server.Server;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class AuthDataDAOTests {
//
//    @Test
//    void createAuthTest() {
//        Server server = new Server();
//        String username = "username";
//        try {
//            String authToken = server.db.authDataDAO.createAuth(username);
//            AuthData query = new AuthData(authToken, username);
//            assertTrue(server.db.authDataDAO.currentUsers.contains(query));
//        } catch (DataAccessException e) {
//            System.out.println("Uhoh");
//        }
//    }
//
//    @Test
//    void getAuthTest() {
//        Server server = new Server();
//        String username = "username";
//        try {
//            String authToken = server.db.authDataDAO.createAuth(username);
//            AuthData returned = server.db.authDataDAO.getAuth(authToken);
//            AuthData expected = new AuthData(authToken, username);
//            assertEquals(returned, expected);
//        } catch (DataAccessException e) {
//            System.out.println("Uhoh");
//        }
//    }
//
//    @Test
//    void deleteAuthTest() {
//        Server server = new Server();
//        String username = "username";
//        try {
//            String authToken = server.db.authDataDAO.createAuth(username);
//            AuthData returned = server.db.authDataDAO.getAuth(authToken);
//            System.out.println(server.db.authDataDAO.currentUsers);
//            server.db.authDataDAO.currentUsers.remove(returned);
//            System.out.println(server.db.authDataDAO.currentUsers);
//            assertTrue(server.db.authDataDAO.currentUsers.isEmpty());
//        } catch (DataAccessException e) {
//            System.out.println("Uhoh");
//        }
//    }
//    @Test
//    void clearTest() {
//        Server server = new Server();
//        server.db.authDataDAO.currentUsers.add(new AuthData("123", "username"));
//        server.db.authDataDAO.currentUsers.add(new AuthData("1334", "username2"));
//        System.out.println(server.db.authDataDAO.currentUsers);
//        server.db.authDataDAO.clear();
//        System.out.println(server.db.authDataDAO.currentUsers);
//        assertTrue(server.db.authDataDAO.currentUsers.isEmpty());
//    }
//}
