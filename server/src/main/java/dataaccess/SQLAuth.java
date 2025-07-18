package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLAuth {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public String createAuth(String username) throws DataAccessException{
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        String authToken = generateToken();
        try {
            executeUpdate(statement, authToken, username);
        } catch (DataAccessException e) {
            throw e;
        }
        return authToken;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(authToken, rs.getString("username"));
                    }
                    else {
                        throw new DataAccessException("Error: not authorized");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: bad request");
        }
    }

    public void deleteAuth(AuthData authData) throws DataAccessException {
        if (authData.authToken() == null) {throw new DataAccessException("Error: bad request");}
        try {
            AuthData existingAuth = getAuth(authData.authToken());
            var statement = "DELETE FROM auth WHERE authToken=?";
            executeUpdate(statement, authData.authToken());
        } catch (Exception e) {
            throw new DataAccessException("Error: not authorized");
        }
    }

    public void deleteAllAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

}
