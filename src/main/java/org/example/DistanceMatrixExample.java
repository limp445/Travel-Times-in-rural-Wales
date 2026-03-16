package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DistanceMatrixExample {
    static int secToHr(int seconds){
        return seconds/3600;
    }
    static int secToRemainMinutes(int seconds){
        return seconds%3600;
    }
    public static void main(String[] args) throws Exception {
        String origins = "London SW1A 1AA,United Kingdom";
        String destinations = "Oxford,United Kingdom";
        String key = "hSDDUzoJ33JfbRdRDqaiLwoC4qEAkh5OxNFaTcJidQMOgcfHeQRdtC0sWbiyMtq1";

        String url = "https://api.distancematrix.ai/maps/api/distancematrix/json"
                + "?origins=" + java.net.URLEncoder.encode(origins, "UTF-8")
                + "&destinations=" + java.net.URLEncoder.encode(destinations, "UTF-8")
                + "&mode=driving"
                + "&departure_time=now"
                + "&traffic_model=best_guess"
                + "&key=" + key;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        JsonObject element = root.getAsJsonArray("rows").get(0).getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();

        int distanceMeters = element.getAsJsonObject("distance").get("value").getAsInt();

        int durationSeconds = element.getAsJsonObject("duration_in_traffic").get("value").getAsInt();
        String time = element.getAsJsonObject("duration_in_traffic").get("text").getAsString();// change to duration if not wanting traffic

        System.out.println(distanceMeters);
        System.out.println(durationSeconds);
        System.out.println(secToHr(durationSeconds) + secToRemainMinutes(durationSeconds));
        System.out.println(time);



        System.out.println(response.body());
    }
    public int getDistance(){

        return 0;
    }
}