package server.websocket;

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

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException  {
        System.out.println(message);
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            AuthData authData = getUsername(command.getAuthToken());
            String username = authData.username();

            // get playercolor the same way

            Integer gameID = command.gameID;

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, gameID);
//                case MAKE_MOVE -> makeMove(session, username);
                case LEAVE -> leaveGame(username, gameID);
//                case RESIGN -> resign(session, username);
            }
        } catch (Exception e) {
            sendMessage(session, new DataAccessException("Error: unauthorized"));
        }
    }

    private void leaveGame(String username, Integer gameID) throws DataAccessException {
        connectionManager.remove(username, gameID);
        String message = String.format("%s has left the game!", username);
        var notification = new NotificationMessage(message);
        try {
            connectionManager.broadcast(username, notification, gameID);
        } catch (Exception e) {
            throw new DataAccessException("Error: broadcasting went wrong.");
        }
    }
    private void connect(Session session, String username, Integer gameID) throws DataAccessException {
        connectionManager.add(username, gameID, session);
        String message = String.format("%s has joined the game!", username);
        var notification = new NotificationMessage(message);
        try {
            connectionManager.broadcast(username, notification, gameID);
        } catch (Exception e) {
            throw new DataAccessException("Error: broadcasting went wrong.");
        }
    }

    private void sendMessage(Session session, DataAccessException e) throws DataAccessException {
        try {
            session.getRemote().sendString(e.getMessage());
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

//    public void saveSession(String visitorName, Integer gameID, Session session) {
//        Connection connection = new Connection(visitorName, session);
//        connectionManager.add(visitorName, gameID, session);
//    }

}
