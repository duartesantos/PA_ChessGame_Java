package pt.isec.pa.chess.model;
import pt.isec.pa.chess.model.command.CommandManager;
import pt.isec.pa.chess.model.command.MoveCommand;
import pt.isec.pa.chess.modelui.ModelUI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Manages the lifecycle and interactions of a chess game, including player moves,
 * game state transitions, undo/redo functionality, and property change notifications.
 * Acts as a facade between the model and external components such as the UI.
 */
public class ChessGameManager {
    private ChessGame game;
    PropertyChangeSupport pcs;
    public static final String PROP_BOARD = "board";
    public static final String PROP_CURRENT_PLAYER = "currentPlayer";
    public static final String PROP_PROMOTE = "promotePawn";
    private final ModelUI modelUi;

    private final CommandManager commandManager = new CommandManager();

    /**
     * Initializes a new chess game manager with default player names ("White Player" , "Black Player").
     */
    public ChessGameManager(){
        game = new ChessGame("White Player", "Black Player");
        pcs = new PropertyChangeSupport(this);
        modelUi = new ModelUI();
    }

    /**
     * Starts a new chess game with a specified player names.
     * @param whitePlayerName the name of the white player
     * @param blackPlayerName the name of the black player
     */
    public void newGame(String whitePlayerName, String blackPlayerName){
        game = new ChessGame(whitePlayerName, blackPlayerName);
        pcs.firePropertyChange(PROP_BOARD,null,null);
        pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayerName());
    }

    /**
     * Attempts to perform a move from one position to another.
     * If successful, fires property changes for board and player updates.
     *
     * @param from the source position ("e2")
     * @param to the target position ("e4")
     * @return true if the move was successful.
     */
    public boolean move(String from, String to) {
        MoveCommand cmd = new MoveCommand(game, from, to);
        boolean moved = commandManager.invokeCommand(cmd);
        if (moved) {
            if (game.isWaitingForPromotion()) {
                pcs.firePropertyChange(PROP_PROMOTE, null, game.getPromotionPosition());
            }
            pcs.firePropertyChange(PROP_BOARD, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayerName());
        }
        else {
            pcs.firePropertyChange(PROP_BOARD, null, null);
        }
        return moved;
    }

    /**
     * Saves the current game state to a file.
     *
     * @param fileName the name of the file
     */
    public void saveGame (String fileName){
        ModelLog.getInstance().add("Jogo guardado para: " + fileName);
        ChessGameSerialization.save(fileName,game);
    }

    /**
     * Loads a game state from a file.
     *
     * @param fileName the name of the file.
     */
    public void loadGame (String fileName){
        game = ChessGameSerialization.load(fileName);
        ModelLog.getInstance().add("Jogo carregado de: " + fileName);
        pcs.firePropertyChange(PROP_BOARD, null, null);

    }

    /**
     * Imports a game state from a CSV-format string.
     * @param data the CSV string representing the game state
     */
    public void importGame (String data){
        game.importGame(data);
        ModelLog.getInstance().add("Jogo importado do CSV: ");
        pcs.firePropertyChange(PROP_BOARD, null, null);
    }

    /**
     * Exports the current game state as a CSV-format string.
     *
     * @return the game state in CSV format
     */
    public String exportGame(){
        ModelLog.getInstance().add("Jogo exportado como CSV.");
        return game.exportGame();
    }

    /**
     * Returns the current game state ( check, ongoing, checkmate, stalemate).
     *
     * @return the current GameState
     */
    public GameState getGameState () {
        return game.getGameState();
    }

    /**
     * Returns the name of the current player.
     *
     * @return the name of the current player
     */
    public String getCurrentPlayerName () {
        return game.getCurrentPlayerName();
    }

    /**
     * Returns the name of the white player.
     *
     * @return the white player's name
     */
    public String getWhiteName (){
        return game.getWhitePlayerName();
    }

    /**
     * Returns the name of the black player.
     *
     * @return the black player's name
     */
    public String getBlackName () {
        return game.getBlackPlayerName();
    }

    /**
     * Returns the name of the winner.
     *
     * @return the winner player's name
     */
    public String getWinnerName () {
        return game.getWinnerName();
    }

    /**
     * Gets possible moves for a piece at the given position.
     *
     * @param column the column (a-h)
     * @param row the row (1-8)
     * @return array of valid destination positions
     */
    public String [] getPossibleMovesFor (char column, int row) {
        return game.getPossibleMoves(column,row);
    }

    /**
     * Checks if it is white's turn to move.
     *
     * @return true if it is white's turn.
     */
    public boolean isWhiteToMove () {
        return game.isWhiteToMove();
    }

    /**
     * Checks whether the game is over (either by checkmate or stalemate).
     *
     * @return true if the game is over
     */
    public boolean isGameOver() {
        return game.getGameState() == GameState.CHECKMATE || game.getGameState() == GameState.STALEMATE;
    }

    /**
     * Returns the winner of the game.
     *
     * @return the Winner (enum value).
     */
    public Winner getWinner () {
        return game.getWinner();
    }

    /**
     * Returns the size of the game board.
     *
     * @return the board size
     */
    public int getBoardSize() {
        return game.getBoardSize();
    }

    /**
     * Returns a textual representation of all pieces on the board.
     *
     * @return a string listing all pieces
     */
    public String getAllPiecesTextual () {
        return game.getAllPiecesTextual();
    }

    /**
     * Adds a listener to receive property change events for a specific property.
     *
     * @param property the property name
     * @param listener the listener to register
     */
    public void addPropertyChangeListener (String property, PropertyChangeListener listener){
        pcs.addPropertyChangeListener(property,listener);
    }

    /**
     * Returns the symbol of the piece at the given position.
     * @param position the board position ("e2")
     * @return the character symbol of the piece
     */
    public char getPieceSymbolAt (String position){
        return game.getPieceSymbolAt(position);
    }

    /**
     * Undo the last move if possible and fires necessary property change events.
     *
     * @return true if undo was successful
     */
    public boolean undo() {
        boolean undone = commandManager.undo();
        if (undone) {
            pcs.firePropertyChange(PROP_BOARD, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayerName());
        }
        return undone;
    }

    /**
     * Redo the last undone move if possible and fires necessary property change events.
     *
     * @return true if redo was successful
     */
    public boolean redo() {
        boolean redone = commandManager.redo();
        if (redone) {
            pcs.firePropertyChange(PROP_BOARD, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, getCurrentPlayerName());
        }
        return redone;
    }

    public boolean canUndo() {
        return commandManager.hasUndo();
    }

    public boolean canRedo() {
        return commandManager.hasRede();
    }

    /**
     * Promotes a pawn at the specified position to the given piece symbol.
     *
     * @param col the column of the pawn
     * @param row the row of the pawn
     * @param symbol the new piece symbol (e.g., 'Q' for queen)
     */
    public void promotePawn(char col, int row, char symbol) {
        game.promotePawn(col, row, symbol);
        pcs.firePropertyChange(PROP_BOARD, null, null);
        pcs.firePropertyChange(PROP_CURRENT_PLAYER,null,getCurrentPlayerName());
    }

    /**
     * Checks whether the game is waiting for a pawn promotion decision.
     *
     * @return true if a pawn is awaiting promotion
     */
    public boolean isWaitingForPromotion() {
        return game.isWaitingForPromotion();
    }

    /**
     * Returns the UI model associated with the chess game.
     *
     * @return the modelUI instance
     */
    public ModelUI getModelUi() {
        return modelUi;
    }
}
