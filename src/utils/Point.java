package utils;

public class Point {
	public double x, y, z;
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return String.format("%fx%fx%f", x, y, z);
	}
}
