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
import java.sql.SQLOutput;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    ConcurrentHashMap<Integer, Session> games = new ConcurrentHashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException  {
        System.out.println(message);
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            AuthData authData = getUsername(command.getAuthToken());
            String username = authData.username();
            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
//                case MAKE_MOVE -> makeMove(session, username, command);
//                case LEAVE -> leaveGame(session, username, command);
//                case RESIGN -> resign(session, username, command);
            }
        } catch (Exception e) {
            sendMessage(session, new DataAccessException("Error: unauthorized"));
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

    public void connect(Session session, String username, UserGameCommand command) {
        connections.add(username, session);
        System.out.println("Made it to connect method");
        try {
            session.getRemote().sendString("Done");
        } catch (Exception e) {
            System.out.println("Whoops");
        }
    }

    public void saveSession(Integer gameID, Session session) {
        games.put(gameID, session);
    }

}
