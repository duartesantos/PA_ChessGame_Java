package pt.isec.pa.chess.ui.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.GameState;
import pt.isec.pa.chess.modelui.ModelUI;
import pt.isec.pa.chess.ui.res.ImageManager;
import pt.isec.pa.chess.ui.res.SoundManager;

import java.util.*;

public class BoardView extends Canvas {
    private ChessGameManager gameManager;
    private Map<Character, Image> piecesImage = new HashMap<>();
    private final int padding = 30;
    private double squareSize;
    private String selectedPosition = null;
    private List<String> highlightedMoves = new ArrayList<>();

    public BoardView (ChessGameManager gameManager) {
        this.gameManager = gameManager;
        loadPiecesImages();
        registerHandlers();
        update();
    }

    private void updateSize() {
        int boardSize = gameManager.getBoardSize();
        double minDimension = Math.min(getWidth(), getHeight());
        squareSize = (minDimension - padding) / boardSize;
    }

    private void loadPiecesImages () {
        piecesImage.put('K', ImageManager.getImage("kingW.png"));
        piecesImage.put('Q', ImageManager.getImage("queenW.png"));
        piecesImage.put('B', ImageManager.getImage("bishopW.png"));
        piecesImage.put('N', ImageManager.getImage("knightW.png"));
        piecesImage.put('R', ImageManager.getImage("rookW.png"));
        piecesImage.put('P', ImageManager.getImage("pawnW.png"));

        piecesImage.put('k', ImageManager.getImage("kingB.png"));
        piecesImage.put('q', ImageManager.getImage("queenB.png"));
        piecesImage.put('b', ImageManager.getImage("bishopB.png"));
        piecesImage.put('n', ImageManager.getImage("knightB.png"));
        piecesImage.put('r', ImageManager.getImage("rookB.png"));
        piecesImage.put('p', ImageManager.getImage("pawnB.png"));

    }

    private void update(){
        GraphicsContext gc = getGraphicsContext2D();
        int boardSize = gameManager.getBoardSize();
        updateSize();

        gc.clearRect(0,0,getWidth(),getHeight());

        //desenha o tabuleiro
        for (int row = 0; row < boardSize; row++){
            for (int column = 0; column < boardSize; column++){
                boolean isLight = (row + column) % 2 == 0;
                gc.setFill(isLight ? Color.rgb(237,214,176) : Color.rgb(184,135,98));
                gc.fillRect(padding + column * squareSize, padding + row * squareSize, squareSize, squareSize );
            }
        }

        //desenha as etiquetas
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Times New Roman", javafx.scene.text.FontWeight.EXTRA_BOLD,14));
        for (int i = 0 ; i < boardSize; i++){
            char colLabel = (char) ('a' + i);
            gc.fillText("" + colLabel,padding + i * squareSize + squareSize/2,padding - 10);

            int rowLabel = boardSize - i;
            gc.fillText("" + rowLabel,padding - 20,padding + i * squareSize + squareSize/2);
        }

        String getAllPiecesTextual = gameManager.getAllPiecesTextual();

        if (getAllPiecesTextual == null || getAllPiecesTextual.isBlank()){
            return;
        }
        //desenhar pecas
        String [] pieces = getAllPiecesTextual.split(",");
        for (String piece : pieces ){
            char symbol = piece.charAt(0);
            char column = piece.charAt(1);
            int row = Character.getNumericValue(piece.charAt(2));

            Image img = piecesImage.get(symbol);
            if (img != null){
                int colIndex = column - 'a';
                int rowIndex = boardSize - row;
                gc.drawImage(img,
                        padding + colIndex * squareSize,
                        padding + rowIndex * squareSize,
                        squareSize, squareSize);
            }
        }

        // destacar a casa selecionada
        if (selectedPosition != null && selectedPosition.length() == 2) {
            char col = selectedPosition.charAt(0);
            int row = Character.getNumericValue(selectedPosition.charAt(1));
            int colIndex = col - 'a';
            int rowIndex = boardSize - row;

            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
            gc.strokeRect(
                    padding + colIndex * squareSize,
                    padding + rowIndex * squareSize,
                    squareSize, squareSize);
        }

        if (gameManager.getModelUi().isLearningMode() && !highlightedMoves.isEmpty()){
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            for (String move : highlightedMoves) {
                if (move.length() != 2) continue;
                char col = move.charAt(0);
                int row = Character.getNumericValue(move.charAt(1));
                int colIndex = col - 'a';
                int rowIndex = boardSize - row;

                gc.strokeRect(
                        padding + colIndex * squareSize + 5,
                        padding + rowIndex * squareSize + 5,
                        squareSize - 10,
                        squareSize - 10);
            }
        }
    }

    private void registerHandlers () {
        gameManager.addPropertyChangeListener(ChessGameManager.PROP_BOARD,evt -> {
            selectedPosition = null;
            highlightedMoves.clear();
            update();});

        widthProperty().addListener((obs, oldVal, newVal) -> update());
        heightProperty().addListener((obs, oldVal, newVal) -> update());

        gameManager.getModelUi().addPropertyChangeListener(ModelUI.PROP_LEARNING_MODE, evt -> {
            // Reage à ativação/desativação do modo de aprendizagem
            if (!(Boolean) evt.getNewValue()) { // SE O NOVO VALOR FOR FALSO
                highlightedMoves.clear();
            }
            update();
        });

        gameManager.addPropertyChangeListener(ChessGameManager.PROP_PROMOTE,evt -> {
            if (!gameManager.isWaitingForPromotion()) return;

            String position = (String) evt.getNewValue();
            if (position == null || position.length() != 2)
                return;
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Q","Q","R","N","B");
            dialog.setTitle("Pawn Promotion");
            dialog.setHeaderText("Chose pawn promotion");
            dialog.setContentText("Promote to: ");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(choice ->{
                char column = position.charAt(0);
                int row = Character.getNumericValue(position.charAt(1));
                gameManager.promotePawn(column,row,choice.charAt(0));
            });
        });

//        gameManager.addPropertyChangeListener(ModelUI.PROP_SOUND,evt ->{
//            this.soundEnabled = (Boolean) evt.getNewValue();
//        });

        this.setOnMouseClicked(e -> {
            if (gameManager.isGameOver()) return; // Se o jogo acabou, nao deixa selecionar

            int boardSize = gameManager.getBoardSize();
            updateSize();

            double x = e.getX() - padding;
            double y = e.getY() - padding;

            if (x < 0 || y < 0) return;

            int columnIndex = (int) (x / squareSize);
            int rowIndex = (int) (y / squareSize);

            if (columnIndex >= boardSize || rowIndex >= boardSize) return;

            char column = (char) ('a' + columnIndex);
            int row = boardSize - rowIndex;

            String clickedPosition = "" + column + row;

            char pieceSymbol = gameManager.getPieceSymbolAt(clickedPosition);

            if (selectedPosition == null) {
                if (pieceSymbol != '\0'){
                    boolean isWhite = Character.isUpperCase(pieceSymbol);
                    boolean isWhiteTurn = gameManager.isWhiteToMove();
                    if (isWhite && isWhiteTurn || (!isWhite && !isWhiteTurn)){
                        selectedPosition = clickedPosition;
                        /// update highlightedmoves
                        if (gameManager.getModelUi().isLearningMode()){
                            highlightedMoves.clear();
                            highlightedMoves.addAll(Arrays.asList(gameManager.getPossibleMovesFor(clickedPosition.charAt(0),
                                                                  Character.getNumericValue(clickedPosition.charAt(1)))));
                        }
                        update();
                    }
                }
            } else {
                if (selectedPosition.equals(clickedPosition))
                    return;

                if (pieceSymbol != '\0'){ //se cliclarmos numa peca da mesma equipa, troca a selecao
                    boolean isWhite = Character.isUpperCase(pieceSymbol);
                    boolean isWhiteTurn = gameManager.isWhiteToMove();
                    if (isWhite && isWhiteTurn || (!isWhite && !isWhiteTurn)){
                        selectedPosition = clickedPosition;
                        /// update highlightedmoves
                        if (gameManager.getModelUi().isLearningMode()){
                            highlightedMoves.clear();
                            highlightedMoves.addAll(Arrays.asList(gameManager.getPossibleMovesFor(clickedPosition.charAt(0),
                                    Character.getNumericValue(clickedPosition.charAt(1)))));
                        }
                        update();
                    }
                }

                String from = selectedPosition;
                String to = clickedPosition;
                char movedPiece = gameManager.getPieceSymbolAt(selectedPosition);
                char targetPiece = gameManager.getPieceSymbolAt(clickedPosition);
                boolean capture = targetPiece != '\0' && Character.isUpperCase(movedPiece) != Character.isUpperCase(targetPiece);

                // Caso contrário, tenta mover
                boolean moved = gameManager.move(selectedPosition, clickedPosition);

                if (moved){
                    boolean isCheck = gameManager.getGameState() == GameState.CHECK;
                    if (gameManager.getModelUi().isSoundEnable()){
                        SoundManager.playMovesSounds(movedPiece,from,to,capture,targetPiece,isCheck);
                    }
                }

                if (moved && gameManager.isGameOver()) {
                    String result;
                    switch (gameManager.getWinner()) {
                        case WHITE:
                        case BLACK:
                            result = gameManager.getWinnerName() + " ganhou o jogo!!";
                            break;
                        case NONE:
                            result = "EMPATE";
                            break;
                        default:
                            result = "Acabou o jogo!";
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Acabou o jogo!");
                    alert.setHeaderText("Resultado:");
                    alert.setContentText(result);
                    alert.showAndWait();
                }
            }
        });

    }


    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }


}