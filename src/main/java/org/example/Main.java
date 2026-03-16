package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        BorderPane root = new BorderPane();

        VBox mainBox = new VBox(10);
        VBox vBox1 = new VBox(
            new Label("A---B"),
            new Label(DistanceMatrix.getTimeAsString()),
            new Label(DistanceMatrix.getDistanceAsString())
        );
        vBox1.getStyleClass().add("mainBox");

        VBox vBox2 = new VBox(
                new Label("A---B"),
                new Label(DistanceMatrix.getTimeAsString()),
                new Label(DistanceMatrix.getDistanceAsString())
        );
        vBox2.getStyleClass().add("mainBox");

        mainBox.getChildren().addAll(vBox1, vBox2);
        mainBox.setAlignment(Pos.TOP_RIGHT);
        HBox hBox = new HBox(mainBox);
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setPadding(new Insets(10));
        root.setTop(hBox);


        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        stage.setTitle("JavaFX 23 Example");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}