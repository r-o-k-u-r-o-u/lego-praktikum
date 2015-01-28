import java.io.IOException;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.Touch;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class LineRunner2 {

	final static int rotationAngle = 200;
	final static int rotationSpeed = 500;
	
	Touch sensorLeft = new TouchSensor(SensorPort.S2);
	Touch sensorRight = new TouchSensor(SensorPort.S3);
	
	boolean running = true;
	boolean lineVisible;
	boolean angleRegistered = false;
	boolean angleStraight = false;
	int rotationA;
	
	// Change last parameter of Pilot to specify on which 
	// direction you want to be "forward" for your vehicle.
	// The wheel and axle dimension parameters should be
	// set for your robot, but are not critical.
	
	public static void main(String[] args) {
		
		new LineRunner2();
	}
	
	public LineRunner2() {
		
		PilotProps pp = new PilotProps();
    	try {
			pp.loadPersistentValues();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		
		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "C"));
		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "B"));
		boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"true"));
		
		float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "3.0"));
		float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "13.0"));
		
		DifferentialPilot pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		
		//init Light-Sensor rotation
		System.out.println(Motor.A.getPosition());
		Motor.A.setSpeed(100);
		//Motor.A.setStallThreshold(5, 200);
		Motor.A.rotate(-rotationAngle);
		Motor.A.stop();
		//Motor.A.resetTachoCount();
		System.out.println(Motor.A.getPosition());
		
		LightSensor lightSensor = new LightSensor(SensorPort.S1, true);
		

		
		
		
		//Thread zum switchen
		Thread switchThread = new Thread(new Runnable() {		
			@Override
			public void run() {
				try {
					while (running) {
						if (!lineVisible) {				
							angleStraight = false;
							Motor.A.setSpeed(rotationSpeed);
							while(true) {
								Motor.A.rotate(rotationAngle, true);
								Thread.sleep(rotationAngle * 1000 / rotationSpeed);
								Motor.A.rotate(-rotationAngle, true);
								Thread.sleep(rotationAngle * 1000 / rotationSpeed);
							}
						} else {
							rotationA = Motor.A.getPosition();
							angleRegistered = true;
							Motor.A.rotateTo(90);
							angleStraight = true;	
						}
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					Motor.A.stop();
				}
			}
		});
		
		Thread findLight = new Thread(new Runnable() {		
			public void run() {
				while (running) {
					if (!angleRegistered)
						if (lightSensor.readValue() >= 40)
							lineVisible = true;
						else
							lineVisible = false;
					
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {}
				}
			}
		});
		
		
		// Thread zum Piepen
		Thread lightRecognition = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running)
				try {
					if (lineVisible && angleStraight) {
						Sound.beep();
						pilot.forward();
					} else if (lineVisible) {
						while (!angleRegistered)
							Thread.yield();
						
						pilot.arcForward(90 - rotationA);
						
						angleRegistered = false;
					}
					Thread.sleep(10);
				} catch (InterruptedException e) {}			
			}
		});
		
		
		
		
		switchThread.start();
		findLight.start();
		lightRecognition.start();
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		switchThread.interrupt();
		lightRecognition.interrupt();
		System.out.println("interrupted");
		try {
			switchThread.join();
			lightRecognition.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("join is finish");

		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}

}
