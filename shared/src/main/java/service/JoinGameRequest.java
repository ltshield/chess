package service;

import chess.ChessGame;

public record JoinGameRequest (String authToken, String playerColor, int gameID){
}
