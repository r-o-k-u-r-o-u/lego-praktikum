package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.TurnTable;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


public class TurnTableRunner extends ParcoursRunner {

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
		
		TurnTableRunner turn = new TurnTableRunner();
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

			TurnTable turnTable = new TurnTable();

			// connect to turntable
			while (!turnTable.connect()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				
				Thread.sleep(500);
			}
			
			// wait until its my turn
			while (!turnTable.waitHello()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				
				Thread.sleep(200);
			}
			

			line = new LineRunner();
			line.start();

			// while rotation not finished
			while (!line.isDone()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				
				Thread.sleep(10);
			}

			line.stop();

			pilot.travel(10);
			pilot.rotate(-180);
			pilot.backward();
			LightSwitcher.setAngle(-90);

			while (!touchRight.isPressed() && !touchLeft.isPressed()) {
				if (Thread.interrupted())
					throw new InterruptedException();
			}

			pilot.stop();

			while (!turnTable.turn()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				
				Thread.sleep(200);
			}
			
			
			// when on turntable wait until done
			while (!turnTable.waitDone()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				
				Thread.sleep(200);
			}
			

			pilot.travel(15);
			line = new LineRunner();
			line.start();

			pilot.travel(20);

			long time1 = System.currentTimeMillis();

			while (System.currentTimeMillis() - time1 < 4000) {
				if (line.isDone()) {
					pilot.travel(5);
				}

				if (Thread.interrupted())
					throw new InterruptedException();
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
