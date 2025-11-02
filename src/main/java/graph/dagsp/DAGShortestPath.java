package graph.dagsp;

import graph.topo.TopologicalSort;
import util.Metrics;

import java.util.*;

public class DAGShortestPath {
    private final Map<Integer, List<int[]>> weightedGraph;
    private final Metrics metrics;

    public DAGShortestPath(Map<Integer, List<int[]>> weightedGraph, Metrics metrics) {
        this.weightedGraph = weightedGraph;
        this.metrics = metrics;
    }

    public Map<Integer, Double> shortestPaths(int source, List<Integer> topoOrder) {
        metrics.startTimer();

        Map<Integer, Double> dist = new HashMap<>();
        for (int node : weightedGraph.keySet()) dist.put(node, Double.POSITIVE_INFINITY);
        dist.put(source, 0.0);

        for (int u : topoOrder) {
            if (dist.get(u) != Double.POSITIVE_INFINITY) {
                for (int[] edge : weightedGraph.getOrDefault(u, List.of())) {
                    int v = edge[0];
                    double w = edge[1];
                    double newDist = dist.get(u) + w;
                    metrics.incrementCounter("relaxations");
                    if (newDist < dist.get(v)) dist.put(v, newDist);
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }

    public Map<Integer, Double> longestPaths(int source, List<Integer> topoOrder) {
        metrics.startTimer();

        Map<Integer, Double> dist = new HashMap<>();
        for (int node : weightedGraph.keySet()) dist.put(node, Double.NEGATIVE_INFINITY);
        dist.put(source, 0.0);

        for (int u : topoOrder) {
            if (dist.get(u) != Double.NEGATIVE_INFINITY) {
                for (int[] edge : weightedGraph.getOrDefault(u, List.of())) {
                    int v = edge[0];
                    double w = edge[1];
                    double newDist = dist.get(u) + w;
                    metrics.incrementCounter("relaxations");
                    if (newDist > dist.get(v)) dist.put(v, newDist);
                }
            }
        }

        metrics.stopTimer();
        return dist;
    }
}
