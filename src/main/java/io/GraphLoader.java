package io;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Loads graphs in format:
 * {
 *   "meta": {
 *     "nodes": <int>,
 *     "cyclic": <bool>,
 *     "edges": <int>
 *   },
 *   "graph": {
 *     "0": [[1, 3], [2, 5]],
 *     "1": [],
 *     ...
 *   }
 * }
 */
public class GraphLoader {
    public static class Data {
        public final int n;
        public final List<int[]> edges;
        public final double[] weights;
        public double[] durations;

        public Data(int n, List<int[]> edges, double[] weights) {
            this.n = n;
            this.edges = edges;
            this.weights = weights;
        }
    }

    public static Data load(String path) throws IOException {
        JsonObject root = JsonParser.parseReader(new FileReader(path)).getAsJsonObject();

        // 1️⃣ Читаем количество узлов
        JsonObject meta = root.getAsJsonObject("meta");
        int n = meta.get("nodes").getAsInt();

        // 2️⃣ Читаем граф
        JsonObject graphObj = root.getAsJsonObject("graph");
        List<int[]> edges = new ArrayList<>();
        List<Double> weights = new ArrayList<>();

        for (String key : graphObj.keySet()) {
            int u = Integer.parseInt(key);
            JsonArray adjList = graphObj.getAsJsonArray(key);
            for (JsonElement e : adjList) {
                JsonArray pair = e.getAsJsonArray();
                int v = pair.get(0).getAsInt();
                double w = pair.get(1).getAsDouble();
                edges.add(new int[]{u, v});
                weights.add(w);
            }
        }

        // 3️⃣ Преобразуем веса в массив
        double[] weightsArr = new double[weights.size()];
        for (int i = 0; i < weights.size(); i++) {
            weightsArr[i] = weights.get(i);
        }

        return new Data(n, edges, weightsArr);
    }
}
