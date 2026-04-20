package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class DistanceMatrix {
    public static boolean traffic = true;
    public static void changeTraffic(){
        traffic = !traffic;
    }
    public static JsonObject getJSONRequest(String origin, String destination) throws IOException, InterruptedException {
        String key = "hSDDUzoJ33JfbRdRDqaiLwoC4qEAkh5OxNFaTcJidQMOgcfHeQRdtC0sWbiyMtq1";

        String url = "https://api.distancematrix.ai/maps/api/distancematrix/json"
                + "?origins=" + java.net.URLEncoder.encode(origin, StandardCharsets.UTF_8)
                + "&destinations=" + java.net.URLEncoder.encode(destination, StandardCharsets.UTF_8)
                + "&mode=driving"
                + "&departure_time=now"
                + "&traffic_model=best_guess"
                + "&key=" + key;//needs changine to include traffic

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        return root.getAsJsonArray("rows").get(0).getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();
    }

    public static String getTimeAsString(double lat, double lng, String address) throws IOException, InterruptedException {
        JsonObject response = DistanceMatrix.getJSONRequest(lat + "," + lng, address);
        if (traffic){
            return response.getAsJsonObject("duration_in_traffic").get("text").getAsString();
        }else {
            return response.getAsJsonObject("duration").get("text").getAsString();
        }
    }
    public static String getDistanceAsString(double lat, double lng, String address) throws IOException, InterruptedException {
        JsonObject response = DistanceMatrix.getJSONRequest(lat + "," + lng, address);
        return response.getAsJsonObject("distance").get("text").getAsString();
    }
    public static Pair<Double, Double> addressToLatLng(String address) throws IOException, InterruptedException {
        Pair<Double, Double> LatLngPair;
        String key2 = "Mg0A04b5TpNTxXHsUGFkeZl7XC8DYosywmJ3jRmtacwYIHaoqHJlblxwKg8aUpLp";
        //String traffic = """&departure_time=now" + "&traffic_model=best_guess""";
        String url = "https://api.distancematrix.ai/maps/api/geocode/json"
                + "?address=" + java.net.URLEncoder.encode(address, StandardCharsets.UTF_8)
                + "&key=" + key2;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        JsonObject element = root.getAsJsonArray("result")
                .get(0).getAsJsonObject()
                .getAsJsonObject("geometry")
                .getAsJsonObject("location");

        double lat = element.get("lat").getAsDouble();
        double lng = element.get("lng").getAsDouble();
        LatLngPair = new Pair<>(lat, lng);
        return LatLngPair;
    }
}
