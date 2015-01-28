package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class BarcodeDedector extends Thread{

	final static int travelSpeedBarcode = 20;
	final static int numberBarcodeLines = 3;
	
	public static void main(String[] args) {
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		//Sensor an die Seite
		LightSwitcher.setAngle(-90);
		//vorwärtsfahren
		pilot.setTravelSpeed(travelSpeedBarcode);
		pilot.forward();
		
		BarcodeDedector barcode = new BarcodeDedector(ligthSensor);
		barcode.start();
		
		while(!barcode.barcodeFound());
		
		pilot.stop();
		barcode.interrupt();
		try {
			barcode.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(!touchright.isPressed() && !touchleft.isPressed()){
			Sound.beep();
		}

	}
	
	
	
	private LightSensor ligthSensor;
	private int lines;
	private boolean testLine;
	
	public BarcodeDedector(LightSensor ligthSensor){
		this.ligthSensor = ligthSensor;
		lines = 0;
		testLine = true;
	}
	
	public boolean barcodeFound(){
		return lines >= numberBarcodeLines && testLine;
	}
	
	public void run(){
		try{
			while(true){
				if(Thread.interrupted())
					throw new InterruptedException();
				if(testLine){
					if(ligthSensor.readValue() >= 40){
						lines++;
						testLine = false;
					}
				} else {
					if(ligthSensor.readValue() < 40){
						testLine = true;
					}
				}
				Thread.yield();
			}
		} catch (InterruptedException e){ }
	}

}
