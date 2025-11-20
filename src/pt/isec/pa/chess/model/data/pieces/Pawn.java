package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{

    public Pawn(char column, int row, boolean isWhite) {
        super(column, row, isWhite? 'P' : 'p', isWhite);
    }


    @Override
    public String[] getPossibleMoves(ChessGame game) {
        List<String> moves = new ArrayList<>();
        int direction = (getIsWhite() ? 1 : -1);
        Board board = game.getBoard();

        char col = getColumn();
        int row = getRow();

        // 1. Movimento normal (1 casa à frente)
        int nextRow = row + direction;
        if (isValidPosition(col, nextRow) && board.getPiece(col, nextRow) == null) {
            moves.add("" + col + nextRow);

            // 2. Movimento inicial (2 casas à frente) — só se a primeira casa também estiver vazia
            int twoStepsRow = row + 2 * direction;
            if (!getHasMoved()
                    && isValidPosition(col, twoStepsRow)
                    && board.getPiece(col, twoStepsRow) == null) {

                // Verifica também se a casa entre está livre
                if (board.getPiece(col, nextRow) == null) {
                    moves.add("" + col + twoStepsRow);
                }
            }
        }

        // 3. Captura diagonal esquerda
        char leftDiagonal = (char) (col - 1);
        if (isValidPosition(leftDiagonal, nextRow)) {
            Piece leftPiece = board.getPiece(leftDiagonal, nextRow);
            if (leftPiece != null && leftPiece.getIsWhite() != this.getIsWhite()) {
                moves.add("" + leftDiagonal + nextRow);
            }
        }

        // 4. Captura diagonal direita
        char rightDiagonal = (char) (col + 1);
        if (isValidPosition(rightDiagonal, nextRow)) {
            Piece rightPiece = board.getPiece(rightDiagonal, nextRow);
            if (rightPiece != null && rightPiece.getIsWhite() != this.getIsWhite()) {
                moves.add("" + rightDiagonal + nextRow);
            }
        }
        // en Passant
        String enPassant = game.getEnPassantTarget();
        if (enPassant != null) {
            char targetCol = enPassant.charAt(0);
            int targetRow = Character.getNumericValue(enPassant.charAt(1));

            // Verifica se o alvo en passant está na linha correta e colunas adjacentes
            if (targetRow == row + direction && Math.abs(targetCol - col) == 1) {
                moves.add("" + targetCol + targetRow);
            }
        }

        return moves.toArray(new String[0]);
    }

}
