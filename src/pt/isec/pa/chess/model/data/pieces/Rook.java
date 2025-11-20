package pt.isec.pa.chess.model.data.pieces;
import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Rook extends Piece {

    public Rook(char column, int row, boolean isWhite) {
        super(column, row, isWhite ? 'R' : 'r', isWhite);
    }

    @Override
    public String[] getPossibleMoves(ChessGame game) {
        List<String> moves = new ArrayList<>();
        addLinearMoves(moves, game.getBoard()); //addLinearMoves() - ja tem a verificacao de pecas aliadas
        return moves.toArray(new String [0]);
    }

    @Override
    public String toString() {
        return (getIsWhite() ? "R" : "r") + getColumn() + getRow();
    }

}
