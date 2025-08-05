package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import dataexception.*;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    if (notification.serverMessageType.equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        NotificationMessage notifi = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notifi);
//                        System.out.println(notifi.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            throw new DataAccessException("Error: whoops");
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterGameClient(String authToken, Integer gameID) throws DataAccessException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void makeMoveClient(String authToken, Integer gameID) throws DataAccessException {

    }

    public void leaveGameClient(String authToken, Integer gameID) throws DataAccessException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void send(String message) throws DataAccessException {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            throw new DataAccessException("Error");
        }
    }

}