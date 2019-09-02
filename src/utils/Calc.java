package utils;

public class Calc {

	public static double dist(Point p1, Point p2) {
		double t1 = Math.pow(p1.x - p2.x, 2);
		double t2 = Math.pow(p1.y - p2.y, 2);
		
		return Math.sqrt(t1 + t2);
	}
	
	public static String mlsToHms(long mls) {
		double seconds = mls / 1000;		
		double minutes = 0;
		double hours = 0.0;
		
		if(seconds > 60) {
			minutes = seconds / 60.0;
			seconds = (minutes - Math.floor(minutes)) * 60;
		}
		
		if(minutes > 60) {
			hours = minutes / 60.0;
			minutes = (hours - Math.floor(hours)) * 60.0;
		}
		
		return String.format("%d:%d:%d", (int)hours, (int)minutes, (int)seconds);
	}
}
