package kit.edu.lego.kompaktor.test;

//import kit.edu.lego.kompaktor.model.LightSwitcher;
//import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.LightSensor;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Datalogger;


//TODO Rückwärtsfahren wenn Linie nicht erkannt? --> nur wenn Linie nicht einfach endet

public class DataTest {

	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
	final static int travelLengthLine = 3;
	final static int ThresholdAngleForward = 5;
	
	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchright = new TouchSensor(SensorPort.S3);
//		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor lightSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
				
		//Thread zum switchen
//		LightSwitcher switchThread = new LightSwitcher();
		
//		boolean left  = false;
//		boolean right  = false;
		
		Datalogger dl = new Datalogger();
		int size = 600;
		
	      boolean more = true;
	      while(more)
	      {
	         for(int i = 0 ; i<size; i++)
	         {
//	            float x = i*0.5f;
	            dl.writeLog(lightSensor.readValue());
	         }
	         dl.transmit(); 
	         LCD.clear();
	         LCD.drawString("more?",0,2);
	         LCD.refresh();
	         more = 1 == Button.waitForAnyPress();
	      }
		
	}

}
