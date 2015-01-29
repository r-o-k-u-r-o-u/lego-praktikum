package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class UTurnRunner {
	
	final static int travelSpeedUTurn = 20;
	final static int travelLengthUTurn = 3;
	final static int travelDistance = 20;
	final static int distanceWallLost = 60;
	final static int ThresholdDistanceForward = 1;

	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, false);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		double lastDiff = 0;
		int lastDistance = 0;
		long timeLastCorrect = System.currentTimeMillis();
		
		while(!touchright.isPressed() && !touchleft.isPressed()){
			int distance = sonic.getDistance();
			if(distance >= 255){ //reject errors
				if((System.currentTimeMillis() - timeLastCorrect) < 300){
					distance = lastDistance;
				}
			} else { //correct value
				timeLastCorrect = System.currentTimeMillis();
			}
			
			double diff = distance - travelDistance;
			
			
			if(distance >= distanceWallLost) {//wall lost

				pilot.travel(10);
				pilot.rotate(-90);
				pilot.travel(25);
				
			} else {
				//normales Wand folgen
				if(Math.abs(diff) < ThresholdDistanceForward){
					pilot.travel(travelLengthUTurn, true);
				} else {
					if(diff > 0){
						//zurück zur Wand
						if(lastDiff > diff + 1){ //sich immer weiter annähert
							//nur geradeaus fahren
							pilot.travel(travelLengthUTurn, true);
						} else { //roboter entfernt sich shon
							//starke veränderung
							double value = -700 / diff;
							pilot.travelArc(value, travelLengthUTurn, true);
						}
					} else {
						//weg von der Wand
						if(lastDiff > diff - 2){ //sich immer weiter annähert
							//starke veränderung
							double value = -300 / diff;
							pilot.travelArc(value, travelLengthUTurn, true);
						} else { //roboter entfernt sich shon
							//nur geradeaus fahren
							pilot.travel(travelLengthUTurn, true);
						}
					}
				}
			}
			
			//letzte differenz / distanz speichern
			lastDiff = diff;
			lastDistance = distance;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

}
