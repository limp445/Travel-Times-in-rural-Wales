package org.example;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;

public class Destination {
    private final String destinationType;
    private final String address;
    private final String countyName;
    public static ArrayList<Pair<Destination, Integer>> destinationlist = new ArrayList<>();
    public static ArrayList<Destination> destinationsCSV = new ArrayList<>();

    public Destination(String destinationType, String address, String countyName) throws IOException {
        this.destinationType = destinationType;
        this.address = address;
        this.countyName = countyName;
    }
    public static void loadDestinationsFromCSV() throws IOException {
        InputStream is = Destination.class.getResourceAsStream("/Hospitals.csv");
        if (is == null) {
            throw new FileNotFoundException("Hospitals.csv not found in resources folder!");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            destinationsCSV.add(new Destination(data[0], data[1], data[2]));
        }
    }

    public String getDestinationType() {
        return destinationType;
    }
    public String getAddress() {
        return address;
    }

    public String getCountyName() {
        return countyName;
    }

    public static ArrayList<String> getDestinationsByCountyCsv(String countyName) throws IOException {
        ArrayList<String> countyDestinations = new  ArrayList<>();
        for (Destination destinationCSV : destinationsCSV) {
            if (destinationCSV.getCountyName().equals(countyName)) {
                countyDestinations.add(destinationCSV.getDestinationType());
            }
        }
        return countyDestinations;
    }
    public static Destination stringToDest(String placeName){
        Destination returnDest = null;
        for (Destination destination : destinationsCSV) {
            if (destination.getDestinationType().equals(placeName)) {
                returnDest = destination;
            }
        }
        return returnDest;
    }
    public static ArrayList<String> getListToString(){
        ArrayList<String> list = new ArrayList<>();
        for (Pair<Destination, Integer> destination : destinationlist) {
            list.add(destination.getKey().getDestinationType());
        }
        return list;
    }

}
