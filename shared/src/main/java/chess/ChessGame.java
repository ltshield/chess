package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn = TeamColor.WHITE;
    public ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        // if no piece at position, return null
        if (board.getPiece(startPosition) == null) {
            return null;
        }

        // if valid and would not put king in check or checkmate
        // therefore, king cannot move into danger AND other pieces cannot move to where king would be in danger
        Collection<ChessMove> validOptions = board.getPiece(startPosition).pieceMoves(board, startPosition);
        // king cannot move into danger
        if (board.getPiece(startPosition).getPieceType() == ChessPiece.PieceType.KING) {
            Collection<ChessMove> toDelete = new ArrayList<>();
            ChessPiece query = board.getPiece(startPosition);
            for (ChessMove myMove : validOptions) {
                ChessBoard newBoard = copyBoard(board);
                Collection<ChessMove> oppMoves = checkFutureBoardState(newBoard, myMove, query);
                boolean inCheck = false;
                for (ChessMove move : oppMoves) {
                    ChessPosition endPos = move.getEndPosition();
                    if (myMove.getEndPosition().equals(endPos)) {
                        inCheck = true;
                    }
                }
                if (inCheck) {
                    toDelete.add(myMove);
                }
            }

            for (ChessMove move : toDelete) {
                validOptions.remove(move);
            }
            return validOptions;
        }

        Collection<ChessMove> toDelete = new ArrayList<>();
        ChessPiece query = board.getPiece(startPosition);
        if (isInCheck(query.getTeamColor())) {
            if (query.getTeamColor() == TeamColor.BLACK) {
                futureKingCheck(validOptions, toDelete, query);
            } else {
                futureKingCheck(validOptions, toDelete, query);
            }

            for (ChessMove finalMove : toDelete) {
                validOptions.remove(finalMove);
            }

            return validOptions;

        } else {
            // if other piece is in danger, and king would be in danger if it were to move, cannot move
            if (query.getTeamColor() == TeamColor.WHITE) {
                for (ChessMove enemyMove : board.getMovesFromOpponent(TeamColor.WHITE)) {
                    if (enemyMove.getEndPosition().equals(startPosition)) {
                        ChessPiece piece = board.getPiece(startPosition);
                        Collection<ChessMove> futureMoves = piece.pieceMoves(board, startPosition);
                        checkFutureMove(TeamColor.WHITE, futureMoves, query, toDelete);
                    }
                }
            } else {
                for (ChessMove enemyMove : board.getMovesFromOpponent(TeamColor.BLACK)) {
                    if (enemyMove.getEndPosition().equals(startPosition)) {
                        ChessPiece piece = board.getPiece(startPosition);
                        Collection<ChessMove> futureMoves = piece.pieceMoves(board, startPosition);
                        checkFutureMove(TeamColor.BLACK, futureMoves, query, toDelete);
                    }
                }
            }
            for (ChessMove mov : toDelete) {
                validOptions.remove(mov);
            }
            return validOptions;
        }
    }

    public void checkFutureMove(TeamColor color, Collection<ChessMove> futureMoves, ChessPiece query, Collection<ChessMove> toDelete) {
        for (ChessMove testMove : futureMoves) {
            ChessBoard newBoard = copyBoard(board);
            Collection<ChessMove> oppMoves = checkFutureBoardState(newBoard, testMove, query);
            ChessPosition kingPosition = newBoard.findKingPosition(color, newBoard);

            // opponent? Ie. in check cause of bishop, other bishop can still move towards bishop
            for (ChessMove move : oppMoves) {
                ChessPosition endPos = move.getEndPosition();
                if (kingPosition.equals(endPos)) {
                    toDelete.add(testMove);
                }
            }
        }
    }

    public void futureKingCheck(Collection<ChessMove> validOptions, Collection<ChessMove> toDelete, ChessPiece query) {
        for (ChessMove myMove : validOptions) {
            ChessBoard newBoard = copyBoard(board);
            Collection<ChessMove> oppMoves = checkFutureBoardState(newBoard, myMove, query);
            ChessPosition kingPosition = newBoard.findKingPosition(query.getTeamColor(), newBoard);
            boolean inCheck = false;
            for (ChessMove move : oppMoves) {
                ChessPosition endPos = move.getEndPosition();
                if (kingPosition.equals(endPos)) {
                    inCheck = true;
                }
            }
            if (inCheck) {
                toDelete.add(myMove);
            }
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // perform the move
        ChessPosition startPos = move.getStartPosition();
        if (board.getPiece(startPos) == null) {
            throw new InvalidMoveException();
        }
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPos);
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException();
        }
        if (validMoves(move.getStartPosition()).contains(move)) {
            TeamColor nextColor = TeamColor.BLACK;
            if (piece.getTeamColor() == TeamColor.BLACK) {
                nextColor = TeamColor.WHITE;
            }
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
                ChessPiece.PieceType promPiece = move.getPromotionPiece();
                piece = new ChessPiece(turn, promPiece);
            }
            board.grid[endPos.getRow()][endPos.getColumn()] = piece;
            board.grid[startPos.getRow()][startPos.getColumn()] = null;
            turn = nextColor;
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> oppMoves = board.getMovesFromOpponent(teamColor);
        ChessPosition kingPosition = board.findKingPosition(teamColor, board);
        for (ChessMove move : oppMoves) {
            ChessPosition endPos = move.getEndPosition();
            if (kingPosition.equals(endPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // if in check and cannot move king
        Collection<ChessMove> myMoves = new ArrayList<>();
        Collection<ChessPosition> myPositions = board.getColorPositions(teamColor);
        for (ChessPosition myPiecePosition : myPositions) {
            Collection<ChessMove> validMovesForPiece = validMoves(myPiecePosition);
            for (ChessMove move : validMovesForPiece) {
                myMoves.add(move);
            }
        }
        if (myMoves.isEmpty() && isInCheck(teamColor)) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // gonna reuse the opponent moves function
        TeamColor oppColor = null;
        if (teamColor == TeamColor.WHITE) {
            oppColor = TeamColor.BLACK;
        }
        if (teamColor == TeamColor.BLACK) {
            oppColor = TeamColor.WHITE;
        }
        if (oppColor != null) {
            Collection<ChessMove> myMoves = new ArrayList<>();
            Collection<ChessPosition> myPositions = board.getColorPositions(teamColor);
            for (ChessPosition myPiecePosition : myPositions) {
                Collection<ChessMove> validMovesForPiece = validMoves(myPiecePosition);
                for (ChessMove move : validMovesForPiece) {
                    myMoves.add(move);
                }
            }
            if (myMoves.isEmpty() && !isInCheck(teamColor)) {
                return true;
            } else {
                return false;
            }
        }
        throw new RuntimeException("Not a valid team color.");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public Collection<ChessMove> checkFutureBoardState(ChessBoard board, ChessMove move, ChessPiece query) {
        ChessBoard newBoard = board;
        int startRow = move.getStartPosition().getRow();
        int startCol = move.getStartPosition().getColumn();
        int endRow = move.getEndPosition().getRow();
        int endCol = move.getEndPosition().getColumn();

        newBoard.grid[startRow][startCol] = null;
        newBoard.grid[endRow][endCol] = query;

        return newBoard.getMovesFromOpponent(query.getTeamColor());
    }

    public ChessBoard copyBoard(ChessBoard board) {
        ChessBoard newBoard = new ChessBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (board.getPiece(new ChessPosition(i, j)) != null) {
                    ChessPosition newPos = new ChessPosition(i,j);
                    ChessPiece oldPiece = board.getPiece(newPos);
                    ChessPiece newPiece = new ChessPiece(oldPiece.getTeamColor(), oldPiece.getPieceType());
                    newBoard.grid[i][j] = newPiece;
                } else {
                    newBoard.grid[i][j] = null;
                }
            }
        }
        return newBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.deepEquals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
