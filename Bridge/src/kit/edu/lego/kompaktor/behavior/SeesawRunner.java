package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;


public class SeesawRunner extends ParcoursRunner {

	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
	final static int travelLengthLine = 3;
	final static int ThresholdAngleForward = 10;
	final static int ThresholdLine = LineRunner.ThresholdLine;
	private boolean isDone = false;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchRight = ParcoursRunner.TOUCH_RIGHT;
		TouchSensor touchLeft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		
		
		while(!touchRight.isPressed() && !touchLeft.isPressed());
		LightSwitcher.initAngles();
		
		SeesawRunner turn = new SeesawRunner();
		turn.init();
		turn.start();
		while(!turn.isDone());
		try {
			turn.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!touchRight.isPressed() && !touchLeft.isPressed());

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
			int val = sonicSensor.getDistance();
			boolean isSeesaw = false;
			
			while (isSeesaw) {
				val = sonicSensor.getDistance();
				
				if (val > 70) {
					// wippe
					
					Sound.beep();
					isSeesaw = true;
				}
			}
			
			Sound.beep();
			
			
			
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
			pilot.stop();
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
