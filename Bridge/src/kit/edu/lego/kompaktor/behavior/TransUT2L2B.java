package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


/**
 * Orientiert sich immer rechts
 * 
 * 
 * @author Christian
 *
 */

public class TransUT2L2B {

//	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 50;
//	final static int travelLengthLine = 3;
//	final static int ThresholdAngleForward = 5;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
		LightSensor ligthSensor = ParcoursRunner.LIGHT_SENSOR;
		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT_REVERSE;
			
		while(!touchright.isPressed() && !touchleft.isPressed());
		// Stelle sicher dass der Lichtsensor nicht im Weg ist.
		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);
		
		BarcodeDetector bar = new BarcodeDetector();
		bar.init();
		bar.start();
		
		pilot.setTravelSpeed(travelSpeedLine);
		
		UTurnRunner uturn = new UTurnRunner();
		uturn.init();
		uturn.start();
		
		
		while (!bar.isDone()) {
			Thread.yield();
		}
		
		try {
			uturn.stop();
			bar.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//drehen
		pilot.rotate(180);
		//Sensor ausrichten
		LightSwitcher.setAngle(0);
		//vorwärts fahren bis Linie erkannt
		pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		pilot.forward();
		while(ligthSensor.readValue() < LineRunner.ThresholdLine);
		pilot.stop();
		
		//LineRunner starten
		LineRunner line = new LineRunner();
		line.init();
		line.start();
		
		while(!line.isDone());
		
		//LineRunner stoppen
		try {
			line.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//detector ausrichten
		LightSwitcher.setAngle(0);
		//neuen Barcode scannen
		bar = new BarcodeDetector();
		bar.init();
		bar.start();
		//vorwärts fahren
		pilot.forward();
		//sobald barcode gefunden wird gestoppt
		while(!bar.isDone());
		pilot.stop();
		try {
			bar.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//etwas vorfahren (auf die Brücke
		LightSwitcher.setAngle(-90);
		pilot.travel(20);
		BridgeRun bridge = new BridgeRun();
		bridge.init();
		bridge.start();
		
		while(!touchright.isPressed() && !touchleft.isPressed()){
			Thread.yield();
		}

		try {
			bridge.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}

}
