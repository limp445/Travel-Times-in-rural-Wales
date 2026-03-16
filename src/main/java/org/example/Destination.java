package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Destination {
    private final String destinationType;
    private final String address;
    private final String countyName;
    public ArrayList<Destination> destinationlist;
    public ArrayList<Destination> destinationsCSV;

    public Destination(String destinationType, String address, String countyName) throws IOException {
        this.destinationType = destinationType;
        this.address = address;
        this.countyName = countyName;
        destinationlist = new ArrayList<>();
        destinationsCSV = new ArrayList<>();//just hospitals atm
        BufferedReader br = new BufferedReader(new FileReader("Hospitals.csv"));
        String line;
        while ((line = br.readLine()) != null){
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
    public void newDestination(String destinationType, String address, String countyName) throws IOException {
        Destination destination = new Destination(destinationType, address, countyName);
        destinationlist.add(destination);
    }
    public void removeDestination(String destinationType, String address, String countyName){
        for (Destination destination : destinationlist) {
            if (destination.destinationType.equals(destinationType) && destination.address.equals(address) && destination.countyName.equals(countyName)) {
                destinationlist.remove(destination);
            }
        }
    }

    public ArrayList<Destination> getDestinationsByCountyCsv(String countyName) throws IOException {
        ArrayList<Destination> countyDestinations = new  ArrayList<>();
        for (Destination destinationCSV : destinationsCSV) {
            if (destinationCSV.getCountyName().equals(countyName)) {
                countyDestinations.add(destinationCSV);
            }
        }
        return countyDestinations;
    }

}
