package graph;

import java.awt.*;

public class Edge<T> {

	private ServerHall<T> from;
	private ServerHall<T> to;
	private double distance;
	private Color color;

	public Edge(ServerHall<T> from, ServerHall<T> to) {
		this.from = from;
		this.to = to;
		this.color = Color.gray;
		calculateDistance(from, to);
	}

	private double calculateDistance(ServerHall<T> from, ServerHall<T> to) {
		double dx = from.getX() - to.getX();
		double dy = from.getY() - to.getY();
		this.distance = Math.sqrt(dx * dx + dy * dy);
		return this.distance;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public ServerHall<T> getFrom() {
		return from;
	}

	public ServerHall<T> getTo() {
		return to;
	}

	public void setTo(ServerHall<T> to) {
		this.to = to;
	}

	public void setFrom(ServerHall<T> from) {
		this.from = from;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
