package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;

    public LoadGameMessage(ChessGame gam) {
        super(ServerMessageType.LOAD_GAME);
        game = gam;
    }

    public ChessGame getGame() {
        return game;
    }
}
