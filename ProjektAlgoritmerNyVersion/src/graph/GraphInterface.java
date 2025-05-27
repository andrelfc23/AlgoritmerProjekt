package graph;

import java.util.List;
import java.util.Map;

public interface GraphInterface<T> {

	public List<ServerHall<T>> getAllServerHalls();

	public List<Edge<T>> getEdges(T info);

	public ServerHall<T> getServerHall(T info);

	public List<Edge<T>> getAllEdges();

	public Map<ServerHall<T>, Double> shortestPaths(T startInfo);

	public double getDistance(T from, T to);

	public void addServerHall(double x, double y, T info);

	public void addEdge(T infoA, T infoB);

	public void remove(T info);

	public int numberOfEdges();

	public int numberOfServerHalls();

	public double calculateHausdorffDistance(List<ServerHall<T>> setOne, List<UpdatedPosition<T>> setTwo);

}
