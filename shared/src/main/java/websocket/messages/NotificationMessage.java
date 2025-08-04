package websocket.messages;

public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String mess) {
        super(ServerMessageType.NOTIFICATION);
        message = mess;
    }

    public String getMessage() {
        return message;
    }
}
