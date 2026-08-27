# AlgorithmProject

# Graph Viewer – Shortest Path & GPS Simulation

A Java Swing application that visualizes a graph of "server halls" (nodes), computes the shortest path between them using **Dijkstra's algorithm**, and animates a car moving along the computed path. The application can also simulate GPS deviations between nodes' original and updated positions and compute the **Hausdorff distance** between the two point sets.

## Features

- 🗺️ **Random graph generation** – creates nodes (server halls) and edges in a grid-like layout with slight random offsets.
- 📍 **Shortest path (Dijkstra)** – select a start and end node in the UI and compute the shortest distance and path between them.
- 🚗 **Car animation** – a car icon animates along the computed shortest path.
- 🎨 **Color themes** – switch between several themes (Random, Pastel, Dark, Neon) for the node colors.
- 🔍 **Zoom and drag** – zoom in/out on the graph and drag nodes to reposition them manually.
- 📡 **GPS simulation** – simulates new ("measured") positions for the nodes and visualizes the deviation from the original positions, with a warning when the deviation is too large.
- 💾 **Export image** – save the rendered graph as a PNG image.
- ⏱️ **Performance testing** – a separate test class that measures the runtime of Dijkstra's algorithm and the Hausdorff distance calculation across different graph sizes.

## Project Structure

```
src/
├── app/
│   ├── Main.java        # Entry point, opens the GUI window
│   ├── GUI.java          # Swing interface, rendering and interaction
│   └── car1.png          # Image used for the car animation
└── graph/
    ├── Graph.java             # Graph implementation, Dijkstra & Hausdorff
    ├── GraphInterface.java    # Interface for the graph
    ├── Edge.java               # Edge between two nodes
    ├── ServerHall.java         # Node in the graph (position + info)
    ├── UpdatedPosition.java    # Simulated/updated position for a node
    ├── GraphHelper.java        # Helper methods, e.g. simulating GPS movement
    └── TimeTest.java           # Performance tests for Dijkstra and Hausdorff distance
```

## Requirements

- Java Development Kit (JDK) 8 or later
- No external dependency management required (standard library only, including `javax.swing`)

## Build and Run

Clone the repository and run the following from the project root:

```bash
# Compile all source files
javac -d out $(find src -name "*.java")

# Copy the image resource to the correct location in the output folder
cp src/app/car1.png out/app/

# Run the application
java -cp out app.Main
```

### Running the performance test

```bash
java -cp out graph.TimeTest
```

This prints the average runtime of Dijkstra's algorithm and the Hausdorff distance calculation for different node counts (100, 500, 1000, 2000, 4000).

## Usage

1. Start the application – a window opens with a randomly generated graph.
2. Select a **Start** and **Target** node from the dropdown menus.
3. Click **"Calculate shortest path"** to highlight the shortest path and start the car animation.
4. Use **"Generate new graph"** to create a new random graph.
5. Click **"Save as image"** to export the current graph view as `graf_export.png`.
6. Change the appearance via the theme selector or **"Randomize colors"**.
7. Zoom with the **+**/**-** buttons, or drag nodes directly on the canvas to move them.
8. Click **"Show GPS movements"** to simulate deviating GPS positions and visualize how much each node has "moved" compared to its original position (with a warning for excessive deviation).

## Algorithms

- **Dijkstra's algorithm** is used to find the shortest path between two nodes in the graph, based on the Euclidean distance between nodes as edge weight.
- **Hausdorff distance** is used to measure the greatest deviation between a set of original positions and a set of simulated/updated positions – a metric commonly used to compare how similar two point sets are.

## License

Add a license of your choice here (e.g. MIT) if the project should be open source.
