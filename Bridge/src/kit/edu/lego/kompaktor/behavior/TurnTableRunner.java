package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.TurnTable;

public class TurnTableRunner extends ParcoursRunner {

	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
	final static int travelLengthLine = 3;
	final static int ThresholdAngleForward = 10;
	final static int ThresholdLine = LineRunner.ThresholdLine;
	private boolean isDone = false;
	
	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchRight = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchLeft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		
		while(!Kompaktor.isTouched());
//		LightSwitcher.initAngles();
		
		Kompaktor.startLevel(LEVEL_NAMES.TURN_TABLE);
		
//		TurnTableRunner turn = new TurnTableRunner();
//		turn.init();
//		turn.start();
//		while(!turn.isDone());
//		try {
//			turn.stop();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		//end
		while(!Kompaktor.isTouched());

	}
	
	private LineRunner line = null;
	
	public void run() {

		try {

			TurnTable turnTable = new TurnTable();

			System.out.println("Connecting");
			
			// connect to turntable
			turnTable.connect();
			
			turnTable.waitForConnection();
			
			System.out.println("Connected.");
			
			// wait until its my turn
			while (!turnTable.waitHello()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				Thread.sleep(Kompaktor.SLEEP_INTERVAL_LONG);
			}
			
			System.out.println("My turn.");

			line = new LineRunner();
			line.start();

			// while rotation not finished
			while (!line.isDone()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			}

			line.stop();

			Kompaktor.DIFF_PILOT.travel(10);
			Kompaktor.DIFF_PILOT.rotate(180);
			Kompaktor.DIFF_PILOT.backward();
//			LightSwitcher.setAngle(-90);
			Kompaktor.parkArm();
			
			while (!Kompaktor.isTouched()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			}

			Kompaktor.DIFF_PILOT.stop();

			while (!turnTable.turn()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				Thread.sleep(Kompaktor.SLEEP_INTERVAL_LONG);
			}
			
			
			// when on turntable wait until done
			while (!turnTable.waitDone()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				Thread.sleep(Kompaktor.SLEEP_INTERVAL_LONG);
			}
			

			Kompaktor.DIFF_PILOT.travel(15);
			line = new LineRunner();
			line.init();
			line.start();

			Kompaktor.DIFF_PILOT.travel(20);

			long time1 = System.currentTimeMillis();

			while (System.currentTimeMillis() - time1 < 4000) {
				if (line.isDone()) {
					Kompaktor.DIFF_PILOT.travel(5);
				}

				if (Thread.interrupted())
					throw new InterruptedException();
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			}

			turnTable.sendCYA();

			isDone = true;
			
			line.join();

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
