package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DistanceMatrix {
    public static JsonObject getJSONRequest(String origin, String destination) throws IOException, InterruptedException {
        String key = "hSDDUzoJ33JfbRdRDqaiLwoC4qEAkh5OxNFaTcJidQMOgcfHeQRdtC0sWbiyMtq1";

        String url = "https://api.distancematrix.ai/maps/api/distancematrix/json"
                + "?origins=" + java.net.URLEncoder.encode(origin, "UTF-8")
                + "&destinations=" + java.net.URLEncoder.encode(destination, "UTF-8")
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

        return root.getAsJsonArray("rows").get(0).getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();
    }

    public static String getTimeAsString() throws IOException, InterruptedException {
        JsonObject response = DistanceMatrix.getJSONRequest("London SW1A 1AA,United Kingdom", "71 Newton Road Great Barr Birmingham B43 6AD,United Kingdom");
        return response.getAsJsonObject("duration").get("text").getAsString();
    }
    public static String getDistanceAsString() throws IOException, InterruptedException {
        JsonObject response = DistanceMatrix.getJSONRequest("London SW1A 1AA,United Kingdom", "Oxford,United Kingdom");
        return response.getAsJsonObject("distance").get("text").getAsString();
    }
}
