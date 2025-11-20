package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(char column, int row, boolean isWhite) {

        super(column, row, isWhite? 'B' : 'b', isWhite);
    }

    @Override
    public String[] getPossibleMoves(ChessGame game) {
        List<String> moves = new ArrayList<>();
        addDiagonalMoves(moves, game.getBoard());
        return moves.toArray(new String [0]);
    }

    @Override
    public String toString() {
        return (getIsWhite() ? "B" : "b") + getColumn() + getRow();
    }


}
