package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import dataaccess.DatabaseManager;
import dataexception.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import websocket.messages.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.*;

import java.sql.SQLException;
import java.util.Collection;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException  {
        System.out.println(message);
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            if (command.commandType.equals(UserGameCommand.CommandType.MAKE_MOVE)) {
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                AuthData authData = getUsername(makeMoveCommand.getAuthToken());
                String username = authData.username();
                boolean legal = true;

                // check it is that player's turn
                Integer gameID = command.gameID;
                String playerColor = getPlayerColor(username, gameID, authData.authToken());
                if (!rightTurn(playerColor, gameID)) {
                    legal = false;
                    sendErrorMessage(session, new DataAccessException("Error: not your turn."));
                    return;
                }
                if (inCheckmateOrStalemate(playerColor, gameID)) {
                    legal = false;
                    sendErrorMessage(session, new DataAccessException("Error: game is over."));
                    return;
                }
                // check if it is valid
                if (!validMove(gameID, makeMoveCommand.move)) {
                    legal = false;
                    sendErrorMessage(session, new DataAccessException("Error: not valid move."));
                    return;
                }
                // check if they are moving their own piece
                if (legal == true) {
                    // then make the move
                    makeMove(username, makeMoveCommand.gameID, makeMoveCommand.move);
                }
            }
            else {
                AuthData authData = getUsername(command.getAuthToken());
                String username = authData.username();

                // get playercolor the same way

                Integer gameID = command.gameID;
                String playerColor = getPlayerColor(username, gameID, authData.authToken());

                int i = getNumGames(authData.authToken(), gameID);
                if (command.gameID > i) {
                    sendErrorMessage(session, new DataAccessException("Error: not a valid ID."));
                }
                else {
                    switch (command.getCommandType()) {
                        case CONNECT -> connect(session, username, gameID, playerColor);
                        case LEAVE -> leaveGame(username, gameID, playerColor);
                        case RESIGN -> resign(session, username, gameID, playerColor);
                    }
                }
            }
        } catch (Exception e) {
            sendErrorMessage(session, new DataAccessException("Error: unauthorized"));
        }
    }

    private void resign(Session session, String username, int gameID, String authToken) throws DataAccessException {
        if (isResigned(gameID)) {
            sendErrorMessage(session, new DataAccessException("Error: game is already resigned."));
            return;
        }
        // if username not in the valid usernames for the game, cannot resign
        if (getPlayerColor(username, gameID, authToken).equals("OBSERVER")) {
            sendErrorMessage(session, new DataAccessException("Error: observers cannot resign."));
        }
        else {
            String message = String.format("%s has resigned. The game is now over.", username);
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT game FROM game WHERE id=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, gameID);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ChessGame gem = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                            gem.resigned = true;
                            try (var conn2 = DatabaseManager.getConnection()) {
                                String statement2 = "UPDATE game SET game=? WHERE id=?";
                                try (var ps2 = conn2.prepareStatement(statement2)) {
                                    var json = new Gson().toJson(gem);
                                    ps2.setString(1, json);
                                    ps2.setInt(2, gameID);
                                    ps2.executeUpdate();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new DataAccessException("Error: updating game");
            }
            NotificationMessage notification = new NotificationMessage(message);
            try {
                connectionManager.broadcast(username, notification, gameID);
                connectionManager.sendMessage(username, notification, gameID);
            } catch (Exception e) {
                throw new DataAccessException("Error: broadcasting went wrong.");
            }
        }
    }
    private boolean isResigned(int gameID) {
        boolean resigned = false;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame gem = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        if (gem.resigned == true) {
                            resigned = true;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            return false;
        }
        return resigned;
    }

    private boolean inCheckmateOrStalemate(String playerColor, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame gem = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        ChessGame.TeamColor turnColor = gem.getTeamTurn();
                        if (playerColor.equals("WHITE")) {
                            if (gem.isInCheckmate(turnColor) || gem.isInStalemate(turnColor)) {
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                        if (playerColor.equals("BLACK")) {
                            if (gem.isInStalemate(turnColor) || gem.isInStalemate(turnColor)) {
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                    }
                    else {
                        throw new DataAccessException("Error: not authorized");
                    }
                }
            }
            return false;
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    private boolean validMove(int gameID, ChessMove move) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame gem = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        if (gem.resigned) {
                            return false;
                        }
                        if (gem.ended) {
                            return false;
                        }
                        Collection<ChessMove> valMoves = gem.validMoves(move.getStartPosition());
                        if (valMoves.contains(move)) {
                            return true;
                        }
                        return false;
                    }
                }
            }
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
        return false;
    }

    private boolean rightTurn(String playerColor, int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame gem = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        ChessGame.TeamColor turnColor = gem.getTeamTurn();
                        ChessGame.TeamColor userColorWhenMadeMove = null;
                        if (playerColor.equals("WHITE")) {
                            userColorWhenMadeMove = ChessGame.TeamColor.BLACK;
                            if (userColorWhenMadeMove.equals(turnColor)) {
                                return false;
                            }
                            return true;
                        }
                        if (playerColor.equals("BLACK")) {
                            userColorWhenMadeMove = ChessGame.TeamColor.WHITE;
                            if (userColorWhenMadeMove.equals(turnColor)) {
                                return false;
                            }
                            return true;
                        }
                        if (playerColor.equals("OBSERVER")) {
                            return false;
                        }
                    }
                    else {
                        throw new DataAccessException("Error: not authorized");
                    }
                }
                return true;
            }
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
    }

    private void makeMove(String username, Integer gameID, ChessMove move) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String gameString = rs.getString("game");
                        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                        game.makeMove(move);
                        String jsonString = new Gson().toJson(game);
                        try (var conn2 = DatabaseManager.getConnection()) {
                            String statement2 = "UPDATE game SET game=? WHERE id=?";
                            try (var ps2 = conn2.prepareStatement(statement2)) {
                                ps2.setString(1, jsonString);
                                ps2.setInt(2, gameID);
                                ps2.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: updating game");
        }

        String message = String.format("%s has moved %s.", username, move);
        NotificationMessage notification = new NotificationMessage(message);

        try {
            connectionManager.broadcast(username, notification, gameID);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameID);
            connectionManager.broadcast(username, loadGameMessage, gameID);
            connectionManager.sendMessage(username, loadGameMessage, gameID);

        } catch (Exception e) {
            throw new DataAccessException("Error: broadcasting went wrong.");
        }
    }

    private void leaveGame(String username, Integer gameID, String playerColor) throws DataAccessException {
        connectionManager.remove(username, gameID);
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        if (playerColor.equals("WHITE") && whiteUsername != null && whiteUsername.equals(username)) {
                            try (var conn2 = DatabaseManager.getConnection()) {
                                String statement2 = "UPDATE game SET whiteUsername=? WHERE id=?";
                                try (var ps2 = conn2.prepareStatement(statement2)) {
                                    ps2.setString(1, null);
                                    ps2.setInt(2, gameID);
                                    ps2.executeUpdate();
                                }
                            }
                        }
                        if (playerColor.equals("BLACK") && blackUsername != null && blackUsername.equals(username)) {
                            try (var conn2 = DatabaseManager.getConnection()) {
                                String statement2 = "UPDATE game SET blackUsername=? WHERE id=?";
                                try (var ps2 = conn2.prepareStatement(statement2)) {
                                    ps2.setString(1, null);
                                    ps2.setInt(2, gameID);
                                    ps2.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: updating game");
        }
        String message = String.format("%s has left the game!", username);
        var notification = new NotificationMessage(message);
        try {
            connectionManager.broadcast(username, notification, gameID);
        } catch (Exception e) {
            throw new DataAccessException("Error: broadcasting went wrong.");
        }
    }
    private void connect(Session session, String username, Integer gameID, String playerColor) throws DataAccessException {
        connectionManager.add(username, gameID, session);
        String message = String.format("%s has joined the game as %s!", username, playerColor);
        var notification = new NotificationMessage(message);
        try {
            connectionManager.broadcast(username, notification, gameID);
            var loadGameMessage = new LoadGameMessage(gameID);
            connectionManager.sendMessage(username, loadGameMessage, gameID);
        } catch (Exception e) {
            throw new DataAccessException("Error: broadcasting went wrong.");
        }
    }

    private void sendErrorMessage(Session session, DataAccessException e) throws DataAccessException {
        try {
            ErrorMessage err = new ErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(err));
        } catch (Exception apple) {
            throw new DataAccessException("Error: something went wrong");
        }
    }

    public AuthData getUsername(String authToken) throws DataAccessException {
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

    public String getPlayerColor(String username, int gameID, String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername FROM game WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getString("whiteUsername") != null && rs.getString("whiteUsername").equals(username)) {
                            return "WHITE";
                        }
                        if (rs.getString("blackUsername") != null && rs.getString("blackUsername").equals(username)) {
                            return "BLACK";
                        }
                        else {
                            return "OBSERVER";
                        }
                    }
                }
            }
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
        throw new DataAccessException("Something went wrong grabbing player color.");
    }

    public int getNumGames(String authToken, int gameID) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: bad request");
        }
        // should this be set to 1? or 0?
        int i = 0;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game";
            try (var ps = conn.prepareStatement(statement)) {
//                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        i++;
                    }
                }
            }
        } catch (DataAccessException e) {
            throw e;
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal error");
        }
        return i;
    }

}
