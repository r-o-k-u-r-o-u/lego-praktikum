package kit.edu.lego.kompaktor.test;

//import kit.edu.lego.kompaktor.model.LightSwitcher;
//import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
//import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


//TODO Rückwärtsfahren wenn Linie nicht erkannt? --> nur wenn Linie nicht einfach endet

public class RotationTest {

	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
	final static int travelLengthLine = 3;
	final static int ThresholdAngleForward = 5;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
//		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
				
		//Thread zum switchen
//		LightSwitcher switchThread = new LightSwitcher();
		
		boolean left  = false;
		boolean right  = false;
		
		while (true) {
			
			while(!left && !right) {
				left  = touchleft.isPressed();
				right = touchright.isPressed();
			}
			
			System.out.println("pressed");
			
			if (left) {
				pilot.rotate(-90);
			} else if (right) {
				pilot.rotate(90);
			}
			
			left = false;
			right = false;
		}
		
	}

}
