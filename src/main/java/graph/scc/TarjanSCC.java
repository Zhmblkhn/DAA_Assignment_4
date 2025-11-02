package graph.scc;

import util.Metrics;
import java.util.*;

public class TarjanSCC {
    private final Map<Integer, List<Integer>> graph;
    private final Metrics metrics;

    private final Map<Integer, Integer> ids = new HashMap<>();
    private final Map<Integer, Integer> low = new HashMap<>();
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Set<Integer> onStack = new HashSet<>();

    private final List<List<Integer>> sccList = new ArrayList<>();
    private int id = 0;

    public TarjanSCC(Map<Integer, List<Integer>> graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<List<Integer>> run() {
        metrics.startTimer();
        for (int node : graph.keySet()) {
            if (!ids.containsKey(node)) dfs(node);
        }
        metrics.stopTimer();
        return sccList;
    }

    private void dfs(int at) {
        metrics.incrementCounter("dfsVisits");

        ids.put(at, id);
        low.put(at, id++);
        stack.push(at);
        onStack.add(at);

        for (int to : graph.getOrDefault(at, List.of())) {
            metrics.incrementCounter("dfsEdges");
            if (!ids.containsKey(to)) {
                dfs(to);
                low.put(at, Math.min(low.get(at), low.get(to)));
            } else if (onStack.contains(to)) {
                low.put(at, Math.min(low.get(at), ids.get(to)));
            }
        }

        if (Objects.equals(ids.get(at), low.get(at))) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int node = stack.pop();
                onStack.remove(node);
                component.add(node);
                if (node == at) break;
            }
            sccList.add(component);
        }
    }

    public Map<Integer, List<Integer>> buildCondensation() {
        Map<Integer, Integer> nodeToScc = new HashMap<>();
        for (int i = 0; i < sccList.size(); i++) {
            for (int node : sccList.get(i)) nodeToScc.put(node, i);
        }

        Map<Integer, List<Integer>> dag = new HashMap<>();
        for (int i = 0; i < sccList.size(); i++) dag.put(i, new ArrayList<>());

        for (var entry : graph.entrySet()) {
            for (int to : entry.getValue()) {
                int a = nodeToScc.get(entry.getKey());
                int b = nodeToScc.get(to);
                if (a != b && !dag.get(a).contains(b)) dag.get(a).add(b);
            }
        }

        return dag;
    }
}
