package pt.isec.pa.chess.model;
import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceFactory;
import pt.isec.pa.chess.model.data.PieceType;
import pt.isec.pa.chess.model.data.pieces.King;
import pt.isec.pa.chess.model.data.pieces.Pawn;

import java.io.Serial;
import java.io.Serializable;

/**
* Represents the logic of a chess game,
* including board state, who's turn is, game state,
* and logic of the special moves (promotion, en passant and castling).
* Implements Serializable to allow game state saving/loading.
*/

public class ChessGame implements Serializable {

    @Serial
    static final long serialVersionUID = 100L;

    private GameState gameState;
    private Board board;
    private boolean whiteToMove;
    private Winner winner; // "WHITE", "BLACK" ou "NONE"
    private String whitePlayerName;
    private String blackPlayerName;
    private String enPassantTarget;
    private String promotionPosition;
    private boolean waitingForPromotion;


    /**
     * Initializes a new game with default player names (White and Black).
     */
    public ChessGame() {
        this("White", "Black");
    }

    /**
     * Initializes a new game with player names choosen by the user.
     *
     * @param whitePlayerName Name of the white player.
     * @param blackPlayerName Name of the black player.
     */
    public ChessGame(String whitePlayerName, String blackPlayerName) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.board = new Board();
        this.whiteToMove = true;
        this.gameState = GameState.ONGOING;
        this.winner = Winner.NONE;
        this.enPassantTarget = null;
        ModelLog.getInstance().add("Novo Jogo iniciado: " + whitePlayerName + " (Brancas) VS " +blackPlayerName+ " (Pretas)");
    }

    /**
     *
     * @return the game state of the game.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Executes a move from one position to another, including
     * special moves like castling and en passant.
     *
     * @param from The origin position (for example "e2").
     * @param to The destination position (for example "e4").
     * @return True if the move is valid and executed.
     */
    public boolean move(String from, String to) {
        if (from.length() != 2 || to.length() != 2)
            return false;

        char colFrom = from.charAt(0);
        int rowFrom = Character.getNumericValue(from.charAt(1));
        char colTo = to.charAt(0);
        int rowTo = Character.getNumericValue(to.charAt(1));

        Piece piece = board.getPiece(colFrom, rowFrom);
        if (piece == null || piece.getIsWhite() != whiteToMove)
            return false;

        String destino = "" + colTo + rowTo;
        String[] movimentosValidos = piece.getPossibleMoves(this);
        boolean podeMover = false;

        for (String mov : movimentosValidos) {
            if (mov.equals(destino)) {
                podeMover = true;
                break;
            }
        }

        if (!podeMover) {
            ModelLog.getInstance().add("Movimento inválido de " + from+ " para " + to + ".");
            return false;
        }
/// ******************
        // Simular jogada e verificar se o rei continuaria em check (verificar se o rei continua em check antes de executar o movimento)
        Piece destinoAnterior = board.getPiece(colTo, rowTo); //guarda a peça que está na posicao de destino, caso haja
        board.removePiece(colFrom, rowFrom);
        board.setPiece(piece, colTo, rowTo);

        boolean aindaEmCheck = isInCheck(whiteToMove);

        // Reverter jogada
        board.removePiece(colTo, rowTo);
        board.setPiece(piece, colFrom, rowFrom);
        if (destinoAnterior != null) {
            board.setPiece(destinoAnterior, colTo, rowTo);
        }

        if (aindaEmCheck) {
            ModelLog.getInstance().add("Movimento inválido de " + from+ " para " + to + " pois o seu rei ficaria em check.");
            return false; // Movimento inválido pois deixaria o rei em check
        }
/// ******************

        // Roque: se for o rei e mover duas colunas
        if (piece instanceof King && Math.abs(colTo - colFrom) == 2) {
            if (colTo == 'g') {
                board.movePiece('h', rowFrom, 'f', rowFrom); // Roque pequeno
                ModelLog.getInstance().add("Roque pequeno realizado pelo rei em " + from);
            } else if (colTo == 'c') {
                board.movePiece('a', rowFrom, 'd', rowFrom); // Roque grande
                ModelLog.getInstance().add("Roque longo realizado pelo rei em " + from);
            }
        }

        if (piece instanceof Pawn && enPassantTarget != null && destino.equals(enPassantTarget)) {
            board.removePiece(colTo, rowFrom);
            ModelLog.getInstance().add("Captura en passant realizada em " + colTo + rowFrom);
        }

        board.movePiece(colFrom, rowFrom, colTo, rowTo);
        ModelLog.getInstance().add("Peça movida de "+from+" para " + to + ": " + piece.getSymbol());
        Piece movedPiece = board.getPiece(colTo, rowTo);
        if (movedPiece instanceof Pawn) {
            if ((movedPiece.getIsWhite() && rowTo == 8) || (!movedPiece.getIsWhite() && rowTo == 1)) {
                waitingForPromotion = true;
                promotionPosition = "" + colTo + rowTo;
               updateGameState();
               return true;
            }
        }

        if (movedPiece instanceof Pawn && Math.abs(rowFrom - rowTo) == 2) {
            int enPassantRow = whiteToMove ? rowTo - 1 : rowTo + 1;
            enPassantTarget = "" + colTo + enPassantRow;
        } else {
            enPassantTarget = null;
        }

        whiteToMove = !whiteToMove;
        updateGameState();
        return true;
    }

    /**
     * Updates the current state of the game (CHECK, CHECKMATE, STALEMATE or if it is ON GOING).
     */
    public void updateGameState() {
        boolean isCheck = isInCheck(whiteToMove);
        boolean hasMoves = hasLegalMoves(whiteToMove);

        if (isCheck && !hasMoves) {
            gameState = GameState.CHECKMATE;
            winner = whiteToMove ? Winner.BLACK : Winner.WHITE;
            ModelLog.getInstance().add("Checkmate. Vencedor: " + getWinnerName());
        } else if (!isCheck && !hasMoves) {
            gameState = GameState.STALEMATE;
            winner = Winner.NONE;
            ModelLog.getInstance().add("Empate (Stalemate).");
        } else if (isCheck) {
            gameState = GameState.CHECK;
            winner = Winner.NONE;
            ModelLog.getInstance().add("Jogador em check: " + getCurrentPlayerName());
        } else {
            gameState = GameState.ONGOING;
            winner = Winner.NONE;
        }
    }

    private boolean hasLegalMoves(boolean white) {
        for (int row = 1; row <= 8; row++) {
            for (char col = 'a'; col <= 'h'; col++) {
                Piece piece = board.getPiece(col, row);
                if (piece != null && piece.getIsWhite() == white) {
                    String[] moves = piece.getPossibleMoves(this);
                    for (String move : moves) {
                        char newCol = move.charAt(0);
                        int newRow = Character.getNumericValue(move.charAt(1));

                        // Simula a jogada
                        Piece backup = board.getPiece(newCol, newRow);
                        board.removePiece(col, row);
                        board.setPiece(piece, newCol, newRow);

                        boolean inCheck = isInCheck(white);

                        // Reverter jogada
                        board.removePiece(newCol, newRow);
                        board.setPiece(piece, col, row);
                        if (backup != null)
                            board.setPiece(backup, newCol, newRow);
                        if (!inCheck)
                            return true; // existe pelo menos um movimento legal
                    }
                }
            }
        }
        return false;
    }


    /**
     * Checks whether a given position is under attack by a specified side.
     *
     * @param column The column of the square (e.g., 'e').
     * @param row The row of the square (1-8).
     * @param byWhite True if checking for attack by white pieces.
     * @return True if the square is under attack.
     */
    public boolean isUnderAttack(char column, int row, boolean byWhite) {
        String targetPosition = "" + column + row;

        for (char c = 'a'; c<= 'h';c++) {
            for (int r = 1; r <= 8; r++) {
                Piece piece = board.getPiece(c,r);
                if (piece == null || piece.getIsWhite() != byWhite){
                    continue;
                }

                //distinguir se a peça que está a atacar é do tipoKing ou outra peça qualquer (correção loop infinito)
                if (piece instanceof King) {
                    // O rei só pode atacar casas adjacentes
                    int dx = Math.abs(piece.getColumn() - column);
                    int dy = Math.abs(piece.getRow() - row);
                    if (dx <= 1 && dy <= 1 && (dx + dy) != 0) {
                        return true;
                        }
                    continue; // Ignora o resto da lógica
                }

                String[] moves = piece.getPossibleMoves(this);
                for (String move : moves) {
                    if (move.equals(targetPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean isInCheck(boolean white) {
        for (int row = 1; row <= 8; row++) {
            for (char col = 'a'; col <= 'h'; col++) {
                Piece piece = board.getPiece(col, row);
                if (piece != null && piece.getIsWhite() == white && Character.toLowerCase(piece.getSymbol()) == 'k') {
                    return this.isUnderAttack(col, row, !white);
                }
            }
        }
        return false; // rei não encontrado — tecnicamente jogo deveria estar terminado
    }

    /**
     * Gets the player whose turn it is ("White" or "Black").
     *
     * @return The name of the current player.
     */
    public String getPlayer() {
        return whiteToMove ? "White" : "Black";
    }

    /**
     * Returns the current chessboard object.
     *
     * @return The Board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Checks if it is white turn to move
     * @return True if is white's turn.
     */
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        board = new Board();
        whiteToMove = true;
        gameState = GameState.ONGOING;
        winner = Winner.NONE;
    }


    /**
     * Returns the winner of the game.
     *
     * @return Winner enum value.
     */
    public Winner getWinner() {
        return winner;
    }

    /**
     * Gets the name of the white player.
     *
     * @return Name of the white player.
     */
    public String getWhitePlayerName (){
        return this.whitePlayerName;
    }

    /**
     * Gets the name of the black player.
     *
     * @return Name of the black player.
     */
    public String getBlackPlayerName(){
        return this.blackPlayerName;
    }

    /**
     * Gets the name of the player who is playing.
     *
     * @return Name of the current player.
     */
    public String getCurrentPlayerName() {
        return whiteToMove ? this.whitePlayerName : this.blackPlayerName;
    }

    /**
     * Gets the name of the winner (White, Black, or "DRAW").
     *
     * @return Winner name or "DRAW".
     */
    public String getWinnerName() {
        return switch(winner) {
            case BLACK -> blackPlayerName;
            case WHITE -> whitePlayerName;
            default -> "DRAW";
        };
    }


    /**
     * Serializes the current game state into a string format.
     *
     * @return A string representing the game state.
     */
    public String exportGame() {
        StringBuilder sb = new StringBuilder();
        sb.append(whiteToMove ? "WHITE" : "BLACK").append(",\n");

        for (char col = 'a'; col <= 'h'; col++) {
            for (int row = 1; row <= 8; row++) {
                Piece piece = board.getPiece(col, row);
                if (piece != null) {
                    sb.append(piece.toString());
                    if (!piece.getHasMoved()){
                        sb.append("*");
                    }
                    sb.append(",");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Imports a game from its string representation.
     *
     * @param data The serialized game data.
     */
    public void importGame(String data) {
        if (data == null || data.isBlank()) return;

        String[] parts = data.split(",\\s*");
        if (parts.length == 0) return;

        // vê quem é o jogador atual
        whiteToMove = parts[0].equalsIgnoreCase("WHITE");

        //Limpa o tabuleiro
        board.clear();

        // recria as peças usando a class PieceFactory
        for (int i = 1; i < parts.length; i++) {
            String pieceText = parts[i];
            if (pieceText.isBlank()) continue;

            Piece piece = PieceFactory.fromText(pieceText);
            if (piece != null){
                if (piece.getHasMoved()){
                    piece.setHasMoved(true);
                }
                board.addPiece(piece);
            }

            boolean hasMoved = pieceText.endsWith("*");
             if (hasMoved){
                 pieceText = pieceText.substring(0, pieceText.length() - 1);

             }

        }
    }

    /**
     * Checks if a king can legally castle to a target column.
     *
     * @param king The king piece.
     * @param targetColumn Target column for castling ('g' or 'c').
     * @return True if castling is possible.
     */
    public boolean canCastle(Piece king, char targetColumn) {
        if (king.getHasMoved() || !(king instanceof King)){
            return false;
        }
        boolean isWhite = king.getIsWhite();
        int getRow = king.getRow();
        Board board = getBoard();

        //roque pequeno
        if (targetColumn == 'g'){
            Piece rook = board.getPiece('h',getRow);
            if (rook == null || rook.getHasMoved() || rook.getIsWhite() != isWhite){
                return false;
            }

            if (board.getPiece('f',getRow) != null || board.getPiece('g',getRow) != null){
                return false;
            }

            return !isUnderAttack('e',getRow,!isWhite)
                    && !isUnderAttack('f',getRow,!isWhite)
                    && !isUnderAttack('g',getRow,!isWhite);
        }

        //roque grande
        if (targetColumn == 'c'){
            Piece rook = board.getPiece('a',getRow);
            if (rook == null || rook.getHasMoved() || rook.getIsWhite() != isWhite){
                return false;
            }
            if (board.getPiece('d',getRow) != null || board.getPiece('c',getRow) != null
                || board.getPiece('b',getRow) != null){
                return false;
            }

            return !isUnderAttack('d',getRow,!isWhite)
                    && !isUnderAttack('e',getRow,!isWhite)
                    && !isUnderAttack('c',getRow,!isWhite);
        }
        return false;
    }

    /**
     * Promotes a pawn to a piece type based on the provided symbol.
     *
     * @param column Column of the pawn.
     * @param row Row of the pawn.
     * @param symbol The symbol representing the desired promotion ('Q','R','N','B').
     */
    public void promotePawn (char column, int row, char symbol){
        boolean isWhite = board.getPiece(column,row).getIsWhite();

        PieceType type = switch (Character.toUpperCase(symbol)){
            case 'R' -> PieceType.ROOK;
            case 'B' ->PieceType.BISHOP;
            case 'N' ->PieceType.KNIGHT;
            case 'Q' ->PieceType.QUEEN;
            default -> PieceType.QUEEN;
        };
        Piece newPromotedPiece = PieceFactory.create(type,column,row,isWhite,true);
        board.addPiece(newPromotedPiece);
        whiteToMove = !whiteToMove;
        updateGameState();
        clearPromotionRequest();
        ModelLog.getInstance().add("Peão promovido para " + type.name() + " em " + column + row);
    }

    /**
     * Gets the square for en passant capture.
     *
     * @return En passant target square or null.
     */
    public String getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Gets the current board size.
     *
     * @return Board size.
     */
    public int getBoardSize() {
        return board.getBoardSize();
    }

    /**
     * Gets a string representation of all pieces on the board.
     *
     * @return Textual representation of pieces.
     */
    public String getAllPiecesTextual () {
        return board.getAllPiecesOnBoard();
    }

    /**
     * Gets the possible moves for a piece at a specific position.
     *
     * @param column Column of the piece.
     * @param row Row of the piece.
     * @return Array of possible destination squares.
     */
    public String [] getPossibleMoves (char column, int row){
        Piece piece = board.getPiece(column, row);
        if (piece == null || piece.getIsWhite() != isWhiteToMove()){
            return new String[0];
        }
        return piece.getPossibleMoves(this);
    }

    /**
     * Gets the symbol of the piece at a specific position.
     *
     * @param position Position string ("d4").
     * @return The piece's symbol or '\0' if none.
     */
    public char getPieceSymbolAt (String position){
        String [] pieces = getAllPiecesTextual().split(",");
        for (String piece : pieces){
            if (piece.substring(1).equals(position)){
                return piece.charAt(0);
            }
        }
        return '\0';
    }

    /**
     * Clears all pieces from the board.
     */
    public void clearBoard () {
        board.clear();
    }

    /**
     * Sets the player who has the next move.
     *
     * @param originalIsWhite True if White should move next.
     */
    public void setWhiteToMove (boolean originalIsWhite){
        this.whiteToMove = originalIsWhite;
    }

    /**
     * Sets the en passant target square.
     *
     * @param originalEnPassant Square eligible for en passant.
     */
    public void setEnPassantTarget (String originalEnPassant){
        this.enPassantTarget = originalEnPassant;
    }

    /**
     * Checks if a player is waiting for a promotion.
     *
     * @return True if waiting for a promotion choice.
     */
    public boolean isWaitingForPromotion() {
        return waitingForPromotion;
    }

    /**
     * Gets the board square where a pawn is going to be promoted.
     *
     * @return Promotion square (e.g., "e8").
     */
    public String getPromotionPosition() {
        return promotionPosition;
    }

    /**
     * Clears the pending promotion state.
     */
    public void clearPromotionRequest() {
        waitingForPromotion= false;
        promotionPosition = null;
    }


    /**
     * Gets the piece at a specified position.
     *
     * @param col Column of the square.
     * @param row Row of the square.
     * @return The piece or null.
     */
    public Piece getPieceAt(char col, int row) {
        return board.getPiece(col, row);
    }

    /**
     * Places a piece at a specific board position.
     *
     * @param piece The piece to place.
     * @param col Column.
     * @param row Row.
     */
    public void setPieceAt(Piece piece, char col, int row) {
        board.setPiece(piece, col, row);
    }

    /**
     * Removes the piece at the given board position.
     *
     * @param col Column of the square.
     * @param row Row of the square.
     */
    public void removePieceAt(char col, int row) {
        board.removePiece(col, row);
    }
}


