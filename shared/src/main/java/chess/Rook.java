package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook {
    private final ChessBoard board;
    private final ChessPosition position;
    public Collection<ChessMove> moves;

    public Rook(ChessBoard board, ChessPosition position) {
        super();
        this.board = board;
        this.position = position;
        this.moves = calculateMoves();
    }

    public Collection<ChessMove> calculateMoves() {
        ChessPiece self = board.getPiece(position);

        board.getPieces();

        Collection<ChessPosition> taken;
        taken = board.positions;
        System.out.println(taken);
        Collection<ChessMove> options = new ArrayList<>();

        int currRow = position.getRow();
        int currCol = position.getColumn();

        for(int i = currRow+1; i <= 8; i++){
            ChessPosition query = new ChessPosition(i,currCol);
            if(taken.contains(query)){
                ChessPiece affil = board.getPiece(query);
                if(affil.getTeamColor() == self.getTeamColor()){
                    break;
                }
                options.add(new ChessMove(position, query, null));
                break;
            }
            options.add(new ChessMove(position, query, null));
        }

        for(int i = currRow-1; i >= 1; i--){
            ChessPosition query = new ChessPosition(i,currCol);
            if(taken.contains(query)){
                ChessPiece affil = board.getPiece(query);
                if(affil.getTeamColor() == self.getTeamColor()){
                    break;
                }
                options.add(new ChessMove(position, query, null));
                break;
            }
            options.add(new ChessMove(position, query, null));
        }

        for(int j = currCol+1; j <= 8; j++){
            ChessPosition query = new ChessPosition(currRow,j);
            if(taken.contains(query)){
                ChessPiece affil = board.getPiece(query);
                if(affil.getTeamColor() == self.getTeamColor()){
                    break;
                }
                options.add(new ChessMove(position, query, null));
                break;
            }
            options.add(new ChessMove(position, query, null));
        }

        for(int j = currCol-1; j >= 1; j--){
            ChessPosition query = new ChessPosition(currRow,j);
            if(taken.contains(query)){
                ChessPiece affil = board.getPiece(query);
                if(affil.getTeamColor() == self.getTeamColor()){
                    break;
                }
                options.add(new ChessMove(position, query, null));
                break;
            }
            options.add(new ChessMove(position, query, null));
        }

        return options;
    }
}
