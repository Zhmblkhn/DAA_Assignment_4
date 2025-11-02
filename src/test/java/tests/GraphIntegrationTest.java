package tests;

import io.GraphLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GraphIntegrationTest {

    @Test
    void testLoadCustomGraphs() throws IOException {
        String[] paths = {
                "test_data/empty.json",
                "test_data/single_node.json",
                "test_data/dense.json",
                "test_data/cycle.json"
        };

        for (String path : paths) {
            GraphLoader.Data data = GraphLoader.load(path);
            assertNotNull(data, "Graph data should not be null for " + path);

            // Проверяем количество вершин
            assertTrue(data.n >= 0, "Number of vertices should be non-negative for " + path);

            // Проверяем количество рёбер
            assertNotNull(data.edges, "Edges list should not be null for " + path);
            assertTrue(data.edges.size() >= 0, "Number of edges should be non-negative for " + path);

            // Простейшая проверка структуры: каждая вершина должна быть >= 0 и < n
            for (int[] edge : data.edges) {
                assertEquals(2, edge.length, "Each edge should have 2 elements for " + path);
                assertTrue(edge[0] >= 0 && edge[0] < data.n,
                        "Edge source out of bounds for " + path);
                assertTrue(edge[1] >= 0 && edge[1] < data.n,
                        "Edge target out of bounds for " + path);
            }

            // Проверяем массив весов
            assertNotNull(data.weights, "Weights array should not be null for " + path);
            assertEquals(data.edges.size(), data.weights.length,
                    "Weights array length must match number of edges for " + path);
        }
    }

    @Test
    void testFileNotFound() {
        String path = "test_data/non_existent.json";
        assertThrows(IOException.class, () -> GraphLoader.load(path));
    }
}
