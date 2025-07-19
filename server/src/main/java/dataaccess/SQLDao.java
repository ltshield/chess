package dataaccess;
import server.Server;
import java.sql.SQLException;

public class SQLDao {

    public Server server;
    public SQLUserData userDataDAO;
    public SQLGameData gameDataDAO;
    public SQLAuth authDataDAO;

    public SQLDao(Server server) throws DataAccessException {
        configureDatabase();
        this.server = server;
        this.userDataDAO = new SQLUserData();
        this.gameDataDAO = new SQLGameData(server);
        this.authDataDAO = new SQLAuth();
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
          PRIMARY KEY (`authToken`),
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
            for (var statement : createGameStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for (var statement : createAuthStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    public void clear() throws DataAccessException{
        try {
            userDataDAO.deleteAllUsers();
            authDataDAO.deleteAllAuth();
            gameDataDAO.deleteAllGames();
        } catch (Exception e) {
            throw new DataAccessException("Error: internal error");
        }
    }
}
