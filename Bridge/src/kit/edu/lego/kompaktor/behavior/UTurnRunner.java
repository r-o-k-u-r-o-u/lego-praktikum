package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class UTurnRunner {
	
	final static int travelSpeedUTurn = 20;
	final static int travelLengthUTurn = 5;
	final static int travelDistance = 20;
	final static int distanceWallLost = 70;
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
		
		boolean test = true;
		
		while(test){ //!touchright.isPressed() && !touchleft.isPressed()
			
			int distance = sonic.getDistance();
			if(distance >= 255){ //reject errors
				if((System.currentTimeMillis() - timeLastCorrect) < 300){
					distance = lastDistance;
				}
			} else { //correct value
				timeLastCorrect = System.currentTimeMillis();
			}
			
			double diff = distance - travelDistance;
			
			
			//teste ob Wand vorne ist
			if (touchright.isPressed() || touchleft.isPressed()){
				int angle = 90;
				if(touchright.isPressed() && touchleft.isPressed()){
					//rechts drehen
					angle = 90;
				} else if(touchleft.isPressed()){
					//kleine Linksdrehung
					angle = -45;
				} else if(touchright.isPressed()){
					//kleine Rechtsdrehung
					angle = 45;
				}
				
				//etwas zurück
				pilot.travel(-10);
				//drehen
				pilot.rotate(angle);
				
			} else { //freie fahrt
			
				if(distance >= distanceWallLost) {//wall lost
	
					pilot.travel(10);
					pilot.rotate(-90);
					while(sonic.getDistance() > distanceWallLost){
						pilot.travel(travelLengthUTurn, true);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					pilot.travel(5);
					
				} else {
					//normales Wand folgen
					if(Math.abs(diff) < ThresholdDistanceForward){
						pilot.travel(travelLengthUTurn, true);
					} else {
						double value = Math.abs(diff);
						if(value > 8){
							value = 8;
						}
						value = 10.5 - value;
						value = Math.sqrt(value);
						if (diff < 0)
							value *= -1;
						value *= -56;
						
						System.out.println("dist: " + distance + "val: " + value);
						
						if(diff > 0){
							//zurück zur Wand
							if(lastDiff > diff + 200){ //sich immer weiter annähert (diff > 10 ? + 1 : -1)
								//nur geradeaus fahren
								pilot.travel(travelLengthUTurn, true);
							} else { //roboter entfernt sich shon
								//starke veränderung
								//double value = -500 / diff;
								pilot.travelArc(value, travelLengthUTurn, true);
								//Sound.beep();
							}
						} else {
							//weg von der Wand
							if(lastDiff > diff - 200){ //sich immer weiter annähert
								//starke veränderung
								//double value = -300 / diff;
								pilot.travelArc(value, travelLengthUTurn, true);
								//Sound.buzz();
							} else { //roboter entfernt sich shon
								//nur geradeaus fahren
								pilot.travel(travelLengthUTurn, true);
							}
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
		
		while(!touchright.isPressed() && !touchleft.isPressed());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

}
