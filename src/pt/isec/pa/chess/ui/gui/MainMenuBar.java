
package pt.isec.pa.chess.ui.gui;


import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class MainMenuBar extends MenuBar {
    private final ChessGameManager gameManager;
    Menu mnGame, mnMode;
    MenuItem mnNew, mnOpen,mnSave,mnImport, mnExport,mnQuit,mnUndo,mnRedo;
    RadioMenuItem mnNormal,mnLearning;
    CheckMenuItem mnShowMoves;



    public MainMenuBar (ChessGameManager gameManager){
        this.gameManager = gameManager;
        createView();
        registerHandlers();
        update();
    }

    private void createView () {

        mnGame = new Menu ("Game");
        mnNew = new MenuItem("New");
        mnOpen = new MenuItem("Open");
        mnSave = new MenuItem("Save");
        mnImport = new MenuItem("Import");
        mnExport = new MenuItem ("Export");
        mnQuit = new MenuItem ("Quit");

        mnGame.getItems().addAll (
                mnNew, mnImport, mnExport, new SeparatorMenuItem(),mnSave,mnOpen, new SeparatorMenuItem(), mnQuit
        );

        mnMode = new Menu ("Mode");
        mnNormal = new RadioMenuItem("Normal"); //RadioMenuItem serve para apenas podermos ter uma opção selecionada de cada vez
        mnLearning = new RadioMenuItem("Learning");
        ToggleGroup group = new ToggleGroup();
        mnNormal.setToggleGroup(group);
        mnLearning.setToggleGroup(group);
        mnNormal.setSelected(true); // modo default

        mnShowMoves = new CheckMenuItem("Show possible moves");
        mnUndo = new MenuItem ("Undo");
        mnRedo = new MenuItem ("Redo");

        mnShowMoves.setDisable(true);
        mnUndo.setDisable(true);
        mnRedo.setDisable(true);

        mnMode.getItems().addAll(mnNormal,mnLearning, new SeparatorMenuItem(), mnUndo,mnRedo,mnShowMoves);

        this.getMenus().addAll(mnGame,mnMode);

    }

    private void registerHandlers (){
        mnUndo.setOnAction(e -> gameManager.undo());
        mnRedo.setOnAction(e -> gameManager.redo());

        mnNormal.setOnAction(e ->{
            gameManager.getModelUi().setLearningMode(false);
            mnShowMoves.setDisable(true);
            mnUndo.setDisable(true);
            mnRedo.setDisable(true);
        });

        mnLearning.setOnAction(e-> {
            gameManager.getModelUi().setLearningMode(false);
            mnShowMoves.setDisable(false);
            mnUndo.setDisable(false);
            mnRedo.setDisable(false);
        });

        mnShowMoves.setOnAction(e-> {
            gameManager.getModelUi().setLearningMode(mnShowMoves.isSelected());
        });

        mnNew.setOnAction(e ->{
            String white = askPlayerName ("White player name:");
            if (white == null || white.isBlank()) {
                white = "White";
            }
            String black = askPlayerName ("Black player name:");
            if (black == null || black.isBlank()) {
                black = "Black";
            }
            gameManager.newGame(white,black);

        });

        mnQuit.setOnAction(e ->{
            Platform.exit();
        });

        mnSave.setOnAction(e ->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("File save...");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll (
                    new FileChooser.ExtensionFilter("File (*dat)", "*.dat"));
            File hFile = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (hFile != null){
                gameManager.saveGame(hFile.getAbsolutePath());
            }

        });

        mnOpen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("File Open...");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("File (*dat)", "*.dat")
            );
            File hFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (hFile != null){
                gameManager.loadGame(hFile.getAbsolutePath());
            }
        });

        mnExport.setOnAction(e ->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Game");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files","*.csv")
            );
            File hFile = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (hFile != null){
                String exportData = gameManager.exportGame();
                try (PrintWriter out = new PrintWriter(hFile)) {
                    out.print(exportData);
                    ModelLog.getInstance().add("Jogo exportado para: " + hFile.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ModelLog.getInstance().add("Erro ao exportar o jogo para: " + hFile.getAbsolutePath());
                }

            }
        });


        mnImport.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Game (CSV)");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File hFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (hFile != null) {
                String white = askPlayerName("White player name:");
                if (white == null || white.isBlank()) {
                    white = "White";
                }
                String black = askPlayerName("Black player name:");
                if (black == null || black.isBlank()) {
                    black = "Black";
                }
                gameManager.newGame(white, black);

                try (Scanner scanner = new Scanner(hFile)) {
                    StringBuilder sb = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        sb.append(scanner.nextLine());
                    }
                    gameManager.importGame(sb.toString().trim());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ModelLog.getInstance().add("Erro ao importar: " + ex.getMessage());
                }
            }
        });


    }

    private String askPlayerName(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Game");
        dialog.setHeaderText(prompt);
        dialog.setContentText("Name:");
        return dialog.showAndWait().orElse(null);
    }

    private void update () {

    }
}
