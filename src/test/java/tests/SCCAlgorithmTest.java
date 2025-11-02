package tests;

import graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;
import util.BasicMetrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SCCAlgorithmTest {

    @Test
    void testSCCOnSimpleCycle() {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int i = 0; i < 3; i++) adj.put(i, new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);
        adj.get(2).add(0);

        BasicMetrics metrics = new BasicMetrics();
        TarjanSCC scc = new TarjanSCC(adj, metrics);
        List<List<Integer>> components = scc.run();

        assertEquals(1, components.size(), "All nodes should be in one SCC");
        assertTrue(components.get(0).containsAll(Arrays.asList(0, 1, 2)));
    }

    @Test
    void testSCCOnDisconnectedGraph() {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int i = 0; i < 3; i++) adj.put(i, new ArrayList<>());

        BasicMetrics metrics = new BasicMetrics();
        TarjanSCC scc = new TarjanSCC(adj, metrics);
        List<List<Integer>> components = scc.run();

        assertEquals(3, components.size(), "Each node should form its own SCC");
    }
}
