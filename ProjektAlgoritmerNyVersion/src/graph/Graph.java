package graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class Graph<T> implements GraphInterface<T> {

	private int nServerHalls;
	private int nEdges;
	private HashMap<T, ServerHall<T>> serverHalls;
	private HashMap<T, ArrayList<Edge<T>>> edges;

	public Graph() {
		this.serverHalls = new HashMap<>();
		this.edges = new HashMap<>();
	}

	@Override
	public List<ServerHall<T>> getAllServerHalls() {

		return new ArrayList<>(serverHalls.values());
	}

	@Override
	public List<Edge<T>> getEdges(T info) {

		return edges.getOrDefault(info, new ArrayList<>());
	}

	@Override
	public ServerHall<T> getServerHall(T info) {

		ServerHall<T> servHall = serverHalls.get(info);

		if (servHall == null) {
			throw new IllegalArgumentException("ServerHallen" + info + "finns ej");
		}

		return servHall;

	}

	@Override
	public List<Edge<T>> getAllEdges() {

		List<Edge<T>> allEdges = new ArrayList<>();

		for (List<Edge<T>> edgeList : edges.values()) {
			allEdges.addAll(edgeList);
		}

		return allEdges;
	}

	@Override
	public Map<ServerHall<T>, Double> shortestPaths(T startInfo) {

		ServerHall<T> start = getServerHall(startInfo);

		if (start == null) {
			throw new IllegalArgumentException("Startnoden finns inte");
		}

		return dijktras(start);
	}

	// Privat hjälpfunktion för dijkatras
	private Map<ServerHall<T>, Double> dijktras(ServerHall<T> start) {

		Map<ServerHall<T>, Double> distances = new HashMap<>();
		Set<ServerHall<T>> visited = new HashSet<>();
		PriorityQueue<ServerHall<T>> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

		for (ServerHall<T> node : serverHalls.values()) {
			distances.put(node, Double.POSITIVE_INFINITY);
		}

		distances.put(start, 0.0);
		queue.add(start);

		while (!queue.isEmpty()) {
			ServerHall<T> current = queue.poll();
			if (!visited.add(current))
				continue;

			List<Edge<T>> neighbors = edges.getOrDefault(current.getInfo(), new ArrayList<>());
			for (Edge<T> edge : neighbors) {
				ServerHall<T> neighbor = edge.getTo();
				double newDist = distances.get(current) + edge.getDistance();
				if (newDist < distances.get(neighbor)) {
					distances.put(neighbor, newDist);
					queue.add(neighbor);
				}
			}
		}

		return distances;

	}

	public List<Edge<T>> getShortestPathEdges(T fromInfo, T toInfo) {
		ServerHall<T> start = getServerHall(fromInfo);
		ServerHall<T> end = getServerHall(toInfo);

		Map<ServerHall<T>, Double> distances = new HashMap<>();
		Map<ServerHall<T>, ServerHall<T>> previous = new HashMap<>();
		Set<ServerHall<T>> visited = new HashSet<>();
		PriorityQueue<ServerHall<T>> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

		for (ServerHall<T> node : serverHalls.values()) {
			distances.put(node, Double.POSITIVE_INFINITY);
		}

		distances.put(start, 0.0);
		queue.add(start);

		while (!queue.isEmpty()) {
			ServerHall<T> current = queue.poll();
			if (!visited.add(current))
				continue;

			for (Edge<T> edge : edges.getOrDefault(current.getInfo(), new ArrayList<>())) {
				ServerHall<T> neighbor = edge.getTo();
				double newDist = distances.get(current) + edge.getDistance();
				if (newDist < distances.get(neighbor)) {
					distances.put(neighbor, newDist);
					previous.put(neighbor, current);
					queue.add(neighbor);
				}
			}
		}

		
		List<Edge<T>> pathEdges = new ArrayList<>();
		ServerHall<T> current = end;
		while (previous.containsKey(current)) {
			ServerHall<T> prev = previous.get(current);
			for (Edge<T> edge : edges.get(prev.getInfo())) {
				if (edge.getTo().equals(current)) {
					pathEdges.add(0, edge); // Lägg först
					break;
				}
			}
			current = prev;
		}

		return pathEdges;
	}

	@Override
	public double getDistance(T from, T to) {

		List<Edge<T>> edgeFrom = edges.get(from);

		if (edgeFrom != null) {
			for (Edge<T> edge : edgeFrom) {
				if (edge.getTo().getInfo().equals(to)) {
					return edge.getDistance();
				}
			}
		}

		return Double.POSITIVE_INFINITY;
	}
	

	@Override
	public void addServerHall(double x, double y, T info) {

		if (serverHalls.containsKey(info)) {
			throw new IllegalArgumentException("Serverhallen finns redan");
		} else {
			ServerHall<T> hall = new ServerHall<>(x, y, info);

			serverHalls.put(info, hall);
			edges.put(info, new ArrayList<>());
			nServerHalls++;
		}
	}
	

	@Override
	public void addEdge(T infoA, T infoB) {

		ServerHall<T> hall = serverHalls.get(infoA);
		ServerHall<T> hall2 = serverHalls.get(infoB);

		if (hall == null || hall2 == null) {
			throw new IllegalArgumentException("Någon av hallarna finns inte");
		}

		Edge<T> edgeA = new Edge<>(hall, hall2);
		Edge<T> edgeB = new Edge<>(hall2, hall);

		edges.computeIfAbsent((T) infoA, k -> new ArrayList<>()).add(edgeA);
		edges.computeIfAbsent((T) infoB, k -> new ArrayList<>()).add(edgeB);

		nEdges++;
	}

	@Override
	public void remove(T info) {

		if (!serverHalls.containsKey(info)) {
			throw new IllegalArgumentException("Serverhallen finns inte");
		}

		for (ArrayList<Edge<T>> edgeList : edges.values()) {
			edgeList.removeIf(edge -> edge.getTo().getInfo().equals(info));
		}

		List<Edge<T>> outgoingEdges = edges.get(info);

		if (outgoingEdges != null) {
			nEdges -= outgoingEdges.size();
		}

		serverHalls.remove(info);
		edges.remove(info);
		nServerHalls--;

	}
	// Calculates the Hausdorff distance between two sets of points
	@Override
	public double calculateHausdorffDistance(List<ServerHall<T>> setOne,
	                                         List<UpdatedPosition<T>> setTwo) {
	    double forwardCheck = computeOneWay(setOne, setTwo);
	    double reverseCheck = computeOtherWay(setTwo, setOne);
	    return Math.max(forwardCheck, reverseCheck);
	}

	// Goes from setOne to setTwo: finds the furthest closest match
	private double computeOneWay(List<ServerHall<T>> source, List<UpdatedPosition<T>> target) {
	    double maxGap = 0;

	    for (ServerHall<T> pointA : source) {
	        double smallestGap = Double.POSITIVE_INFINITY;

	        for (UpdatedPosition<T> pointB : target) {
	            double gap = calculateDistance(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());

	            if (gap < smallestGap) {
	                smallestGap = gap;
	            }
	        }

	        if (smallestGap > maxGap) {
	            maxGap = smallestGap;
	        }
	    }

	    return maxGap;
	}

	// Goes from setTwo to setOne: same logic, flipped
	private double computeOtherWay(List<UpdatedPosition<T>> source, List<ServerHall<T>> target) {
	    double maxGap = 0;

	    for (UpdatedPosition<T> pointA : source) {
	        double smallestGap = Double.POSITIVE_INFINITY;

	        for (ServerHall<T> pointB : target) {
	            double gap = calculateDistance(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());

	            if (gap < smallestGap) {
	                smallestGap = gap;
	            }
	        }

	        if (smallestGap > maxGap) {
	            maxGap = smallestGap;
	        }
	    }

	    return maxGap;
	}

	private double calculateDistance(double x1, double y1, double x2, double y2) {
	    double dx = x1 - x2;
	    double dy = y1 - y2;
	    return Math.sqrt(dx * dx + dy * dy);
	}
	@Override
	public int numberOfEdges() {

		return nEdges;
	}

	@Override
	public int numberOfServerHalls() {

		return nServerHalls;
	}

}
