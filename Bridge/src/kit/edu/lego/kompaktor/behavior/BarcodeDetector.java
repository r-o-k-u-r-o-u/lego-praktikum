package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.Sound;

public class BarcodeDetector extends ParcoursRunner{

	final static int travelSpeedBarcode = 20;
	final static int numberBarcodeLines = 3;
	final static int maxTimeElipsed = 2000;
	final static int ThresholdLine = LineRunner.ThresholdLine;
	
	public static void main(String[] args) {
		
//		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT;
		
//		while(!touchright.isPressed() && !touchleft.isPressed());
		while (!Kompaktor.isTouched());
//		LightSwitcher.initAngles();
		//Sensor an die Seite
//		LightSwitcher.setAngle(-90);
		Kompaktor.parkArm();
		
		//vorwärtsfahren
		Kompaktor.DIFF_PILOT.setTravelSpeed(travelSpeedBarcode);
		Kompaktor.DIFF_PILOT.forward();
		
		BarcodeDetector barcode = new BarcodeDetector();
		barcode.start();
		
		while(!barcode.isDone());
		
		Kompaktor.DIFF_PILOT.stop();
		try {
			barcode.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(!Kompaktor.isTouched()){
			Sound.beep();
		}

	}
	
	
	private int lines;
	private boolean testLine;
	private long lastTime;
	
	@Deprecated
	public boolean barcodeFound(){
		return lines >= numberBarcodeLines && testLine;
	}
	
	public void run(){
		try{
			while(true){
				if(Thread.interrupted())
					throw new InterruptedException();
				if((System.currentTimeMillis() - lastTime) > maxTimeElipsed){
					testLine = true;
					lines = 0;
					lastTime = System.currentTimeMillis();
				}
				if(testLine){
					if(Kompaktor.LIGHT_SENSOR.readValue() >= ThresholdLine){
						lines++;
						testLine = false;
						lastTime = System.currentTimeMillis();
					}
				} else {
					if(Kompaktor.LIGHT_SENSOR.readValue() < ThresholdLine){
						testLine = true;
						lastTime = System.currentTimeMillis();
					}
				}
				Thread.yield();
			}
		} catch (InterruptedException e){ }
	}

	@Override
	public void init() {
		//nothing to do
	}

	@Override
	public boolean isDone() {
		return barcodeFound();
	}

}
