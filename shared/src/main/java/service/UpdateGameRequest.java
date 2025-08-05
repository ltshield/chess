package service;
import chess.ChessGame;
public record UpdateGameRequest (String authToken, ChessGame game, int gameID) {
}
