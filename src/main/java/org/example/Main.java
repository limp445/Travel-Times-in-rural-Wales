package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Main extends Application {
    private String userCounty;
    private String userPlace;
    private VBox addDest2;
    private HBox userDestinations = new HBox();
    private Double originLat;
    private Double originLng;
    @Override
    public void start(Stage stage) throws Exception {
        //Setting variables
        ArrayList<String> welshCounties = new ArrayList<>();
        Collections.addAll(welshCounties, "Anglesey", "Blaenau Gwent", "Bridgend", "Caerphilly", "Cardiff", "Carmarthenshire", "Ceredigion", "Conwy", "Denbighshire", "Flintshire", "Gwynedd", "Merthyr Tydfil", "Monmouthshire", "Neath Port Talbot", "Newport", "Pembrokeshire", "Powys", "Rhondda Cynon Taf", "Swansea", "Torfaen", "Vale of Glamorgan", "Wrexham");
        //Destination.loadDestinationsFromCSV();// loads only once
        userDestinations.setVisible(false);

        //Map
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        webView.setZoom(1.0);
        String url = getClass().getResource("/index.html").toExternalForm();
        engine.load(url);
        //add destination pt1

        //add Destination pt1
        VBox addDestination = new VBox(new Label("A---B"), new ComboBox<String>(FXCollections.observableList(welshCounties)),new Button("Enter"));
        addDestination.getStyleClass().add("addDestination");
        addDestination.setVisible(false);
        addDestination.setAlignment(Pos.CENTER);
        //add destination pt2
        addDest2 = new VBox(new Label("A---B"), new ComboBox<String>(FXCollections.observableList(welshCounties)),new Button("Enter"));
        addDest2.setVisible(false);
        addDest2.setAlignment(Pos.CENTER);
        addDestination.getChildren().get(2).setOnMousePressed(event -> {
            addDestination.setVisible(false);
            ComboBox<String> cb = (ComboBox<String>) addDestination.getChildren().get(1);
            userCounty = cb.getValue();
            addDest2.getChildren().clear();
            try {
                addDest2.getChildren().addAll(new Label("Choose Destination"), new ComboBox<String>(FXCollections.observableList(Destination.getDestinationsByCountyCsv(userCounty))), new Button("Enter"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            addDest2.setVisible(true);
            addDest2.setAlignment(Pos.CENTER);
            addDest2.getStyleClass().add("addDestination");
        });

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