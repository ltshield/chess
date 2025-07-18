package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import server.Server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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

    public int createGame(String gameName, String authToken) throws DataAccessException{
        if (gameName == null || authToken == null) {
            throw new DataAccessException("Error: bad request");
        }
        AuthData currUser = null;
        try {
            currUser = server.db.authDataDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw e;
        }
        if (currUser == null) {
            throw new DataAccessException("Error: not authorized");
        }
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        var json = new Gson().toJson(game);
        try {
            return executeUpdate(statement, null, null, gameName, json);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        try {
            AuthData currUser = server.db.authDataDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: not authorized");
        }
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("id");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String game = rs.getString("game");
                        ChessGame newGame = new Gson().fromJson(game, ChessGame.class);
                        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, newGame));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: bad request");
        }
        return games;
    }

    public void addUserToGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        try {
            Collection<String> availableColors = new ArrayList<>();
            availableColors.add("WHITE");
            availableColors.add("BLACK");
            AuthData user = null;
            try {
                user = server.db.authDataDAO.getAuth(authToken);
            } catch(DataAccessException e) {
                throw new DataAccessException("Error: not authorized");
            }

            // cannot join more than one game at a time?
            if (playerColor == null || !availableColors.contains(playerColor)) {
                throw new DataAccessException("Error: bad request");
            }

            var finalStatement = "";
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT whiteUsername, blackUsername FROM game WHERE id=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, gameID);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String whiteUser = rs.getString("whiteUsername");
                            String blackUser = rs.getString("blackUsername");
                            if (playerColor.equals("WHITE") && whiteUser == null) {
                                finalStatement = "UPDATE game SET whiteUsername=? WHERE id=?";
                            }
                            else if (playerColor.equals("BLACK") && blackUser == null) {
                                finalStatement = "UPDATE game SET blackUsername=? WHERE id=?";
                            } else {throw new DataAccessException("Error: already taken");}
                        }
                    }
                }
            } catch (Exception e) {
                throw e;
            }
            executeUpdate(finalStatement, user.username(), gameID);
            } catch (DataAccessException e) {
                throw e;
        }
        catch (SQLException e) {
            throw new DataAccessException("Error: bad request");
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
