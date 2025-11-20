package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{

    public Knight(char column, int row, boolean isWhite) {
        super(column, row, isWhite? 'N' : 'n', isWhite);
    }

    @Override
    public String[] getPossibleMoves(ChessGame game) {
        List<String> moves = new ArrayList<>();

        int[] dx = {-2, -2, -1, 1, 2, 2, 1, -1};
        int[] dy = {-1, 1, 2, 2, 1, -1, -2, -2};

        for (int i = 0; i < dx.length; i++){
            char newColumn = (char) (getColumn() + dx[i]);
            int newRow = getRow() + dy[i];
            if (isValidPosition(newColumn,newRow)){
                Piece piece = game.getBoard().getPiece(newColumn, newRow);
                if (piece == null || piece.getIsWhite() != this.getIsWhite()) {
                    moves.add("" + newColumn + newRow);
                }
            }

        }
        return moves.toArray(new String [0]);
    }


}
