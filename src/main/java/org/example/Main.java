package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public void start(Stage stage) throws Exception {
        //Setting variables
        ArrayList<String> welshCounties = new ArrayList<>();
        Collections.addAll(welshCounties, "Anglesey", "Blaenau Gwent", "Bridgend", "Caerphilly", "Cardiff", "Carmarthenshire", "Ceredigion", "Conwy", "Denbighshire", "Flintshire", "Gwynedd", "Merthyr Tydfil", "Monmouthshire", "Neath Port Talbot", "Newport", "Pembrokeshire", "Powys", "Rhondda Cynon Taf", "Swansea", "Torfaen", "Vale of Glamorgan", "Wrexham");
        Destination.loadDestinationsFromCSV();// loads only once
        userDestinations.setVisible(false);
        setBandsVbox.setVisible(false);

        //Map
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        webView.setZoom(1.0);
        String url = Objects.requireNonNull(getClass().getResource("/index.html")).toExternalForm();
        engine.load(url);
        //add destination pt1

        //add Destination pt1
        VBox addDestination = new VBox(new Label("Please select the county of your destination"), new ComboBox<String>(FXCollections.observableList(welshCounties)),new Button("Enter"));
        addDestination.getStyleClass().add("addDestinationCounty");
        addDestination.setVisible(false);
        addDestination.setAlignment(Pos.CENTER);
        //add destination pt2
        addDest2 = new VBox(new Label("Please select your destination"), new ComboBox<String>(FXCollections.observableList(welshCounties)),new Button("Enter"));
        addDest2.setVisible(false);
        addDest2.setAlignment(Pos.CENTER);
        //error message set up
        noOriginError = new VBox(new Label("Please choose an origin before adding destinations"), new Button("OK"));
        noOriginError.setAlignment(Pos.CENTER);
        noOriginError.getStyleClass().add("addDestination");
        noOriginError.setVisible(false);
        addDestination.getChildren().get(2).setOnMousePressed(event -> {
            if (originLat != null && originLng != null) {
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
                addDest2.getChildren().get(2).setOnMousePressed(event2 -> {
                    addDest2.setVisible(false);
                    ComboBox<String> cb2 = (ComboBox<String>) addDest2.getChildren().get(1);
                    Destination userDest = Destination.stringToDest(cb2.getValue());
                    try {
                        Destination.destinationlist.add(new Pair<>(userDest, DistanceMatrix.getTimeAsInt(originLat, originLng, userDest.getAddress())));
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    //new hbox / vbox// only dest needs origin and covert to latlong
                    //would need to be different eqch time ran
                    //maybe function rest boxes are blank
                    updateScreenList(engine);
            });
            } else {
                addDestination.setVisible(false);
                noOriginError.setVisible(true);
                noOriginError.getChildren().getLast().setOnMousePressed(event1 -> noOriginError.setVisible(false));
            }
        });
        //remove destination
        removeBox = new VBox(new Label("Choose a Destination to remove"), new ComboBox<String>(removeDestList), new Button("Enter"));
        removeBox.setVisible(false);
        removeBox.setAlignment(Pos.CENTER);
        removeBox.getStyleClass().add("removeDestination");
        removeBox.getChildren().get(2).setOnMousePressed(event -> {
            removeBox.setVisible(false);
            if (originLat == null || originLng == null) {
                ComboBox<String> cb = (ComboBox<String>) removeBox.getChildren().get(1);
                String userRemove = cb.getValue();//try the combobox as destination
                Destination removeDest = Destination.stringToDest(userRemove);
                try {
                    Destination.destinationlist.remove(new Pair<>(removeDest, DistanceMatrix.getTimeAsInt(originLat, originLng, removeDest.getAddress())));
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //func to complete userlist onscreen would be usefule
                try {
                    Pair<Double, Double> removeLatLng = DistanceMatrix.addressToLatLng(removeDest.getDestinationType());
                    engine.executeScript(
                            "removeMarkerByLatLng("
                                    + removeLatLng.getKey() + ", "
                                    + removeLatLng.getValue() + ")"
                    );
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                updateScreenList(engine);
            } else{
                noOriginError.setVisible(true);
                noOriginError.getChildren().getLast().setOnMousePressed(event1 -> noOriginError.setVisible(false));
            }


        });

        //Set origin
        VBox originVbox = new VBox(new Label("Type the address"), new TextField("Latitude"), new TextField("Longitude"), new Button("Enter"));
        originVbox.setAlignment(Pos.CENTER);
        originVbox.setVisible(false);
        originVbox.getStyleClass().add("setOrigin");
        originVbox.getChildren().get(3).setOnMousePressed(event -> {
            originVbox.setVisible(false);
            TextField latTextF = (TextField) originVbox.getChildren().get(1);
            originLat = Double.parseDouble(latTextF.getText());
            TextField lngTextF = (TextField) originVbox.getChildren().get(2);
            originLng = Double.parseDouble(lngTextF.getText());
            engine.executeScript(
                    "placeMarker("
                            + originLat + ", "
                            + originLng+ ", '"
                            + "Your Origin" + "')"
            );

        });



        //Top Menu
        Menu TopMenu = new Menu("Functions");
        TopMenu.getItems().addAll(
                new MenuItem("Add Destination"),// marker colours are banded groups
                new MenuItem("Remove Destination"),
                new MenuItem("Set Origin"),
                new MenuItem("Find Quickest Visit route"),
                new MenuItem("Graph Data"));
        Menu Trafficmenu = new Menu("Traffic");
        if(DistanceMatrix.traffic){
            Trafficmenu.getItems().addAll(new MenuItem("Traffic ON"));
        }else {
            Trafficmenu.getItems().addAll(new MenuItem("Traffic OFF"));
        }
        Menu MeasureMenu = new Menu("Set Banded Groups");
        MeasureMenu.getItems().addAll(
                new MenuItem("Reset Bands"),
                new MenuItem("1"),
                new MenuItem("2"),
                new MenuItem("3"),
                new MenuItem("4")
        );
        TopMenu.getItems().getFirst().setOnAction(event -> {addDestination.setVisible(true);});
        TopMenu.getItems().get(1).setOnAction(event -> {removeBox.setVisible(true);});
        TopMenu.getItems().get(2).setOnAction(event -> {originVbox.setVisible(true);});
        Trafficmenu.getItems().getFirst().setOnAction(event -> {
            DistanceMatrix.changeTraffic();
            if(DistanceMatrix.traffic){
                Trafficmenu.getItems().getFirst().setText("Traffic ON");
            }else {
                Trafficmenu.getItems().getFirst().setText("Traffic OFF");
            }
        });
        MeasureMenu.getItems().getFirst().setOnAction(e -> {numBands = 0;});
        MeasureMenu.getItems().get(1).setOnAction(e -> {
            numBands = 1;
            setBandedGroups(1, engine);
        });
        MeasureMenu.getItems().get(2).setOnAction(e -> {
            numBands = 2;
            setBandedGroups(2, engine);
        });
        MeasureMenu.getItems().get(3).setOnAction(e -> {
            numBands = 3;
            setBandedGroups(3, engine);
        });
        MeasureMenu.getItems().get(4).setOnAction(e -> {
            numBands = 4;
            setBandedGroups(4, engine);
        });
        //maybe pair for bands, move circle out of marker func, set bands to variables 0 <0 if not set
        MenuBar TopMenuBar = new MenuBar();
        TopMenuBar.getMenus().addAll(TopMenu, Trafficmenu, MeasureMenu);



        //StackPane
        StackPane stackPane = new StackPane(webView, TopMenuBar, addDestination, addDest2, originVbox, userDestinations, noOriginError, removeBox, setBandsVbox);
        StackPane.setAlignment(TopMenuBar, Pos.TOP_CENTER);
        StackPane.setAlignment(userDestinations, Pos.TOP_RIGHT);
        addDestination.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        addDest2.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        originVbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        userDestinations.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setMargin(userDestinations, new Insets(50, 50, 0, 0));
        noOriginError.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        removeBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setBandsVbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);



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