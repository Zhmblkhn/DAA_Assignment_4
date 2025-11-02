package graph.scc;

import java.util.*;

public class Condensation {
    private final List<List<Integer>> compAdj;
    private final int compCount;
    private final double[] compDurations;

    /**
     * @param origAdj original adjacency (List<List<Integer>>)
     * @param components SCCs (List of lists of node ids)
     * @param nodeDurations durations per original node (length = origAdj.size())
     */
    public Condensation(List<List<Integer>> origAdj, List<List<Integer>> components, double[] nodeDurations) {
        int n = origAdj.size();
        this.compCount = components.size();
        int[] compOf = new int[n];
        for (int i = 0; i < components.size(); i++) {
            for (int v : components.get(i)) compOf[v] = i;
        }
        compAdj = new ArrayList<>(compCount);
        for (int i = 0; i < compCount; i++) compAdj.add(new ArrayList<>());
        Set<Long> seen = new HashSet<>();
        for (int u = 0; u < n; u++) {
            for (int v : origAdj.get(u)) {
                int a = compOf[u], b = compOf[v];
                if (a != b) {
                    long key = (((long) a) << 32) | (b & 0xffffffffL);
                    if (!seen.contains(key)) {
                        compAdj.get(a).add(b);
                        seen.add(key);
                    }
                }
            }
        }

        compDurations = new double[compCount];
        Arrays.fill(compDurations, 0.0);
        for (int i = 0; i < components.size(); i++) {
            for (int v : components.get(i)) {
                compDurations[i] += nodeDurations[v];
            }
        }
    }

    public List<List<Integer>> getCompAdj() { return compAdj; }
    public int getCompCount() { return compCount; }
    public double[] getCompDurations() { return compDurations; }
}
