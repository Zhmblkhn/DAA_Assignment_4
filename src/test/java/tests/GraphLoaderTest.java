package tests;

import io.GraphLoader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GraphLoaderTest {

    @Test
    void testLoadSmallGraph() throws IOException {
        String path = "data/small_1.json";
        GraphLoader.Data data = GraphLoader.load(path);

        assertNotNull(data, "Graph data should not be null");
        assertEquals(9, data.n, "Number of vertices should match");
        assertEquals(21, data.edges.size(), "Number of edges should match");

        int[] firstEdge = data.edges.get(0);
        assertEquals(0, firstEdge[0]);
        assertEquals(1, firstEdge[1]);
    }

    @Test
    void testFileNotFound() {
        String path = "test_data/non_existent.json";
        assertThrows(IOException.class, () -> GraphLoader.load(path));
    }
}
