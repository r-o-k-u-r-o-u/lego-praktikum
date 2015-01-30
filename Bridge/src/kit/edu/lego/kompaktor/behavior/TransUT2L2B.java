package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.LightSensor;
//import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
//import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
//import lejos.nxt.UltrasonicSensor;
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
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, false);
			
		while(!touchright.isPressed() && !touchleft.isPressed());
		// Stelle sicher dass der Lichtsensor nicht im Weg ist.
		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);
		
		BarcodeDedector bar = new BarcodeDedector(ligthSensor);
		bar.start();
		
		pilot.setTravelSpeed(travelSpeedLine);
		
		UTurnRunner uturn = new UTurnRunner(touchright, touchleft, sonic, pilot);
		uturn.start();
		
		
		while (!bar.barcodeFound()) {
			Thread.yield();
		}
		
		uturn.interrupt();
		bar.interrupt();
		try {
			uturn.join();
			bar.join();
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
		LineRunner line = new LineRunner(ligthSensor, pilot);
		line.start();
		
		while(line.getSwitchCounter() < 3);
		
		//LineRunner stoppen
		line.interrupt();
		try {
			line.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//detector ausrichten
		LightSwitcher.setAngle(0);
		//neuen Barcode scannen
		bar = new BarcodeDedector(ligthSensor);
		bar.start();
		//vorwärts fahren
		pilot.forward();
		//sobald barcode gefunden wird gestoppt
		while(!bar.barcodeFound());
		pilot.stop();
		bar.interrupt();
		try {
			bar.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//etwas vorfahren (auf die Brücke
		LightSwitcher.setAngle(-90);
		pilot.travel(20);
		BridgeRun bridge = new BridgeRun(ligthSensor, pilot);
		bridge.run();
		
		while(!touchright.isPressed() && !touchleft.isPressed()){
			Thread.yield();
		};
		bridge.interrupt();
		try {
			bridge.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}

}
