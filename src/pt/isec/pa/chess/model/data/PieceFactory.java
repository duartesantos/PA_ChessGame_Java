package pt.isec.pa.chess.model.data;
import pt.isec.pa.chess.model.data.pieces.*;

public class PieceFactory {
    //criar peças pelo enum
    public static Piece create (PieceType type, char column, int row, boolean isWhite,boolean hasMoved){
        Piece piece = switch (type){
            case KING -> new King (column,row,isWhite);
            case QUEEN -> new Queen(column,row,isWhite);
            case BISHOP -> new Bishop (column,row,isWhite);
            case ROOK -> new Rook (column,row,isWhite);
            case PAWN -> new Pawn (column,row,isWhite);
            case KNIGHT -> new Knight (column,row,isWhite);
        };
        piece.setHasMoved(hasMoved);
        return piece;
    }

    //criar peças apartir de texto
    public static Piece fromText (String text){
        if (text == null || text.length() < 3){
            return null;
        }
        char symbol = text.charAt(0);
        char column = text.charAt(1);
        int row = Character.getNumericValue(text.charAt(2));

        boolean isWhite = Character.isUpperCase(symbol);
        PieceType type = PieceType.fromPieceSymbol(symbol);
        boolean hasMoved =!text.endsWith("*") && (type == PieceType.KING || type == PieceType.ROOK);

        Piece piece = create(type,column,row,isWhite,hasMoved);

        if (text.endsWith("*")){
            piece.setHasMoved(false);
        }
        return piece;
    }
}
