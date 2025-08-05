package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import websocket.messages.*;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void add(String username, Integer gameID, Session session) {
        // checks if already exists
        var connection = new Connection(username, session);
        Set<Connection> setOfConnections = new HashSet<Connection>();
        if (connections.containsKey(gameID)) {
            connections.get(gameID).add(connection);
        }
        else {
            connections.put(gameID, setOfConnections);
            connections.get(gameID).add(connection);
        }
    }

    public void remove(String visitorName, Integer gameID) {
        Set<Connection> listOfConnectionsInGame = connections.get(gameID);
        Set<Connection> toDelete = new HashSet<>();
        for (Connection conn : listOfConnectionsInGame) {
            if (conn.username.equals(visitorName)) {
                toDelete.add(conn);
            }
        }
        for (Connection conn : toDelete) {
            connections.get(gameID).remove(conn);
        }
    }

    public void broadcast(String excludeVisitorName, ServerMessage notification, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();

        Set<Connection> listOfConnectionsInGame = connections.get(gameID);
        for (var c : listOfConnectionsInGame) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeVisitorName)) {
                    if (notification.serverMessageType.equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        c.send(new Gson().toJson(notification));
                    }
//                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c);
        }
    }
}