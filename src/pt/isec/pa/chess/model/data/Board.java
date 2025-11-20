package pt.isec.pa.chess.model.data;

import  pt.isec.pa.chess.model.data.pieces.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board implements Serializable {
    @Serial
    static final long serialVersionUID = 100L;
    private Map<String,Piece> board;

    public Board(){
        board = new HashMap<>();
        setupBoard ();
    }

    public void setupBoard() {

        // Peças pretas
        addPiece(new Rook('a', 8, false));
        addPiece(new Knight('b', 8, false));
        addPiece(new Bishop('c', 8, false));
        addPiece(new Queen('d', 8, false));
        addPiece(new King('e', 8, false));
        addPiece(new Bishop('f', 8, false));
        addPiece(new Knight('g', 8, false));
        addPiece(new Rook('h', 8, false));

        // Peões pretos
        for (char c = 'a'; c <= 'h'; c++) {
            addPiece(new Pawn(c, 7, false));
        }

        // Peças brancas
        addPiece(new Rook('a', 1, true));
        addPiece(new Knight('b', 1, true));
        addPiece(new Bishop('c', 1, true));
        addPiece(new Queen('d', 1, true));
        addPiece(new King('e', 1, true));
        addPiece(new Bishop('f', 1, true));
        addPiece(new Knight('g', 1, true));
        addPiece(new Rook('h', 1, true));

        // Peões brancos
        for (char c = 'a'; c <= 'h'; c++) {
            addPiece(new Pawn(c, 2, true));
        }
        
    }
    private String getPositionKey(char column, int row) {
        return "" + column + row;
    }

    public void addPiece(Piece newPiece){
        String position = "" + newPiece.getColumn() + newPiece.getRow();
        board.put(position,newPiece);
    }

    public void removePiece(char column, int row){
        board.remove (getPositionKey(column, row));
    }


    public Piece getPiece(char column, int row) {
        return board.get("" + column + row); //retorna null se nao houver
    }

    public void setPiece(Piece piece, char column, int row) {
        if (piece != null) {
            removePiece(column, row); // Remove qualquer peça existente na posição
            piece.setColumn(column);
            piece.setRow(row);
            board.put(getPositionKey(column, row), piece);
        }
    }

    public void movePiece(char colFrom, int rowFrom, char colTo, int rowTo) {
        Piece piece = getPiece(colFrom, rowFrom);
        // Remove a peça da posição inicial e coloca na nova posição
        if (piece == null){
            return;
        }
        removePiece(colFrom, rowFrom);
        setPiece(piece, colTo, rowTo);
        piece.setHasMoved(true);
        piece.setColumn(colTo);
        piece.setRow(rowTo);
    }

    public String getAllPiecesOnBoard() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            Piece piece = entry.getValue();
            String position = entry.getKey();

            if (piece != null) {
                sb.append(piece.getSymbol());
                sb.append(position);
                sb.append(",");
            }
        }

        if (sb.length() > 0)
            sb.setLength(sb.length() - 1); // Remove a última vírgula

        return sb.toString();
    }

    public void clear (){
        board.clear();
    }

    public int getBoardSize() {
        return 8;
    }

}
