package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Gate;
import kit.edu.lego.kompaktor.model.Kompaktor;

public class GateRunner extends ParcoursRunner {

//	private LabyrinthRunner driver;
	ParcoursRunner ropeBridgeRunner;

	public static void main(String[] args) {

		GateRunner gr = new GateRunner();
		
		gr.init();
		gr.start();
		
		while (!gr.isDone());

		try {
			gr.stop();
		} catch (InterruptedException e) {
			System.out.println("Exception occured in GateRunner.");
		}
	}

	public GateRunner() {
//		driver = new LabyrinthRunner();
	}

	@Override
	public void run() {

		int threshWood = 30;
		
		
		
		try {
			System.out.println("Calling gate");
			Gate.connect();

			
			//Sound.beepSequenceUp();
			
			// Wait for connection
			Gate.waitForConnection();
			System.out.println("Connected to the gate.");

//			Kompaktor.SONIC_SENSOR.setMode(UltrasonicSensor.MODE_CONTINUOUS);
			Kompaktor.SONIC_SENSOR.continuous();
			
			while (Kompaktor.readLightValue() >= threshWood) {
				
				if (Kompaktor.isTouched()) {
					Kompaktor.DIFF_PILOT_REVERSE.travel(-5);
				}
				
				
				if (Kompaktor.readDistanceValue() <= 10)
				{
					Kompaktor.DIFF_PILOT_REVERSE.rotate(5);
				}
				else {
					Kompaktor.DIFF_PILOT_REVERSE.rotate(-5);
				}
				
				Kompaktor.DIFF_PILOT_REVERSE.travel(10);
				
			}
			
			for (int i = 0; i < 5; i++) {
				Kompaktor.DIFF_PILOT_REVERSE.travel(10);
				if (Kompaktor.readDistanceValue() <= 15)
				{
					Kompaktor.DIFF_PILOT_REVERSE.rotate(5);
				}
				else {
					Kompaktor.DIFF_PILOT_REVERSE.rotate(-5);
				}
			}
			
			Kompaktor.DIFF_PILOT_REVERSE.travel(10);
			Kompaktor.DIFF_PILOT_REVERSE.rotate(180);
			
			// Now the gate opens & a timer of 20 seconds starts
			// in this time the robot has to drive through & send a "I passed"
			// signal



			// Robot drives through the gate
			System.out.println("Driving through.");

			// Tell the gate that robot passed, send a "I passed" signal
			
			long time = System.currentTimeMillis();
			boolean through = false;
			while (!through) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				through = Gate.sendPassed();
				
				if (System.currentTimeMillis() - time > 3000) {
					break;
				}
			}
		
//			System.out.println("success="+through);
			
			Kompaktor.DIFF_PILOT_REVERSE.stop();
//			driver.stop();
			
			Kompaktor.DIFF_PILOT.travel(10);
			
			
			Kompaktor.showText("Starting RopeBridgeRunner");
			
			ropeBridgeRunner = new RopeBridgeRun();
			ropeBridgeRunner.init();
			ropeBridgeRunner.start();
			
			while (ropeBridgeRunner.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			ropeBridgeRunner.join();
			
		} catch (InterruptedException ie) {

			if (ropeBridgeRunner != null) {
				ropeBridgeRunner.interrupt();
			}

		}
	}

	@Override
	public void init() {
	}

	@Override
	public boolean isDone() {

		return ropeBridgeRunner != null && ropeBridgeRunner.isDone();
	}

}
