package kit.edu.lego.kompaktor.behavior;

import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;

public class Collision {

	
	public static void main(String [] args) {
		
		TouchSensor sensorLeft = new TouchSensor(SensorPort.S3);
		TouchSensor sensorRight = new TouchSensor(SensorPort.S2);
		
		while(true) {
			
			if(sensorLeft.isPressed() && sensorRight.isPressed()) {
				Sound.playTone(1000, 200);
			} else if (sensorLeft.isPressed()) {
				Sound.playTone(800, 200);
			} else if (sensorRight.isPressed()) {
				Sound.playTone(500, 200);
			}
			
		}
	}
}
