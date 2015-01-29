package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.LightSensor;
//import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
//import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;


/**
 * Orientiert sich immer rechts
 * 
 * 
 * @author Christian
 *
 */

public class TransitionUTurnToLineFollow {

//	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 50;
//	final static int travelLengthLine = 3;
//	final static int ThresholdAngleForward = 5;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, false);
				
		// Stelle sicher dass der Lichtsensor nicht im Weg ist.
		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);
		
		BarcodeDedector bar = new BarcodeDedector(ligthSensor);
		bar.start();
		
//		double value = 0;
		
		pilot.setTravelSpeed(travelSpeedLine);
		pilot.forward();
		
		while (!bar.barcodeFound()) {
			Thread.yield();
		}
//		Sound.beep();
		
//		while (!bar.barcodeFound()) {
//
//			pilot.forward();
//			Sound.beep();
//			
//			while (!touchright.isPressed() && !touchleft.isPressed()) {
//				Thread.yield();
//			}
//
//			pilot.stop();
//			Sound.beep();
//			
//			pilot.travel(-10);
//			pilot.rotate(-90);
//			Thread.yield();
//		}
		
		pilot.stop();
		pilot.rotate(180);
		pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		bar.interrupt();
		try {
			bar.join();
		} catch (InterruptedException e) {
			Sound.beep();
//			e.printStackTrace();
		}
		
		LineRunner run = new LineRunner(ligthSensor, pilot);
		LightSwitcher.setAngle(0);
		pilot.forward();
		
		while (ligthSensor.readValue() < 40) {
		}
		pilot.stop();
		
		run.start();
		
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}

}
