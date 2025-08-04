package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String message;

    public ErrorMessage(String mess) {
        super(ServerMessageType.ERROR);
        message = mess;
    }

    public String getErrorMessage() {
        return message;
    }
}
