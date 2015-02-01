package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.LightSwitcher;

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
//		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor ligthSensor = ParcoursRunner.LIGHT_SENSOR;
//		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT_REVERSE;
			
		while(!Kompaktor.isTouched());
		// Stelle sicher dass der Lichtsensor nicht im Weg ist.
		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);
		
		BarcodeDetector bar = new BarcodeDetector();
		bar.init();
		bar.start();
		
		Kompaktor.DIFF_PILOT.setTravelSpeed(travelSpeedLine);
		
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
		Kompaktor.DIFF_PILOT.rotate(180);
		//Sensor ausrichten
		LightSwitcher.setAngle(0);
		//vorwärts fahren bis Linie erkannt
		//Kompaktor.DIFF_PILOT = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		Kompaktor.DIFF_PILOT_REVERSE.forward();
		while(Kompaktor.LIGHT_SENSOR.readValue() < LineRunner.ThresholdLine);
		Kompaktor.DIFF_PILOT_REVERSE.stop();
		
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
		Kompaktor.DIFF_PILOT_REVERSE.forward();
		//sobald barcode gefunden wird gestoppt
		while(!bar.isDone());
		Kompaktor.DIFF_PILOT_REVERSE.stop();
		try {
			bar.stop();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//etwas vorfahren (auf die Brücke
		LightSwitcher.setAngle(-90);
		Kompaktor.DIFF_PILOT_REVERSE.travel(20);
		BridgeRun bridge = new BridgeRun();
		bridge.init();
		bridge.start();
		
		while(!Kompaktor.isTouched()){
			Thread.yield();
		}

		try {
			bridge.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!Kompaktor.isTouched());
	}

}
