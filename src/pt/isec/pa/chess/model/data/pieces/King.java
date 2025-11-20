package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
    public King (char column,int row, boolean isWhite){
        super(column,row,isWhite? 'K' : 'k',isWhite);
    }


    @Override
    public String[] getPossibleMoves(ChessGame game) {
        List<String> moves = new ArrayList<>();
        int[] dx = {-1, 0, 1, 1, 1, 0, -1, -1};
        int[] dy = {-1, -1, -1, 0, 1, 1, 1, 0};

        for (int i = 0; i < dx.length; i++) {
            char newColumn = (char) (getColumn() + dx[i]);
            int newRow = getRow() + dy[i];

            if (isValidPosition(newColumn, newRow)) {
                Piece piece = game.getBoard().getPiece(newColumn, newRow);

                // Se a casa estiver vazia ou tiver uma peça inimiga
                if (piece == null || piece.getIsWhite() != this.getIsWhite()) {
                    // Rei não pode mover para uma casa atacada
                    if (!game.isUnderAttack(newColumn, newRow, !this.getIsWhite())) {
                        moves.add("" + newColumn + newRow);
                    }
                }
            }
        }

        // Roque (Castling)
        if (!getHasMoved() && !game.isUnderAttack(getColumn(), getRow(), !this.getIsWhite())) {
            if (game.canCastle(this, 'g')) moves.add("g" + getRow());  // Roque pequeno
            if (game.canCastle(this, 'c')) moves.add("c" + getRow());  // Roque grande
        }

        return moves.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return (getIsWhite() ? "K" : "k") + getColumn() + getRow();
    }

}
