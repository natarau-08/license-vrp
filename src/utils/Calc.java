package utils;

public class Calc {

	public static double dist(Point p1, Point p2) {
		double t1 = Math.pow(p1.x - p2.x, 2);
		double t2 = Math.pow(p1.y - p2.y, 2);
		
		return Math.sqrt(t1 + t2);
	}
}
