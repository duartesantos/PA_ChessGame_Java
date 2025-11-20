package pt.isec.pa.chess.ui.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;

public class MainJFX extends Application {
    ChessGameManager gameManager;

    public MainJFX() {
        gameManager = new ChessGameManager();
    }

    @Override
    public void start (Stage stage){
        RootPane root = new RootPane(gameManager);
        Scene scene = new Scene(root,800,800);
        stage.setScene(scene);
        stage.setTitle("Chess Game PA");
        stage.show();

        Stage st2 = new Stage();
        RootPane root2 = new RootPane(gameManager);
        Scene scene2 = new Scene(root2,800,800);
        st2.setScene(scene2);
        st2.setTitle("Chess Game PA");
        st2.show();
        // Lança a janela de logs
        LogWindow logWindow = new LogWindow();
        logWindow.show();
    }
}
