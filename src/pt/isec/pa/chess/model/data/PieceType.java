package pt.isec.pa.chess.model.data;

public enum PieceType {
    KING,QUEEN,BISHOP,ROOK,PAWN,KNIGHT;

    public static PieceType fromPieceSymbol (char c){
        return switch (Character.toLowerCase(c)){
            case 'k' -> KING;
            case 'q' -> QUEEN;
            case 'b' -> BISHOP;
            case 'r' -> ROOK;
            case 'p' -> PAWN;
            case 'n' -> KNIGHT;
            default -> throw new IllegalArgumentException ("Tipo da peça inválido: " + c);
        };
    }

    public char toSymbol (boolean isWhite) {
        char symbol = switch (this) {
            case KING -> 'k';
            case QUEEN -> 'q';
            case ROOK -> 'r';
            case BISHOP -> 'b';
            case KNIGHT -> 'n';
            case PAWN -> 'p';
        };
        return isWhite ? Character.toUpperCase(symbol) : symbol;
    }
}
