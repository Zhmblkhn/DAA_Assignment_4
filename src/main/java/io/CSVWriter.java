package io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CSVWriter {

    private final String path;

    public CSVWriter(String path) {
        this.path = path;
    }

    public void writeHeader() throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("File,GraphID,Vertices,Edges,Cyclic,Algorithm,Time(ms),Ops,CriticalPathLen\n");
        }
    }

    public void appendRow(String file, int graphId, int vertices, int edges, boolean cyclic,
                          String algorithm, double timeMs, int ops, Double criticalPathLen) throws IOException {

        try (FileWriter fw = new FileWriter(path, true)) {
            String row = String.format(Locale.US,
                    "%s,%d,%d,%d,%b,%s,%.3f,%d,%s\n",
                    file,
                    graphId,
                    vertices,
                    edges,
                    cyclic,
                    algorithm,
                    timeMs,
                    ops,
                    (criticalPathLen == null ? "" : String.format(Locale.US, "%.3f", criticalPathLen))
            );
            fw.write(row);
        }
    }
}
