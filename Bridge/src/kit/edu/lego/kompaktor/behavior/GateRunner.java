package kit.edu.lego.kompaktor.behavior;


import kit.edu.lego.kompaktor.model.Gate;
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

			Gate gate = new Gate();

			System.out.println("Sending passed");
			
			driver.drive(14, 30);
			
			Sound.beepSequenceUp();
			System.out.println("Calling gate");
			// Wait for connection
			while (!gate.connect()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Connected to the gate.");

			// Now the gate opens & a timer of 20 seconds starts
			// in this time the robot has to drive through & send a "I passed"
			// signal
			Thread.sleep(1000);
			driver.straight(80);
			driver.rotate(-160);
			driver.straight(-20);

			// Robot drives through the gate
			System.out.println("Driving through.");

			// Tell the gate that robot passed, send a "I passed" signal
//			while (!gate.sendPassed()) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		
			System.out.println("success="+gate.sendPassed());
			

			
			driver.stop();
			ropeBridgeRunner = new RopeBridgeRun();
			ropeBridgeRunner.init();
			ropeBridgeRunner.start();
			
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
