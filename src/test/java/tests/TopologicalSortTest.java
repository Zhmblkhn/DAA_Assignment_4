package tests;

import graph.topo.TopologicalSort;
import org.junit.jupiter.api.Test;
import util.BasicMetrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortTest {

    @Test
    void testSimpleDAG() {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int i = 0; i < 3; i++) adj.put(i, new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);

        BasicMetrics metrics = new BasicMetrics();
        TopologicalSort topo = new TopologicalSort(adj, metrics);
        List<Integer> order = topo.sort();

        assertEquals(3, order.size(), "Topological order should include all nodes");
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
    }

    @Test
    void testTopoOnCycle() {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int i = 0; i < 3; i++) adj.put(i, new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);
        adj.get(2).add(0);

        BasicMetrics metrics = new BasicMetrics();
        TopologicalSort topo = new TopologicalSort(adj, metrics);
        List<Integer> order = topo.sort();

        assertTrue(order.isEmpty(), "Topological sort on cyclic graph should return empty list");
    }
}
