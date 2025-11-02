package graph.topo;

import util.Metrics;
import java.util.*;

public class TopologicalSort {

    private final Map<Integer, List<Integer>> graph;
    private final Metrics metrics;

    public TopologicalSort(Map<Integer, List<Integer>> graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<Integer> sort() {
        metrics.startTimer();

        Map<Integer, Integer> indegree = new HashMap<>();
        for (int node : graph.keySet()) indegree.put(node, 0);
        for (List<Integer> edges : graph.values()) {
            for (int v : edges) indegree.put(v, indegree.getOrDefault(v, 0) + 1);
        }

        Queue<Integer> q = new ArrayDeque<>();
        for (var entry : indegree.entrySet())
            if (entry.getValue() == 0) q.add(entry.getKey());

        List<Integer> order = new ArrayList<>();

        while (!q.isEmpty()) {
            int u = q.poll();
            metrics.incrementCounter("queuePops");
            order.add(u);
            for (int v : graph.getOrDefault(u, List.of())) {
                indegree.put(v, indegree.get(v) - 1);
                metrics.incrementCounter("queuePushes");
                if (indegree.get(v) == 0) q.add(v);
            }
        }

        metrics.stopTimer();
        return order;
    }
}
