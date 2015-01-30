package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class BarcodeDedector extends ParcoursRunner{

	final static int travelSpeedBarcode = 20;
	final static int numberBarcodeLines = 3;
	final static int maxTimeElipsed = 2000;
	final static int ThresholdLine = LineRunner.ThresholdLine;
	
	public static void main(String[] args) {
		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT;
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		//Sensor an die Seite
		LightSwitcher.setAngle(-90);
		//vorwärtsfahren
		pilot.setTravelSpeed(travelSpeedBarcode);
		pilot.forward();
		
		BarcodeDedector barcode = new BarcodeDedector();
		barcode.start();
		
		while(!barcode.isDone());
		
		pilot.stop();
		try {
			barcode.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(!touchright.isPressed() && !touchleft.isPressed()){
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
					if(lightSensor.readValue() >= ThresholdLine){
						lines++;
						testLine = false;
						lastTime = System.currentTimeMillis();
					}
				} else {
					if(lightSensor.readValue() < ThresholdLine){
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
