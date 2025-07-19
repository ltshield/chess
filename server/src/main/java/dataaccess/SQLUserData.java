package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserData extends SQLBase {

    public void getUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        throw new DataAccessException("Error: already taken");
                    }
                }
            }
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    public UserData insertUser(UserData user) throws DataAccessException{
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, user.username(), hashedPassword, user.email());
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Error: already taken");
            }
            throw new DataAccessException("Error: internal error");
        }
        return new UserData(user.username(), user.password(), user.email());
    }

    public void checkUsernameAndPassword(String username, String password) throws DataAccessException {
        if (username == null || password == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var hashedPassword = rs.getString("password");
                        if(!BCrypt.checkpw(password, hashedPassword)) {
                            throw new DataAccessException("Error: unauthorized");
                        }
                    }
                    else {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

}
