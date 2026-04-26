package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class Main extends Application {
    private String userCounty;
    private String userPlace;
    private VBox addDest2;
    private HBox userDestinations = new HBox();
    private Double originLat;
    private Double originLng;
    private VBox noOriginError = new VBox();
    private VBox removeBox = new VBox();
    private ObservableList<String> removeDestList = FXCollections.observableArrayList();
    private int numBands = 0;
    private VBox setBandsVbox = new VBox();
    public void setBandedGroups(int numBands, WebEngine engine) {
        setBandsVbox.getChildren().clear();
        setBandsVbox.setAlignment(Pos.CENTER);
        setBandsVbox.getStyleClass().add("setBands");
        HBox setBands = new HBox(new Label("Choose Values for each band"));
        setBands.setAlignment(Pos.CENTER);
        setBandsVbox.getChildren().add(setBands);
        for (int i = 0; i < numBands; i++) {
            HBox minMax = new HBox(new Label("Band: " + (i+1)),new TextField("Min"), new TextField("Max"));
            minMax.setAlignment(Pos.CENTER);
            minMax.setSpacing(10);
            setBandsVbox.getChildren().add(minMax);
        }
        HBox enter = new HBox(new Button("Enter"));
        enter.setAlignment(Pos.CENTER);
        setBandsVbox.getChildren().add(enter);
        setBandsVbox.setVisible(true);
        enter.getChildren().getFirst().setOnMousePressed(event -> {
            setBandsVbox.setVisible(false);
            Destination.bands.clear();
            for (int i = 0; i < numBands; i++) {
                HBox current = (HBox) setBandsVbox.getChildren().get(i + 1);
                TextField min = (TextField) current.getChildren().get(1);
                TextField max = (TextField) current.getChildren().get(2);
                //if statement? method is in banded group store as map?
                //arraylis of
                Pair<Integer, Integer> addBand = new Pair<>(Integer.parseInt(min.getText()), Integer.parseInt(max.getText()));
                Destination.bands.add(addBand);
            }
            try {
                Destination.splitIntoBands(engine);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


    }
    public void updateScreenList(WebEngine engine){
        VBox userDestinationsVbox = new VBox(10);
        Destination.destinationlist.sort(Comparator.comparingInt(Pair::getValue));
        for (Pair<Destination, Integer> destination : Destination.destinationlist) {
            try {
                VBox dest1 = new VBox(
                        new Label(destination.getKey().getDestinationType()),
                        new Label(DistanceMatrix.getTimeAsString(originLat, originLng, destination.getKey().getAddress())),
                        new Label(DistanceMatrix.getDistanceAsString(originLat, originLng, destination.getKey().getAddress())));
                dest1.getStyleClass().add("destinations");
                userDestinationsVbox.getChildren().add(dest1);
                Pair<Double, Double> LatLongPair = DistanceMatrix.addressToLatLng(destination.getKey().getDestinationType());
                engine.executeScript(
                        "placeMarker("
                                + LatLongPair.getKey() + ", "
                                + LatLongPair.getValue() + ", '"
                                + destination.getKey().getDestinationType() + "')"
                );
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        userDestinations.getChildren().clear();
        userDestinations.getChildren().add(userDestinationsVbox);
        userDestinations.setAlignment(Pos.TOP_RIGHT);
        userDestinations.setPadding(new Insets(10));
        userDestinations.setVisible(true);
        userDestinations.setMouseTransparent(true);
        userDestinations.getStyleClass().add("userDestinations");
        removeDestList.setAll(Destination.getListToString());
    }
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainView.fxml")));
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        stage.setTitle("Leaflet Map in JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}