package graph;

import java.awt.Color;

public class ServerHall<T> {
	
	private T info;

	private double x;
	private double y;

	private Color color;
	
	
	public ServerHall(double x, double y, T info) {
		this.y = y;
		this.x = x;
		this.color = Color.BLACK;
		this.info = info;
	}
	

	public T getInfo() {
		return info;
	}
	
	public void setInfo(T info) {
		this.info = info;
	}
	
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	
	

}
