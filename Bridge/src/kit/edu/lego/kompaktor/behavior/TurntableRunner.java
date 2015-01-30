package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.TurnTable;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


public class TurntableRunner extends ParcoursRunner {

	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
	final static int travelLengthLine = 3;
	final static int ThresholdAngleForward = 10;
	final static int ThresholdLine = 42;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		TurnTable turnTable = new TurnTable();
		
		turnTable.connect();
		
		turnTable.waitHello();
		
		LineRunner line = new LineRunner(ligthSensor, pilot);
		line.start();
		
		while (!line.isDone());
		
		line.interrupt();
		
		try {
			line.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		pilot.travel(10);
		pilot.rotate(-180);
		pilot.backward();
		LightSwitcher.setAngle(-90);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		
		pilot.stop();
		
		// when on turntable
		turnTable.turn();
		turnTable.waitDone();
		
		pilot.travel(15);
		LineRunner line2 = new LineRunner(ligthSensor, pilot);
		line2.start();
		
		pilot.travel(20);
		
		long time1 = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - time1 < 4000) {
			if (line2.isDone()) {
				pilot.travel(5);
			}
		}
		
		turnTable.sendCYA();
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

	private LightSensor ligthSensor;
	private DifferentialPilot pilot;
	private TouchSensor touchright = new TouchSensor(SensorPort.S3);
	private TouchSensor touchleft = new TouchSensor(SensorPort.S2);
	
	public TurntableRunner(LightSensor ligthSensor, DifferentialPilot pilot) {
		this.ligthSensor = ligthSensor;
		this.pilot = pilot;
	}
	
	public void run(){
	
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		TurnTable turnTable = new TurnTable();
		
		turnTable.connect();
		
		turnTable.waitHello();
		
		LineRunner line = new LineRunner(ligthSensor, pilot);
		line.start();
		
		while (!line.isDone());
		
		line.interrupt();
		
		try {
			line.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		pilot.travel(10);
		pilot.rotate(-180);
		pilot.backward();
		LightSwitcher.setAngle(-90);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		
		pilot.stop();
		
		// when on turntable
		turnTable.turn();
		turnTable.waitDone();
		
		pilot.travel(15);
		LineRunner line2 = new LineRunner(ligthSensor, pilot);
		line2.start();
		
		pilot.travel(20);
		
		long time1 = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - time1 < 4000) {
			if (line2.isDone()) {
				pilot.travel(5);
			}
		}
		
		turnTable.sendCYA();
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}



	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
