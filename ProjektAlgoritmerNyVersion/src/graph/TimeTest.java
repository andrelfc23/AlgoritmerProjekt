package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.*;

public class TimeTest {

	public static void main(String[] args) {

		int[] nodeSizes = { 100, 500, 1000, 2000, 4000 }; 
		int repetitions = 5;

		for (int size : nodeSizes) {
			double totalTimeDijkras = 0;
			double totalTimeHaussdorf = 0;

			for (int i = 0; i < repetitions; i++) {
				totalTimeDijkras += runDijktrasTest(size, size * 3);
				totalTimeHaussdorf += runHausdorffTest(size);

			}

			double averageTimeDijktras = totalTimeDijkras / repetitions;
			double averageTimeHausdorf = totalTimeHaussdorf / repetitions;
			System.out.printf("Noder: %d, GenomsnittstidDijktras: %.2f ms%n", size, averageTimeDijktras);
			System.out.printf("Noder: %d, GenomsnittstidHausdorf: %.2f ms%n", size, averageTimeHausdorf);
		}
	}

	public static double runDijktrasTest(int numNodes, int numEdges) {

		Graph<String> graph = new Graph<>();
		Random rand = new Random();

		
		for (int i = 0; i < numNodes; i++) {
			double x = rand.nextInt(1000);
			double y = rand.nextInt(1000);
			graph.addServerHall(x, y, "N" + i);
		}

		
		Set<String> usedEdges = new HashSet<>();
		while (usedEdges.size() < numEdges) {
			int from = rand.nextInt(numNodes);
			int to = rand.nextInt(numNodes);
			if (from != to) {
				String key = from + "-" + to;
				String rev = to + "-" + from;
				if (!usedEdges.contains(key) && !usedEdges.contains(rev)) {
					try {
						graph.addEdge("N" + from, "N" + to);
						usedEdges.add(key);
					} catch (IllegalArgumentException ignored) {
						
					}
				}
			}
		}

		String startNode = "N" + rand.nextInt(numNodes);

		long startTime = System.nanoTime();
		graph.shortestPaths(startNode);
		long endTime = System.nanoTime();

		return (endTime - startTime) / 1_000_000.0; 
	}
	
	public static double runHausdorffTest(int numPoints) {
	    Random rand = new Random();
	    List<ServerHall<String>> original = new ArrayList<>();
	    List<UpdatedPosition<String>> updated = new ArrayList<>();

	    for (int i = 0; i < numPoints; i++) {
	        double x = rand.nextDouble() * 1000;
	        double y = rand.nextDouble() * 1000;
	        original.add(new ServerHall<>(x, y, "P" + i));

	       
	        double dx = x + rand.nextGaussian(); 
	        double dy = y + rand.nextGaussian();
	        updated.add(new UpdatedPosition<>(dx, dy, "P" + i));
	    }

	    Graph<String> graph = new Graph<>();

	    long start = System.nanoTime();
	    graph.calculateHausdorffDistance(original, updated);
	    long end = System.nanoTime();

	    return (end - start) / 1_000_000.0; 
	}


}
