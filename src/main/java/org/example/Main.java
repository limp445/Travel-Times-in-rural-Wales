package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


import java.awt.*;
import java.net.URI;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //Map
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        webView.setZoom(1.0);
        String url = getClass().getResource("/index.html").toExternalForm();
        engine.load(url);
        //Map

        //Top Menu
        Menu TopMenu = new Menu("Functions");
        TopMenu.getItems().addAll(
                new MenuItem("Add Destination"),
                new MenuItem("Remove Destination"),
                new MenuItem("Set Origin"),
                new MenuItem("Find Quickest Visit route"),
                new MenuItem("Graph Data"));

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(TopMenu);
        //menuBar.setMouseTransparent(true);// not needed here



        //StackPane
        StackPane stackPane = new StackPane(webView, menuBar);
        StackPane.setAlignment(menuBar, Pos.TOP_CENTER);



        Scene scene = new Scene(stackPane, 1000, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        stage.setTitle("Leaflet Map in JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}