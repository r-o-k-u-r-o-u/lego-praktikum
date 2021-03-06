package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.Sound;

public class SeesawRunner extends ParcoursRunner {

	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
	final static int travelLengthLine = 3;
	final static int ThresholdAngleForward = 10;
	final static int ThresholdLine = LineRunner.ThresholdLine;
	private boolean isDone = false;
	
	public static void main(String[] args) {
		//wait until it is pressed
		while(!Kompaktor.isTouched());
		
		Kompaktor.startLevel(LEVEL_NAMES.SEESAW);
		
		//end
		while(!Kompaktor.isTouched());
	}
	
	private LineRunner line = null;
	
	public void run() {

		try {

			line = new LineRunner();
			line.start();

			// linie endet
			while (!line.isDone()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				
				Thread.sleep(10);
			}

			line.stop();

			// falls endet checke mit dem Ultraschallsensor ob
			// es sich um die Wippe oder den Drehteller handelt.
			int val = Kompaktor.SONIC_SENSOR.getDistance();
			boolean isSeesaw = false;
			
			while (isSeesaw) {
				val = Kompaktor.SONIC_SENSOR.getDistance();
				
				if (val > 70) {
					// wippe
					
					Sound.beep();
					isSeesaw = true;
				}
			}
			
			Sound.beepSequenceUp();
			
			line = new LineRunner();
			line.init();
			line.start();

			line.join();
			
			isDone = true;

		} catch (InterruptedException ie) {
			//System.out.println("TurnTable runner exception.");
			if(line != null && line.isAlive())
				try {
					line.stop();
				} catch (InterruptedException e) {
					System.out.println("TurnTable runner exception.");
				}
			Kompaktor.DIFF_PILOT.stop();
		}
	}


	@Override
	public void init() {
	}

	@Override
	public boolean isDone() {
		return isDone && line.isDone();
	}
	
}
