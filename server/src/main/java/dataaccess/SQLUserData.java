package dataaccess;

import model.UserData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUserData {

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

    public UserData addUser(UserData user) throws DataAccessException{
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, user.username(), user.password(), user.email());
        } catch (DataAccessException e) {
            throw e;
        }
        return new UserData(user.username(), user.password(), user.email());
    }
}
