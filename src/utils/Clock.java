package utils;

public class Clock implements Runnable{

	private long time;
	private boolean runing;
	private long delay = 100;
	
	public Clock() {
		time = 0;
		runing = false;	
	}
	
	public String getWaitInfo() {
		int seconds = (int)( time / 1000 );
		time /= 1000;
		int minutes = (int) ( time / 60 );
		time /= 60;
		int hours = (int) ( time / 60 );
		return String.format("Time Passed[h:m:s]: %d:%d:%d", hours, minutes, seconds);
	}
	
	public Thread start() {
		runing = true;
		time = 0;
		Thread t = new Thread(this);
		t.start();
		return t;
	}
	
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
