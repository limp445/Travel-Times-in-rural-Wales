package org.example;

import java.util.*;

public class WeightedGraph {
    static class Edge {
        String source;
        String destination;
        int Time; // represents the weight of a edge

        public Edge(String source, String destination, int Time) {
            this.source = source;
            this.destination = destination;
            this.Time = Time;
        }
    }

    static class Graph {
        int vertices;
        ArrayList<Edge> allEdges = new ArrayList<>();

        Graph(int vertices) {
            this.vertices = vertices;
        }

        public void addEgde(String source, String destination, int Time) {
            allEdges.add(new Edge(source, destination, Time));
            allEdges.add(new Edge(destination, source, Time));//both ways
        }

        public void printGraph(){
            for (int i = 0; i <allEdges.size() ; i++) {
                Edge edge = allEdges.get(i);
                System.out.println("Edge-" + i + " source: " + edge.source +
                        " destination: " + edge.destination +
                        " Time: " + edge.Time);
            }
        }


        public static void main(String[] args) {
            int vertices = 6;
            Graph graph = new Graph(vertices);
            graph.addEgde("A", "B", 4);
            graph.addEgde("A", "C", 3);
            graph.addEgde("B", "D", 2);
            graph.addEgde("B", "C", 5);
            graph.addEgde("C", "D", 7);
            graph.addEgde("D", "E", 2);
            graph.addEgde("E", "A", 4);
            graph.addEgde("E", "B", 4);
            graph.addEgde("E", "F", 6);
            graph.printGraph();
        }
    }
}


