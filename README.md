# Design and Analysis of Algorithms 

---
## Assignment 4: Smart City / Smart Campus Scheduling
This project implements **Strongly Connected Components (SCC)**, **Topological Sorting**, and **Shortest/Longest Paths in DAGs** to model and analyze task scheduling for city services and internal analytics subtasks.  

The project is implemented in **Java (Maven)** and includes:
- SCC detection using **Tarjan’s algorithm** and condensation to a DAG;
- Topological sorting of the condensation DAG using **Kahn's algorithm**;
- DAG shortest and longest path computation for scheduling analysis;
- Performance measurement (execution time, operation counters, critical path lengths);
- Dataset generation and automated tests for correctness and edge cases.
---
## 1. Project Structure
src/main/java/ <br>
├── app/ <br>
│ └── AppMain.java ← Main entry: runs SCC, Topo, DAG-SP and exports metrics <br>
├── generator/ <br>
│ └── DatasetGenerator.java ← Generates small, medium, large graphs <br>
├── graph/ <br>
│ ├── scc/ <br>
│ │ ├── TarjanSCC.java <br>
│ │ └── Condensation.java <br>
│ ├── topo/ <br>
│ │ └── TopologicalSort.java <br>
│ └── dagsp/ <br>
│ └── DAGShortestPath.java <br>
├── io/ <br>
│ ├── CSVWriter.java <br>
│ └── GraphLoader.java <br>
└── util/ <br>
├── Metrics.java <br>
└── BasicMetrics.java <br>

src/test/java/ <br>
├── GraphIntegrationTest.java <br>
├── GraphLoaderTest.java <br>
├── SCCAlgorithmTest.java <br>
├── TopologicalSortTest.java <br>
└── DAGSPTest.java (optional) <br>

data/ <br>
├── small_.json <br>
├── medium_.json <br>
└── large_*.json <br>

data/results/ <br>
└── metrics_analytics.csv <br>
---

## 2. Input Data Description

Input data represents graphs in JSON format with:

- `"nodes"` — number of tasks;
- `"edges"` — directed dependencies between tasks;
- `"weights"` — edge weights.

| File | GraphID | Nodes | Edges | Cyclic |
|------|---------|-------|-------|--------|
| small_1.json | 7 | 9 | 21 | true |
| medium_2.json | 5 | 10 | 31 | false |
| large_2.json | 2 | 41 | 360 | false |

**Notes:**
- Small: 6–10 nodes, simple cycles or DAGs;
- Medium: 10–20 nodes, mixed SCC structures;
- Large: 20–50 nodes, performance and timing tests;
- Each dataset includes varying density levels.

---

## 3. Implementation Details

### 3.1 SCC (Tarjan)
- Detects strongly connected components.
- Builds condensation DAG from SCCs.
- Metrics tracked: DFS visits, DFS edges.
- Complexity: **O(V + E)**.

### 3.2 Topological Sort (Kahn)
- Computes valid execution order for DAGs (after SCC compression).
- Metrics tracked: queue pushes, queue pops.
- Complexity: **O(V + E)**.
- Returns empty list if graph contains cycles.

### 3.3 DAG Shortest / Longest Paths
- Weighted DAG uses **edge weights** (documented choice).
- Shortest paths: standard DAG relaxation in topological order.
- Longest path: max-DP over topo order.
- Metrics tracked: relaxations.
- Complexity: **O(V + E)**.
- Outputs critical path length for scheduling.

---

## 4. Run Instructions

> **How to build, test, and run the project**

### Using Maven (Recommended)

```bash
# Clean and compile the project
mvn clean compile

# Run the main application
mvn exec:java -Dexec.mainClass="app.AppMain"

# Run all JUnit tests
mvn test
```

## 5. Results Summary

### Summary Table (selected large/medium datasets)

| File | GraphID | Vertices | Edges | Cyclic | SCC Time(ms) | SCC Ops | Topo Time(ms) | Topo Ops | DAG-SP Time(ms) | DAG-SP Ops | CriticalPathLen |
|------|---------|----------|-------|--------|--------------|---------|---------------|----------|----------------|------------|----------------|
| large_1.json | 1 | 24 | 346 | true  | 1.489 | 370 | 0.453 | 0 | 0.000 | 0 | - |
| large_2.json | 2 | 41 | 360 | false | 0.736 | 401 | 1.169 | 401 | 0.040 | 4347 | 21.0 |
| medium_2.json | 5 | 10 | 31 | false | 0.132 | 41 | 0.093 | 41 | 0.010 | 109 | 8.0 |
| small_2.json | 8 | 10 | 23 | false | 0.049 | 33 | 0.068 | 33 | 0.010 | 64 | 5.0 |

---

## 6. Performance Analysis

### SCC
- Time grows linearly with **number of vertices + edges**.
- Larger SCCs slightly increase DFS stack usage and operations.
- Cyclic graphs have larger components → more DFS operations.

### Topological Sort
- Works only on DAGs.
- Time grows with **number of vertices + edges**.
- Cyclic graphs return empty order as expected.
- Observed queue pushes/pops match edge traversals.

### DAG Shortest / Longest Paths
- Only executed for acyclic graphs.
- Relaxation count correlates with edge count and topo order traversal.
- Critical path length represents longest execution chain for tasks.

---

### 6.1 Observations

- **Cyclic vs Acyclic:**
    - Cyclic graphs: DAG-SP not executed, Topo returns empty list.
    - Acyclic graphs: DAG-SP computes shortest/longest paths correctly.

- **Dataset size effect:**
    - Larger graphs → more SCC ops and time.
    - Edge density more important than vertex count for DAG-SP relaxations.

- **Example:** `large_2.json` (41 vertices, 360 edges)
    - SCC: 0.736 ms, 401 ops
    - Topo: 1.169 ms, 401 ops
    - DAG-SP: 0.040 ms, 4347 relaxations, critical path 21

- **Small graphs:** DAG-SP completes <0.01 ms, confirming scalability for lightweight cases.

---

## 7. Theoretical vs Practical Performance

| Algorithm | Complexity | Works Best For | Observed Behavior |
|-----------|------------|----------------|-----------------|
| **Tarjan SCC** | O(V+E) | Cyclic & acyclic graphs | Matches theory; larger SCCs increase ops |
| **Topological Sort** | O(V+E) | DAGs | Linear growth with edges; empty for cycles |
| **DAG-SP** | O(V+E) | DAG scheduling | Relaxations scale with edge count; critical path correctly detected |

- Dense or cyclic graphs increase SCC DFS operations.
- Sparse DAGs → fewer relaxations and faster execution.
- Execution times align with operation counts.

---

## 8. Dataset and Metrics Notes

- Generated datasets cover:
    - Small, Medium, Large graphs.
    - Mixed cyclic/acyclic structures.
    - Varying density levels.
- Metrics captured per algorithm:
    - SCC: DFS visits, DFS edges
    - Topo: queue pushes/pops
    - DAG-SP: relaxations, critical path length
    - Time measured with `System.nanoTime()` converted to ms

---

## 9. Testing

- **JUnit tests** validate:
    - Graph loading
    - SCC correctness (simple cycle, disconnected graph)
    - Topological sort correctness (simple DAG, cyclic detection)
    - DAG-SP path computations (optional tests)
- Ensures reproducibility and correctness for edge cases.

---

## 10. Conclusions

- Tarjan SCC efficiently detects cycles and builds condensation DAG.
- Kahn’s topological sort successfully orders DAG tasks; returns empty for cycles.
- DAG shortest/longest paths provide critical scheduling info for acyclic graphs.
- Operation counts correlate strongly with edge density and SCC sizes.
- Code is modular, testable, and scalable across small → large datasets.
- For cyclic graphs, preprocessing via SCC is essential to detect compressible components.

