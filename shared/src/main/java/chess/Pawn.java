package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

import static chess.ChessPiece.PieceType.QUEEN;
import static chess.ChessPiece.PieceType.ROOK;
import static chess.ChessPiece.PieceType.BISHOP;
import static chess.ChessPiece.PieceType.KNIGHT;

public class Pawn {
    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;
    private ArrayList<chess.ChessPiece.PieceType> promPieces = new ArrayList<chess.ChessPiece.PieceType>();

    public Pawn(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.moves = calculateMoves();
        this.promPieces.add(BISHOP);
        this.promPieces.add(QUEEN);
        this.promPieces.add(KNIGHT);
        this.promPieces.add(ROOK);
    }

    public Collection<ChessMove> calculateMoves() {
        // get piece color
        ChessPiece self = board.getPiece(position);

        board.getPieces();

        Collection<ChessPosition> taken;
        taken = board.positions;

        Collection<ChessMove> options = new ArrayList<ChessMove>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        boolean hasMoved = false;
        boolean canPromote = false;

        if (self.getTeamColor() == BLACK && position.getRow() != 7) {
            hasMoved = true;
        }

        if (self.getTeamColor() == WHITE && position.getRow() != 2) {
            hasMoved = true;
        }

        if (self.getTeamColor() == BLACK && position.getRow() == 2) {
            canPromote = true;
        }

        if (self.getTeamColor() == WHITE && position.getRow() == 7) {
            canPromote = true;
        }

        if (self.getTeamColor() == BLACK) {
            if (canPromote) {
                ChessPosition query = new ChessPosition(currRow-1, currCol);
                if (!taken.contains(query)) {
                    for (chess.ChessPiece.PieceType promPiece : promPieces) {
                        options.add(new ChessMove(position, query, promPiece));
                    }
                }
                // capture to right
                query = new ChessPosition(currRow-1, currCol-1);
                if (taken.contains(query) && board.getPiece(query).getTeamColor() == WHITE) {
                    for (chess.ChessPiece.PieceType promPiece : promPieces) {
                        options.add(new ChessMove(position, query, promPiece));
                    }
                }
                query = new ChessPosition(currRow-1, currCol+1);
                if (taken.contains(query) && board.getPiece(query).getTeamColor() == WHITE) {
                    for (chess.ChessPiece.PieceType promPiece : promPieces) {
                        options.add(new ChessMove(position, query, promPiece));
                    }
                }
            } else {
                if (self.getTeamColor() == BLACK) {
                    // can move two initially
                    if (hasMoved == false) {
                        ChessPosition firstSquare = new ChessPosition(currRow-1, currCol);
                        ChessPosition secondSquare = new ChessPosition(currRow-2, currCol);
                        if (!taken.contains(firstSquare)) {
                            if (!taken.contains(secondSquare)) {
                                options.add(new ChessMove(position, secondSquare, null));
                            }
                        }
                    }

                    // can move one
                    ChessPosition query = new ChessPosition(currRow-1, currCol);
                    if (!taken.contains(query)) {
                        options.add(new ChessMove(position, query, null));
                    }

                    // can capture diagonal to left
                    // TODO: if you capture into the enemy backline you can promote
                    query = new ChessPosition(currRow-1, currCol-1);
                    if (taken.contains(query)) {
                        ChessPiece enemy = board.getPiece(query);
                        if (enemy.getTeamColor() != self.getTeamColor()) {
                            options.add(new ChessMove(position, query, null));
                        }
                    }

                    // capture to the right
                    query = new ChessPosition(currRow-1, currCol+1);
                    if (taken.contains(query)) {
                        ChessPiece enemy = board.getPiece(query);
                        if (enemy.getTeamColor() != self.getTeamColor()) {
                            options.add(new ChessMove(position, query, null));
                        }
                    }
                }
            }
        }

        if (self.getTeamColor() == WHITE) {
// PROMOTION
            if (canPromote && self.getTeamColor() == WHITE) {
                ChessPosition query = new ChessPosition(currRow+1, currCol);
                if (!taken.contains(query)) {
                    for (chess.ChessPiece.PieceType promPiece : promPieces) {
                        options.add(new ChessMove(position, query, promPiece));
                    }
                }
                // capture to right
                query = new ChessPosition(currRow+1, currCol+1);
                if (taken.contains(query) && board.getPiece(query).getTeamColor() == BLACK) {
                    for (chess.ChessPiece.PieceType promPiece : promPieces) {
                        options.add(new ChessMove(position, query, promPiece));
                    }
                }
                query = new ChessPosition(currRow+1, currCol-1);
                if (taken.contains(query) && board.getPiece(query).getTeamColor() == BLACK) {
                    for (chess.ChessPiece.PieceType promPiece : promPieces) {
                        options.add(new ChessMove(position, query, promPiece));
                    }
                }
            } else {
                // can move two initially
                if (hasMoved == false && self.getTeamColor() == WHITE) {
                    ChessPosition firstSquare = new ChessPosition(currRow+1, currCol);
                    ChessPosition secondSquare = new ChessPosition(currRow+2, currCol);
                    if (!taken.contains(firstSquare)) {
                        if (!taken.contains(secondSquare)) {
                            options.add(new ChessMove(position, secondSquare, null));
                        }
                    }
                }

                // can move one
                ChessPosition query = new ChessPosition(currRow+1, currCol);
                if (!taken.contains(query)) {
                    options.add(new ChessMove(position, query, null));
                }

                if (self.getTeamColor() == WHITE) {
                    // can capture diagonal to left
                    // TODO: if you capture into the enemy backline you can promote
                    query = new ChessPosition(currRow+1, currCol-1);
                    if (taken.contains(query)) {
                        ChessPiece enemy = board.getPiece(query);
                        if (enemy.getTeamColor() != self.getTeamColor()) {
                            options.add(new ChessMove(position, query, null));
                        }
                    }

                    // capture to the right
                    query = new ChessPosition(currRow+1, currCol+1);
                    if (taken.contains(query)) {
                        ChessPiece enemy = board.getPiece(query);
                        if (enemy.getTeamColor() != self.getTeamColor()) {
                            options.add(new ChessMove(position, query, null));
                        }
                    }
                }
            }
        }

        System.out.println(self.getTeamColor());
        return options;
    }
}
