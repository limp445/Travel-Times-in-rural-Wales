package org.example;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MainController {
    @FXML private WebView webView;
    @FXML private VBox selectCountyBox;
    @FXML private VBox chooseDestinationBox;
    @FXML private VBox originBox;
    @FXML private VBox removeBox;
    @FXML private VBox bandBox;
    @FXML private VBox errorBox;
    @FXML private HBox userDestinationsBox;

    @FXML private ComboBox<String> countyCombo;
    @FXML private ComboBox<String> destinationCombo;
    @FXML private ComboBox<String> removeCombo;

    @FXML private TextField originLatField;
    @FXML private TextField originLngField;

    private WebEngine engine;

    private Double originLat;
    private Double originLng;

    private ArrayList<String> welshCounties = new ArrayList<>();

    public void initialize() throws IOException {
        engine = webView.getEngine();
        engine.load(Objects.requireNonNull(getClass().getResource("/index.html")).toExternalForm());
        Collections.addAll(welshCounties, "Anglesey", "Blaenau Gwent", "Bridgend", "Caerphilly", "Cardiff", "Carmarthenshire", "Ceredigion", "Conwy", "Denbighshire", "Flintshire", "Gwynedd", "Merthyr Tydfil", "Monmouthshire", "Neath Port Talbot", "Newport", "Pembrokeshire", "Powys", "Rhondda Cynon Taf", "Swansea", "Torfaen", "Vale of Glamorgan", "Wrexham");
        countyCombo.setItems(FXCollections.observableList(welshCounties));
        Destination.loadDestinationsFromCSV();
        hideAllPopups();
    }
    @FXML
    private void handleAddDestination() {
        hideAllPopups();
        selectCountyBox.setVisible(true);
    }
    @FXML
    private void handleRemoveDestination() {
        hideAllPopups();
        removeCombo.setItems(FXCollections.observableList(Destination.getListToString()));
        removeBox.setVisible(true);
    }
    @FXML
    private void handleOrigin() {
        hideAllPopups();
        originBox.setVisible(true);
    }
    @FXML
    private void handleBand1() {
        hideAllPopups();
        bandBox.setVisible(true);
        setBandedGroups(1);
    }
    @FXML
    private void handleBand2() {
        hideAllPopups();
        bandBox.setVisible(true);
        setBandedGroups(2);
    }
    @FXML
    private void handleBand3() {
        hideAllPopups();
        bandBox.setVisible(true);
        setBandedGroups(3);
    }
    @FXML
    private void handleBand4() {
        hideAllPopups();
        bandBox.setVisible(true);
        setBandedGroups(4);
    }
    @FXML
    private void handleTraffic(ActionEvent event) throws IOException, InterruptedException {
        MenuItem item = (MenuItem) event.getSource();
        if (item.getText().equals("Traffic ON")) {
            item.setText("Traffic OFF");
            DistanceMatrix.changeTraffic();
            updateDestinationList();
            // turn traffic on
        } else {
            item.setText("Traffic ON");
            DistanceMatrix.changeTraffic();
            updateDestinationList();
            // turn traffic off
        }
    }
    //Set origin
    @FXML
    private void selectOrigin() {
        if (originLat != null && originLng != null) {
            engine.executeScript(
                    "removeMarkerByLatLng("
                            + originLat + ", "
                            + originLng + ")"
            );
        }
        try {
            originLat = Double.parseDouble(originLatField.getText());
            originLng = Double.parseDouble(originLngField.getText());

            engine.executeScript(
                    "placeMarker(" + originLat + ", " + originLng + ", 'Origin')"
            );

            originBox.setVisible(false);

        } catch (Exception e) {
            showError("Invalid origin coordinates");
            hideAllPopups();
        }
    }
    @FXML
    private void selectCounty() {
        String county = countyCombo.getValue();
        if (county == null) {
            selectCountyBox.setVisible(false);
            showError("Please select a county");
            return;
        }

        try {
            destinationCombo.setItems(
                    FXCollections.observableList(Destination.getDestinationsByCountyCsv(county))
            );
            selectCountyBox.setVisible(false);
            chooseDestinationBox.setVisible(true);

        } catch (Exception e) {
            showError("Unable to load destinations");
        }
    }
    @FXML
    private void selectDestination() {
        if (originLat == null || originLng == null) {
            showError("Please set an origin first");
            return;
        }

        String destName = destinationCombo.getValue();
        if (destName == null) {
            showError("Please select a destination");
            hideAllPopups();
            return;
        }

        Destination dest = Destination.stringToDest(destName);

        try {
            int time = DistanceMatrix.getTimeAsInt(originLat, originLng, dest.getAddress());
            Destination.destinationlist.add(new Pair<>(dest, time));

            updateDestinationList();
            chooseDestinationBox.setVisible(false);

        } catch (Exception e) {
            showError("Error calculating travel time");
            hideAllPopups();
        }
    }

    // Remove Destination

    @FXML
    private void selectRemove() {
        String destName = removeCombo.getValue();
        if (destName == null) {
            showError("Please select a destination to remove");
            return;
        }

        Destination dest = Destination.stringToDest(destName);

        try {
            int time = DistanceMatrix.getTimeAsInt(originLat, originLng, dest.getAddress());
            Destination.destinationlist.remove(new Pair<>(dest, time));
            Pair<Double, Double> removeLatLng = DistanceMatrix.addressToLatLng(dest.getDestinationType());
            engine.executeScript(
                    "removeMarkerByLatLng("
                            + removeLatLng.getKey() + ", "
                            + removeLatLng.getValue() + ")"
            );
            updateDestinationList();
            removeBox.setVisible(false);

        } catch (Exception e) {
            showError("Unable to remove destination");
        }
    }

    // -----------------------------
    // Update UI List
    // -----------------------------

    private void updateDestinationList() throws IOException, InterruptedException {
        userDestinationsBox.getChildren().clear();
        VBox vbox = new VBox(10);

        Destination.destinationlist.sort(Comparator.comparingInt(Pair::getValue));

        for (Pair<Destination, Integer> pair : Destination.destinationlist) {
            Destination dest = pair.getKey();

            VBox entry = new VBox(
                    new Label(dest.getDestinationType()),
                    new Label(DistanceMatrix.getTimeAsString(originLat, originLng, dest.getAddress())),
                    new Label(DistanceMatrix.getDistanceAsString(originLat, originLng, dest.getAddress()))
            );

            entry.getStyleClass().add("destinations");
            vbox.getChildren().add(entry);

            try {
                Pair<Double, Double> latlng = DistanceMatrix.addressToLatLng(dest.getDestinationType());
                engine.executeScript(
                        "placeMarker(" + latlng.getKey() + ", " + latlng.getValue() + ", '" + dest.getDestinationType() + "')"
                );
            } catch (Exception ignored) {}
        }
        userDestinationsBox.getChildren().add(vbox);
        userDestinationsBox.setMouseTransparent(true);
        userDestinationsBox.setAlignment(Pos.TOP_RIGHT);
        userDestinationsBox.setVisible(true);
    }
    public void setBandedGroups(int numBands) {

        bandBox.getChildren().clear();
        bandBox.setAlignment(Pos.CENTER);

        // Title
        HBox title = new HBox(new Label("Choose Values for each band"));
        title.setAlignment(Pos.CENTER);
        bandBox.getChildren().add(title);

        // Create band rows
        for (int i = 0; i < numBands; i++) {
            HBox minMax = new HBox(
                    new Label("Band: " + (i + 1)),
                    new TextField("Min"),
                    new TextField("Max")
            );
            minMax.setAlignment(Pos.CENTER);
            minMax.setSpacing(10);
            bandBox.getChildren().add(minMax);
        }

        // Enter button
        Button enterButton = new Button("Enter");
        HBox enter = new HBox(enterButton);
        enter.setAlignment(Pos.CENTER);
        bandBox.getChildren().add(enter);

        bandBox.setVisible(true);

        // Handle click
        enterButton.setOnMousePressed(event -> {

            Destination.bands.clear();

            for (int i = 0; i < numBands; i++) {
                HBox current = (HBox) bandBox.getChildren().get(i + 1);
                TextField min = (TextField) current.getChildren().get(1);
                TextField max = (TextField) current.getChildren().get(2);

                Pair<Integer, Integer> addBand =
                        new Pair<>(Integer.parseInt(min.getText()), Integer.parseInt(max.getText()));

                Destination.bands.add(addBand);
            }

            bandBox.setVisible(false); // moved to AFTER reading fields

            try {
                Destination.splitIntoBands(engine);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void summaryStats(){
        //min max mean, range, iqr q1 and q3
        summaryBox.getChildren().add(new Label("Summary Statistics"));
        List<Integer> summary = Destination.destinationlist.stream().map(Pair::getValue).toList();
        //min
        Destination.destinationlist.sort(Comparator.comparingInt(Pair::getValue));
        String min = Destination.destinationlist.getFirst().getKey().getDestinationType();
        summaryBox.getChildren().add(new Label("Min: " + min));
        //max
        String max = Destination.destinationlist.getLast().getKey().getDestinationType();
        summaryBox.getChildren().add(new Label("Max: " + max));
        //mean
        double mean = 0;
        for (Pair<Destination, Integer> Destination : Destination.destinationlist){
            mean = mean + Destination.getValue();
        }
        mean = mean / Destination.destinationlist.size();
        summaryBox.getChildren().add(new Label("Average: " + mean));
    }



    private void hideAllPopups() {
        selectCountyBox.setVisible(false);
        chooseDestinationBox.setVisible(false);
        originBox.setVisible(false);
        removeBox.setVisible(false);
        bandBox.setVisible(false);
        errorBox.setVisible(false);
    }
    private void showError(String message) {
        errorBox.getChildren().clear();
        errorBox.getChildren().addAll(new Label(message), new Button("OK"));
        errorBox.setVisible(true);
        errorBox.getChildren().getLast().setOnMousePressed(event -> {
            errorBox.setVisible(false);
        });
    }


}
