package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLDao {

    public SQLUserData sqlUserData = new SQLUserData();
    public SQLGameData sqlGameData = new SQLGameData();
    public SQLAuth sqlAuth = new SQLAuth();

    public SQLDao() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] createGameStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NULL,
              `blackUsername` varchar(256) NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] createAuthStatements = {
        """
        CREATE TABLE IF NOT EXISTS auth (
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256),
          PRIMARY KEY (`username`),
          INDEX(username),
          INDEX(authToken)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (var statement : createUserStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("Finished.");
            for (var statement : createGameStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("Finished.");
            for (var statement : createAuthStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("Finished.");
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
