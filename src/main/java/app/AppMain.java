package app;

import com.google.gson.JsonSyntaxException;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import io.CSVWriter;
import io.GraphLoader;
import util.BasicMetrics;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppMain {

    public static void main(String[] args) {
        String dataDir = "data";
        String outCsv = "data/results/metrics_analytics.csv";

        CSVWriter writer = new CSVWriter(outCsv);
        try {
            writer.writeHeader();
        } catch (IOException e) {
            System.err.println("Cannot create output CSV: " + e.getMessage());
            return;
        }

        File folder = new File(dataDir);
        File[] files = folder.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.err.println("No .json files found in " + dataDir);
            return;
        }

        Arrays.sort(files);
        int graphId = 1;

        for (File f : files) {
            System.out.println("Processing: " + f.getName());
            try {
                GraphLoader.Data data = GraphLoader.load(f.getPath());
                int n = data.n;
                int m = data.edges.size();

                Map<Integer, List<Integer>> adj = new HashMap<>();
                Map<Integer, List<int[]>> weighted = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    adj.put(i, new ArrayList<>());
                    weighted.put(i, new ArrayList<>());
                }
                for (int[] e : data.edges) {
                    int u = e[0], v = e[1];
                    adj.get(u).add(v);
                    int w = (e.length >= 3) ? e[2] : 1;
                    weighted.get(u).add(new int[]{v, w});
                }

                BasicMetrics sccMetrics = new BasicMetrics();
                TarjanSCC scc = new TarjanSCC(adj, sccMetrics);
                sccMetrics.startTimer();
                List<List<Integer>> components = scc.run();
                sccMetrics.stopTimer();

                boolean cyclic = components.stream().anyMatch(c -> c.size() > 1);

                long sccOps = sccMetrics.getCounter("dfsVisits") + sccMetrics.getCounter("dfsEdges");
                double sccTime = sccMetrics.getElapsedTimeNanos() / 1_000_000.0;

                writer.appendRow(f.getName(), graphId, n, m, cyclic, "SCC", sccTime, (int) sccOps, null);

                BasicMetrics topoMetrics = new BasicMetrics();
                TopologicalSort topo = new TopologicalSort(adj, topoMetrics);
                topoMetrics.startTimer();
                List<Integer> topoOrder = topo.sort();
                topoMetrics.stopTimer();

                long topoOps = topoMetrics.getCounter("queuePushes") + topoMetrics.getCounter("queuePops");
                double topoTime = topoMetrics.getElapsedTimeNanos() / 1_000_000.0;

                writer.appendRow(f.getName(), graphId, n, m, cyclic, "Topo", topoTime, (int) topoOps, null);

                BasicMetrics dagMetrics = new BasicMetrics();
                DAGShortestPath dagsp = new DAGShortestPath(weighted, dagMetrics);

                double criticalLen = Double.NEGATIVE_INFINITY;
                long dagOps = 0L;
                double dagTime = 0.0;

                if (!cyclic) {
                    dagMetrics.startTimer();

                    if (!topoOrder.isEmpty()) {
                        for (int start : topoOrder) {
                            Map<Integer, Double> longestFromStart = dagsp.longestPaths(start, topoOrder);
                            for (double val : longestFromStart.values()) {
                                if (val != Double.NEGATIVE_INFINITY && val > criticalLen) {
                                    criticalLen = val;
                                }
                            }
                        }
                    }

                    dagMetrics.stopTimer();
                    dagOps = dagMetrics.getCounter("relaxations");
                    dagTime = dagMetrics.getElapsedTimeNanos() / 1_000_000.0;
                } else {
                    dagMetrics.startTimer();
                    dagMetrics.stopTimer();
                    dagTime = dagMetrics.getElapsedTimeNanos() / 1_000_000.0;
                    dagOps = dagMetrics.getCounter("relaxations");
                }

                Double crit = (criticalLen == Double.NEGATIVE_INFINITY) ? null : criticalLen;
                writer.appendRow(f.getName(), graphId, n, m, cyclic, "DAGSP", dagTime, (int) dagOps, crit);

                graphId++;

            } catch (IOException | JsonSyntaxException ex) {
                System.err.println("Failed to load/parse " + f.getName() + ": " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error processing " + f.getName() + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        System.out.println("All done. Results written to " + outCsv);
    }
}
