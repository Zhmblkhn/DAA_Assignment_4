package generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DatasetGenerator {

    private static final Random rand = new Random();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        generateAll();
        System.out.println("All datasets generated in /data/");
    }

    public static void generateAll() throws IOException {
        new java.io.File("data").mkdirs();

        generateSet("small", 3, 6, 10);
        generateSet("medium", 3, 10, 20);
        generateSet("large", 3, 20, 50);
    }

    private static void generateSet(String prefix, int count, int minNodes, int maxNodes) throws IOException {
        for (int i = 1; i <= count; i++) {
            int n = randBetween(minNodes, maxNodes);
            boolean cyclic = (i % 2 == 1);
            double density = 0.25 + rand.nextDouble() * 0.5;

            Map<Integer, List<int[]>> graph = generateGraph(n, density, cyclic);
            int edgeCount = countEdges(graph);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("nodes", n);
            meta.put("edges", edgeCount);
            meta.put("cyclic", cyclic);

            Map<String, Object> root = new LinkedHashMap<>();
            root.put("meta", meta);
            root.put("graph", graph);

            String filename = String.format("data/%s_%d.json", prefix, i);
            try (FileWriter fw = new FileWriter(filename)) {
                gson.toJson(root, fw);
            }

            System.out.printf("Generated %-12s | nodes=%d | edges=%d | cyclic=%b | density=%.2f%n",
                    filename, n, edgeCount, cyclic, density);
        }
    }

    private static Map<Integer, List<int[]>> generateGraph(int n, double density, boolean cyclic) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) graph.put(i, new ArrayList<>());

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (rand.nextDouble() < density) {
                    int weight = randBetween(1, 10);

                    if (!cyclic && j <= i) continue;

                    graph.get(i).add(new int[]{j, weight});
                }
            }
        }

        if (cyclic && n >= 3) {
            int a = rand.nextInt(n - 1);
            int b = a + 1;
            graph.get(b).add(new int[]{a, randBetween(1, 10)});
        }

        return graph;
    }

    private static int countEdges(Map<Integer, List<int[]>> g) {
        int total = 0;
        for (List<int[]> e : g.values()) total += e.size();
        return total;
    }

    private static int randBetween(int a, int b) {
        return a + rand.nextInt(b - a + 1);
    }
}
