package utils;

public class Clock {

	private static long t0;
	
	public static void initClock() {
		t0 = System.currentTimeMillis();
	}
	
	public static void stopClock() {
		t0 = System.currentTimeMillis() - t0;
	}
	
	public static String dumpClock() {
		return String.format("Completed in %s\nmillis: %x", 
				mlsToHms(System.currentTimeMillis() - t0),
				System.currentTimeMillis() - t0
			);
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
