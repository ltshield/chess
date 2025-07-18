package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
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

    public UserData addUser(String username, String password, String email) throws DataAccessException{
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, username, hashedPassword, email);
        } catch (DataAccessException e) {
            throw e;
        }
        return new UserData(username, password, email);
    }

    public boolean checkUsernameAndPassword(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
//                ps.setString(2, password);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var hashedPassword = rs.getString("password");
                        System.out.println(password);
                        System.out.println(hashedPassword);
                        return BCrypt.checkpw(password, hashedPassword);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Whoops.");
        }
        return false;
    }

//    private UserData readUser(ResultSet rs) throws SQLException {
//        var username = rs.getString("username");
//        var password = rs.getString("password");
//        var email = rs.getString("email");
//        return pet.setId(id);
}
