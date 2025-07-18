package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import server.Server;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameData {

    public Server server;
    public SQLGameData(Server server) {this.server = server;}

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
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

    public GameData createGame(String gameName) throws DataAccessException{
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        var json = new Gson().toJson(game);
        try {
            int id = executeUpdate(statement, null, null, gameName, json);
            return new GameData(id, null, null, gameName, game);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public void deleteGame(String authToken, Integer id) throws DataAccessException {
        if (authToken == null || id == null) {throw new DataAccessException("Error: bad request");}
        try {
//            AuthData existingAuth = server.db.getAuth(authToken);
            var statement = "DELETE FROM auth WHERE authToken=?";
            executeUpdate(statement, authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: not authorized");
        }
    }

    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

}
