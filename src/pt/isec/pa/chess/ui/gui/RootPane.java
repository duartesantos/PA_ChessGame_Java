package pt.isec.pa.chess.ui.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.modelui.ModelUI;

import java.util.Optional;

public class RootPane extends BorderPane {
    private ChessGameManager gameManager;
    private Label lblPlayerWhite;
    private Label lblPlayerBlack;
    private Label lblCurrentPlayer;
    private BoardView boardView;
    ToggleButton btnSound;

    public RootPane(ChessGameManager gameManager) {
        this.gameManager = gameManager;

        createViews ();
        registerHandlers();
        update ();
    }

    private void createViews (){

        lblPlayerWhite = new Label("White: " + gameManager.getWhiteName());
        lblPlayerBlack = new Label("Black: " + gameManager.getBlackName());
        lblCurrentPlayer = new Label("Current Player: " + gameManager.getCurrentPlayerName());
        lblCurrentPlayer.setMinWidth(160);
        lblCurrentPlayer.setMaxWidth(160);

        setTop (
               new VBox(
                       new MainMenuBar(gameManager)
               )

        );
        btnSound = new ToggleButton("Sound ON");
        btnSound.setSelected(true);
        // Info dos jogadores acima do tabuleiro
        HBox topInfo = new HBox(20, lblPlayerWhite, lblPlayerBlack, lblCurrentPlayer,btnSound);
        topInfo.setPadding(new Insets(10));

        // Tabuleiro
        boardView = new BoardView(gameManager);

        // Agrupar info e tabuleiro
        VBox centerBox = new VBox(topInfo, boardView);
        centerBox.setPadding(new Insets(10));
        setCenter(centerBox);

    }

    private void registerHandlers(){
        boardView.widthProperty().bind(widthProperty(). subtract(50));
        boardView.heightProperty().bind(heightProperty().subtract(100));
        gameManager.getModelUi().addPropertyChangeListener(ModelUI.PROP_SOUND, evt -> {
            update();
        });
        gameManager.addPropertyChangeListener(ChessGameManager.PROP_CURRENT_PLAYER, evt -> {
            update();
        });

        btnSound.setOnAction(e -> {
            gameManager.getModelUi().toggleSound();
            if (btnSound.isSelected()){
                btnSound.setText("Sound ON");
            }
            else {
                btnSound.setText("Sound OFF");
            }
        });
    }

    private void update () {
        lblPlayerWhite.setText("White: " + gameManager.getWhiteName());
        lblPlayerBlack.setText("Black: " + gameManager.getBlackName());
        lblCurrentPlayer.setText("Current Player: " + gameManager.getCurrentPlayerName());
        btnSound.setText(gameManager.getModelUi().isSoundEnable() ? "Sound ON" : "Sound OFF");

    }
}
