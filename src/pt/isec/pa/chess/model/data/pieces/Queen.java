package pt.isec.pa.chess.model.data.pieces;
import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece{

    public Queen(char column, int row, boolean isWhite) {
        super(column, row, isWhite ? 'Q' : 'q', isWhite);
    }
    @Override
    public String[] getPossibleMoves(ChessGame game) {
        List<String> moves = new ArrayList<>();
        addDiagonalMoves(moves, game.getBoard());
        addLinearMoves(moves, game.getBoard());
        return moves.toArray(new String [0]);
    }

}