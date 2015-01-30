package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.threading.ParcoursRunner;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class UTurnRunner extends ParcoursRunner{
	
	final static int travelSpeedUTurn = 200;
	final static int travelLengthUTurn = 5;
	final static int travelDistance = 20;
	final static int distanceWallLost = 70;
	final static int ThresholdDistanceForward = 2;

	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, false);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		
		
		UTurnRunner uturn = new UTurnRunner(touchright, touchleft, sonic, pilot);
		uturn.init();
		uturn.start();
		
		while(!uturn.isDone());

		uturn.interrupt();
		try {
			uturn.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

	private TouchSensor touchright;
	private TouchSensor touchleft;
	private UltrasonicSensor sonic;
	private DifferentialPilot pilot;
	
	public UTurnRunner(TouchSensor touchright, TouchSensor touchleft, UltrasonicSensor sonic, DifferentialPilot pilot) {
		this.touchright = touchright;
		this.touchleft = touchleft;
		this.sonic = sonic;
		this.pilot = pilot;
	}
	
	
	public void run(){
		try{
			double lastDiff = 0;
			int lastDistance = 0;
			long timeLastCorrect = System.currentTimeMillis();
			//pilot.setTravelSpeed(travelSpeedUTurn);
			
			while(true){ //!touchright.isPressed() && !touchleft.isPressed()
				if(Thread.interrupted())
					throw new InterruptedException();
				int distance = sonic.getDistance();
				if(distance >= 255){ //reject errors
					if((System.currentTimeMillis() - timeLastCorrect) < 350){
						distance = lastDistance;
					}
				} else { //correct value
					timeLastCorrect = System.currentTimeMillis();
				}
				
				double diff = distance - travelDistance;
				
				//teste ob Wand vorne ist
				if (touchright.isPressed() || touchleft.isPressed()){
					Thread.sleep(100);
					int angle = 90;
					if(touchright.isPressed() && touchleft.isPressed()){
						//rechts drehen
						angle = 90;
					} else if(touchleft.isPressed()){
						//kleine Linksdrehung
						angle = 100;
					} else if(touchright.isPressed()){
						//kleine Rechtsdrehung
						angle = 30;
					}
					
					//etwas zur�ck
					pilot.travel(-10);
					//drehen
					pilot.rotate(angle);
					
				} else { //freie fahrt
				
					if(distance >= distanceWallLost) {//wall lost
		
						pilot.travel(10);
						pilot.rotate(-90);
						//vorw�rtsfahren bis Wand an der Seite sehen oder gegen Wand gefahren
						while(sonic.getDistance() > distanceWallLost && !touchright.isPressed() && !touchleft.isPressed()){
							if(Thread.interrupted())
								throw new InterruptedException();
							
							pilot.travel(travelLengthUTurn, true);
							Thread.sleep(100);
						}
						//nur ein st�ck vorw�rtsfahren, wenn m�glich
						if(!touchright.isPressed() && !touchleft.isPressed())
							pilot.travel(5);
						
					} else {
						//normales Wand folgen
						if(Math.abs(diff) < ThresholdDistanceForward){
							pilot.travel(travelLengthUTurn, true);
						} else {
							double value = Math.abs(diff);
							if(value > 200){
								value = 200;
							}
							value = 220 - value;
							value = Math.sqrt(value);
							if (diff < 0)
								value *= -1;
							value *= -30;
							
							System.out.println("dist: " + distance + "val: " + value);
							
							if(diff > 0){
								//zur�ck zur Wand
								if(lastDiff > diff + 1){ //sich immer weiter ann�hert (diff > 10 ? + 1 : - 1)
									//nur geradeaus fahren
									pilot.travel(travelLengthUTurn, true);
								} else { //roboter entfernt sich shon
									//starke ver�nderung
									//double value = -500 / diff;
									pilot.travelArc(value, travelLengthUTurn, true);
									//Sound.beep();
								}
							} else {
								//weg von der Wand
								if(lastDiff > diff - 1){ //sich immer weiter ann�hert (diff < -10 ? - 1 : + 1)
									//starke ver�nderung
									//double value = -300 / diff;
									value /= 2;
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
				
				Thread.sleep(150);

			}
		} catch (InterruptedException e){
			pilot.stop();
		}
	}


	@Override
	public void init() {
		LightSwitcher.setAngle(-90);
	}


	@Override
	public boolean isDone() {
		//kann das Ende nicht selbst erkennen
		return false;
	}
	
}
