package dataaccess;

import dataexception.DataAccessException;
import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import server.Server;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameData extends SQLBase {

    public Server server;
    public SQLGameData(Server server) {this.server = server;}

    @Override
    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {ps.setString(i + 1, p);}
                    else if (param instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (param instanceof ChessGame p) {ps.setString(i + 1, p.toString());}
                    else if (param == null) {ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    public ChessGame getGameBoard(int gameID) throws DataAccessException {
        ChessGame actualGame = null;
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT game FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String game = rs.getString("game");
                        actualGame = new Gson().fromJson(game, ChessGame.class);
                    }
                    else {
                        throw new DataAccessException("Error: bad request");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: bad request");
        }
        return actualGame;
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
            throw e;
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
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
        return games;
    }

    public void update(ResultSet rs, String playerColor, Connection conn, int gameID) throws DataAccessException {
        try {
            if (rs.next()) {
                String whiteUser = rs.getString("whiteUsername");
                String blackUser = rs.getString("blackUsername");
                if (whiteUser != null && playerColor.equals("WHITE")) {
                    String statement2 = "UPDATE game SET whiteUsername=? WHERE id=?";
                    try (var ps2 = conn.prepareStatement(statement2)) {
                        ps2.setString(1, null);
                        ps2.setInt(2, gameID);
                        ps2.executeUpdate();
                    }
                }
                if (blackUser != null && playerColor.equals("BLACK")) {
                    String statement2 = "UPDATE game SET blackUsername=? WHERE id=?";
                    try (var ps2 = conn.prepareStatement(statement2)) {
                        ps2.setString(1, null);
                        ps2.setInt(2, gameID);
                        ps2.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Whoops");
        }
    }
    public void updateGameBoard(Integer gameID, ChessGame game, String username, String playerColor) throws DataAccessException {
        if (username==null) {
            try (var conn = DatabaseManager.getConnection()) {
                String statement = "SELECT whiteUsername, blackUsername FROM game WHERE id=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, gameID);
                    ps.executeQuery();
                    try (var rs = ps.executeQuery()) {
                        update(rs, playerColor, conn, gameID);
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException("Error: couldn't update game board.");
            }
        }

        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE game SET game=? WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                var json = new Gson().toJson(game);
                ps.setString(1, json);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: couldn't update game board.");
        }
    }

    public boolean checkIfInGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        try {
            Collection<String> availableColors = new ArrayList<>();
            availableColors.add("WHITE");
            availableColors.add("BLACK");
            if (playerColor == null || !availableColors.contains(playerColor)) {
                throw new DataAccessException("Error: bad request");
            }

            AuthData user = null;
            try {
                user = server.db.authDataDAO.getAuth(authToken);
            } catch(DataAccessException e) {
                throw e;
            }

            boolean finalStatement = false;
            try (var conn = DatabaseManager.getConnection()) {
                finalStatement = checkHelperFunc(conn, gameID, playerColor, user.username());
            } catch (Exception e) {
                throw e;
            }
            return finalStatement;
        } catch (DataAccessException e) {
            throw e;
        }
        catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    public boolean checkHelperFunc(Connection conn, int gameID, String playerColor, String username) throws DataAccessException{
        String statement = "SELECT whiteUsername, blackUsername FROM game WHERE id=?";
        try (var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String whiteUser = rs.getString("whiteUsername");
                    String blackUser = rs.getString("blackUsername");
                    if (playerColor.equals("WHITE") && Objects.equals(whiteUser, username)) {
                        return true;
                    }
                    else if (playerColor.equals("BLACK") && Objects.equals(blackUser, username)) {
                        return true;
                    }
                }
                else {
                    throw new DataAccessException("Error: bad request");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: bad request");
        }
        return false;
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
                throw e;
            }

            // cannot join more than one game at a time?
            if (playerColor == null || !availableColors.contains(playerColor)) {
                throw new DataAccessException("Error: bad request");
            }

            var finalStatement = "";
            try (var conn = DatabaseManager.getConnection()) {
                finalStatement = addHelperFunc(conn, gameID, playerColor);
            } catch (Exception e) {
                throw e;
            }
            executeUpdate(finalStatement, user.username(), gameID);
            } catch (DataAccessException e) {
                throw e;
        }
        catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    public String addHelperFunc(Connection conn, int gameID, String playerColor) throws DataAccessException{
        String statement = "SELECT whiteUsername, blackUsername FROM game WHERE id=?";
        String finalStatement;
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
                else {
                    throw new DataAccessException("Error: bad request");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: bad request");
        }
        return finalStatement;
    }

    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

}
