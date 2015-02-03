package kit.edu.lego.kompaktor.test;

import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;

public class DistanceTest {

	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		
		
		
		while(!touchright.isPressed() && !touchleft.isPressed()){
			System.out.println(sonic.getDistance());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
