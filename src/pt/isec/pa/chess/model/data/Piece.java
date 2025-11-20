package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.ChessGame;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public abstract class Piece implements Serializable {

    @Serial
    static final long serialVersionUID = 100L;

    private int row;
    private char column,symbol;
    private boolean hasMoved;
    private boolean isWhite;


    public Piece (char column,int row,char symbol, boolean isWhite){
        this.row = row;
        this.column = column;
        this.hasMoved = false;
        this.symbol = symbol;
        this.isWhite = isWhite;
    }

    public abstract String[] getPossibleMoves (ChessGame game);

    public void move(int newRow, char newColumn){
        this.row = newRow;
        this.column = newColumn;
        this.hasMoved = true;

    }

    @Override
    public String toString(){
        String position = "" + symbol + column + row;
        if (!hasMoved && (symbol == 'K' || symbol == 'k' || symbol == 'R' || symbol == 'r')){
            position = position + "*";
        }
        return position;
    }

    public char getSymbol() {
        return symbol;
    }

    public char getColumn (){
        return column;
    }

    public int getRow () {
        return row;
    }

    public boolean getHasMoved () {
        return hasMoved;
    }

    public void setHasMoved(boolean newHasMoved) {
        this.hasMoved = newHasMoved;
    }

    public void setRow(int newRow) {
        this.row = newRow;
    }

    public void setColumn(char newColumn) {
        this.column = newColumn;
    }

    public void setSymbol(char newSymbol) {
        this.symbol = newSymbol;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public boolean isValidPosition(char column, int row) {
        return column >= 'a' && column <= 'h' && row >= 1 && row <= 8;
    }

    public void addLinearMoves (List<String> moves, Board board){
        int dx [] = {-1,0,1,0};
        int dy [] = {0,-1,0,1};

        for (int i = 0; i < dx.length; i++){
            char newColumn = column;
            int newRow = row;

            while (true){
                newColumn =(char) (newColumn + dx[i]);
                newRow = newRow + dy[i];

                if (!isValidPosition(newColumn,newRow)){
                    break;
                }

                ///verifica pecas aliadas
                Piece piece = board.getPiece(newColumn, newRow);
                if (piece != null) { // se ha uma peca nessa posicao
                    if (piece.getIsWhite() != this.getIsWhite()) { // se for inimiga
                        moves.add("" + newColumn + newRow); // captura possível (logo movimento possivel)
                    }
                    break;
                }
                moves.add("" + newColumn + newRow);
            }
        }
    }

    public void addDiagonalMoves(List <String> moves, Board board){
        int [] dx = {1,1,-1,-1};
        int [] dy = {1,-1,1,-1};

        for (int i = 0; i < dx.length; i++){
            char newColumn = column;
            int newRow = row;

            while (true) {
                newColumn = (char) (newColumn + dx[i]);
                newRow += dy[i];

                if (!isValidPosition(newColumn, newRow)) break;



                ///verifica pecas aliadas
                Piece piece = board.getPiece(newColumn, newRow);
                if (piece != null) { // se ha uma peca nessa posicao
                    if (piece.getIsWhite() != this.getIsWhite()) { // se for inimiga
                        moves.add("" + newColumn + newRow); // captura possível (logo movimento possivel)
                    }
                    break;
                }
                moves.add("" + newColumn + newRow);
            }
        }

    }

}
