package org.example;

import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

public class WeightedGraph {
    static class Edge {
        String source;
        String destination;
        int Time; // represents the travel time as weight of a edge

        public Edge(String source, String destination, int Time) {
            this.source = source;
            this.destination = destination;
            this.Time = Time;
        }
    }

    public static class Graph {
        int vertices;
        public static ArrayList<Edge> allEdges = new ArrayList<>();

        Graph(int vertices) {
            this.vertices = vertices;
        }

        public static void addEgde(String source, String destination, int Time) {
            allEdges.add(new Edge(source, destination, Time));
            allEdges.add(new Edge(destination, source, Time));//both ways
        }

        public static void printGraph(){
            for (int i = 0; i <allEdges.size() ; i++) {
                Edge edge = allEdges.get(i);
                System.out.println("Edge-" + i + " source: " + edge.source +
                        " destination: " + edge.destination +
                        " Time: " + edge.Time);
            }
        }
        public static void populateGraph() throws IOException, InterruptedException {
            int vertices = Destination.destinationlist.size();
            String d = "";
            ArrayList<Pair<Double, Double>> latLngs = new ArrayList<>();
            for (int i = 0; i < vertices; i++) {
                latLngs.add(DistanceMatrix.addressToLatLng(Destination.destinationlist.get(i).getKey().getDestinationType()));
            }
            for (int i = 0; i < vertices; i++) {
                addEgde("Origin", Destination.destinationlist.get(i).getKey().getDestinationType(), Destination.destinationlist.get(i).getValue());
                String s = Destination.destinationlist.get(i).getKey().getDestinationType();
                Pair<Double, Double> ps = latLngs.get(i);
                for (int j = 0; j < vertices; j++) {
                    if (j == i) {
                        continue;
                    }
                    d = Destination.destinationlist.get(j).getKey().getDestinationType();
                    addEgde(s,d, DistanceMatrix.getTimeAsInt(ps.getKey(), ps.getValue(), d));
                    addEgde(d,s, DistanceMatrix.getTimeAsInt(latLngs.get(j).getKey(), latLngs.get(j).getValue(), s));
                }
            }

        }
        private static ArrayList<ArrayList<String>> permatations(ArrayList<String> list){
            if (list.size() == 1) {
                ArrayList<ArrayList<String>> base = new ArrayList<>();
                base.add(new ArrayList<>(list));
                return base;
            }
            ArrayList<ArrayList<String>> result = new ArrayList<>();

            for (String destination : list) {
                ArrayList<String> remain = new ArrayList<>(list);
                remain.remove(destination);
                ArrayList<ArrayList<String>> sub = permatations(remain);
                for (ArrayList<String> subList : sub) {
                    ArrayList<String> newList = new ArrayList<>();
                    newList.add(destination);
                    newList.addAll(subList);
                    result.add(newList);
                }

            }
            return result;
        }
        private static Integer getTime(String A, String B){
            for (Edge e : allEdges) {
                if(e.source.equals(A) && e.destination.equals(B)){
                    return e.Time;
                }
            }
            return null;
        }
        public static Pair<ArrayList<String>, Integer> routeFind() throws NullPointerException{
            ArrayList<String> nodeList = new ArrayList<>();
            for (int i = 0; i < Destination.destinationlist.size(); i++) {
                nodeList.add(Destination.destinationlist.get(i).getKey().getDestinationType());
            }
            ArrayList<ArrayList<String>> base = permatations(nodeList);
            ArrayList<ArrayList<String>> routes = new ArrayList<>();
            for (ArrayList<String> route : base) {
                ArrayList<String> newRoute = new ArrayList<>();
                newRoute.add("Origin");
                newRoute.addAll(route);
                routes.add(newRoute);
            }
            ArrayList<String> fastestRoute = null;
            int fastestTime = Integer.MAX_VALUE;
            for (ArrayList<String> route : routes) {
                int total = 0;
                for (int i = 0; i < route.size()-1; i++) {
                    Integer t = getTime(route.get(i), route.get(i+1));
                    total += t;
                }
                if (total < fastestTime) {
                    fastestRoute = route;
                    fastestTime = total;
                }
            }
            return new Pair<>(fastestRoute, fastestTime);

        }
    }
}


