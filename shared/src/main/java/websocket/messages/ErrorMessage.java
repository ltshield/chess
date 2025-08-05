package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String mess) {
        super(ServerMessageType.ERROR);
        errorMessage = mess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
