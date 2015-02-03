package kit.edu.lego.kompaktor.behavior;


import kit.edu.lego.kompaktor.model.Gate;
import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.Sound;

public class GateRunner extends ParcoursRunner {

	private LabyrinthRunner driver;
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
		driver = new LabyrinthRunner();
	}

	@Override
	public void run() {

		try {
			System.out.println("Calling gate");
			Gate.connect();
			Kompaktor.DIFF_PILOT_REVERSE.travel(30);
//			driver.drive(14, 20);
			
			//Sound.beepSequenceUp();
			
			// Wait for connection
			Gate.waitForConnection();
			System.out.println("Connected to the gate.");

			// Now the gate opens & a timer of 20 seconds starts
			// in this time the robot has to drive through & send a "I passed"
			// signal
			//Thread.sleep(1000);
			
//			double left, right;
//			left = Kompaktor.SONIC_SENSOR.getDistance();
//			Kompaktor.DIFF_PILOT_REVERSE.rotate(-180);
//			right = Kompaktor.SONIC_SENSOR.getDistance();
//			Kompaktor.DIFF_PILOT_REVERSE.rotate(90);
//			double middle = (left + right) / 2.0;
//			Kompaktor.DIFF_PILOT_REVERSE.travel(middle - left);
//			Kompaktor.DIFF_PILOT_REVERSE.rotate(90);
			
			
			//driver.straight(80);
			Kompaktor.DIFF_PILOT_REVERSE.travel(50);
			Gate.sendPassed();
			//driver.drive(20, 70);
			driver.init();
			//driver.start();
			//Thread.sleep(5000);
			//driver.stop();
			driver.drive(20, 80);
			driver.rotate(-160);
			driver.straight(-20);
			driver.stopHelpThreads();

			// Robot drives through the gate
			System.out.println("Driving through.");

			// Tell the gate that robot passed, send a "I passed" signal
			
//			long time = System.currentTimeMillis();
//			boolean through = false;
//			while (!through) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				through = Gate.sendPassed();
//				
//				if (System.currentTimeMillis() - time > 3000) {
//					break;
//				}
//			}
		
//			System.out.println("success="+through);
			
			//driver.stop();
			//driver.join();
			//driver.stopHelpThreads();
			
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
