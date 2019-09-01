package utils;

/**Simple Clock that can measure time passed throughout the execution of various processes.
 * @author Alexandru
 *
 */
public class Clock implements Runnable{

	private long time;
	private boolean runing;
	private long delay = 100;
	
	/**
	 * Simple clock that can measure time.
	 */
	public Clock() {
		time = 0;
		runing = false;	
	}
	
	/**
	 * @return - time passed in hours:minutes:seconds format
	 */
	public String getWaitInfo() {
		long t = time;
		int seconds = (int)( t / 1000 );
		t /= 1000;
		int minutes = (int) ( t / 60 );
		t /= 60;
		int hours = (int) ( t / 60 );
		return String.format("Time Passed[h:m:s]: %d:%d:%d", hours, minutes, seconds);
	}
	
	/**
	 * Get the number of milliseconds passed since the clock started
	 * @return - milliseconds passed since clock started
	 */
	public long getMills() {
		return time;
	}
	
	/**
	 * Starts a new thread in which the time is measured
	 * @return - the started thread
	 */
	public Thread start() {
		runing = true;
		time = 0;
		Thread t = new Thread(this);
		t.start();
		return t;
	}
	
	/**
	 * Flags the clock to stop at the next interval
	 */
	public void stop() {
		runing = false;
	}
	
	@Override
	public void run() {
		long t0 = System.currentTimeMillis();
		while(runing) {
			try {
				Thread.sleep(delay);
				time = System.currentTimeMillis() - t0;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
