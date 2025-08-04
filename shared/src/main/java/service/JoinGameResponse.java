package service;
import chess.ChessGame;

public record JoinGameResponse (int GameID, ChessGame game){
}
