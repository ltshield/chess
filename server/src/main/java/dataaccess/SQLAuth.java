package dataaccess;

import DataAccessException.DataAccessException;
import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class SQLAuth extends SQLBase {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String createAuth(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Error: bad request.");
        }
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
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    public void deleteAuth(AuthData authData) throws DataAccessException {
        if (authData.authToken() == null) {throw new DataAccessException("Error: bad request");}
        try {
            AuthData existingAuth = getAuth(authData.authToken());
            var statement = "DELETE FROM auth WHERE authToken=?";
            executeUpdate(statement, authData.authToken());
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: not authorized");
        }
    }

    public void deleteAllAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

}
