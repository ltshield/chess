package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
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
                    }
                    if (notification.serverMessageType.equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                        LoadGameMessage notifi = new Gson().fromJson(message, LoadGameMessage.class);
                        notificationHandler.notify(notifi);
                    }
                    if (notification.serverMessageType.equals(ServerMessage.ServerMessageType.ERROR)) {
                        ErrorMessage notifi = new Gson().fromJson(message, ErrorMessage.class);
                        notificationHandler.notify(notifi);
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

//    public void drawGame(String authToken, Integer gameID) throws DataAccessException {
//        try {
//            var action = new UserGameCommand(UserGameCommand.CommandType., authToken, gameID);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new DataAccessException(ex.getMessage());
//        }
//    }

    public void enterGameClient(String authToken, Integer gameID) throws DataAccessException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
//            throwErrorClient(ex.getMessage());
            System.out.println("I am in the enter game client.");
        }
    }

//    public void throwErrorClient(String errorMessage) {
//        try {
//            var error = new ErrorMessage(errorMessage);
//            this.session.getBasicRemote().sendText(new Gson().toJson(error));
//        } catch (Exception e) {
//            System.out.println("Whoops.");;
//        }
//    }

    public void makeMoveClient(String authToken, Integer gameID) throws DataAccessException {
        try {
            ;
        } catch (Exception e) {
            System.out.println("I am in the make move client.");
        }
    }

    public void leaveGameClient(String authToken, Integer gameID) throws DataAccessException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
//            throwErrorClient(ex.getMessage());
            System.out.println("I am in the leave game client.");
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