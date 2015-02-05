package kit.edu.lego.kompaktor.test;

import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.Motor;

public class MaxSpeedTest {

	public static void main(String[] args) {
		//über pilot
		double speed = Kompaktor.DIFF_PILOT.getMaxTravelSpeed();
		Kompaktor.DIFF_PILOT.setTravelSpeed(speed);
		Kompaktor.DIFF_PILOT.travel(40);
		//direkter zugriff
		Motor.B.setSpeed(Float.MAX_VALUE);
		Motor.C.setSpeed(Float.MAX_VALUE);
		Motor.B.backward();
		Motor.C.backward();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
