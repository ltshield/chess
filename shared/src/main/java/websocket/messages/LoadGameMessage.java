package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private final int game;

    public LoadGameMessage(Integer gam) {
        super(ServerMessageType.LOAD_GAME);
        game = gam;
    }

    public Integer getGameID() {
        return game;
    }
}
