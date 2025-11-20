package pt.isec.pa.chess.ui.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ModelLog;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import java.beans.PropertyChangeListener;
import java.util.List;


public class LogWindow extends Stage  {
    private ListView <String> listView;
    private Button btnClear;

    public LogWindow() {
        setTitle("Model Logs");
        createViews();
        registerHandlers();
        update();
    }

    private void createViews () {
        listView = new ListView<>();
        btnClear = new Button("Clear Logs");

        BorderPane root = new BorderPane();
        root.setCenter(listView);
        root.setBottom(btnClear);
        setScene(new Scene (root,400,300));
    }

    private void registerHandlers() {
        btnClear.setOnAction(e->{
            ModelLog.getInstance().clear();
        });

        ModelLog.getInstance().addPropertyChangeListener(evt -> {
               update();
        });
    }

    private void update () {
        listView.getItems().setAll(ModelLog.getInstance().getLogs());
    }
}
