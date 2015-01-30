package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.TouchSensor;


public class UTurnRunner extends ParcoursRunner{
	
	final static int travelSpeedUTurn = 200;
	final static int travelLengthUTurn = 5;
	final static int travelDistance = 20;
	final static int distanceWallLost = 70;
	final static int ThresholdDistanceForward = 2;

	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchRight = ParcoursRunner.TOUCH_RIGHT;
		TouchSensor touchLeft = ParcoursRunner.TOUCH_LEFT;
		
		while(!touchRight.isPressed() && !touchLeft.isPressed());
		LightSwitcher.initAngles();
		
		
		
		UTurnRunner uturn = new UTurnRunner();
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
		while(!touchRight.isPressed() && !touchLeft.isPressed());

	}
	
	
	public void run(){
		try{
			double lastDiff = 0;
			int lastDistance = 0;
			long timeLastCorrect = System.currentTimeMillis();
			//pilot_reverse.setTravelSpeed(travelSpeedUTurn);
			
			while(true){ //!touchRight.isPressed() && !touchLeft.isPressed()
				if(Thread.interrupted())
					throw new InterruptedException();
				int distance = sonicSensor.getDistance();
				if(distance >= 255){ //reject errors
					if((System.currentTimeMillis() - timeLastCorrect) < 350){
						distance = lastDistance;
					}
				} else { //correct value
					timeLastCorrect = System.currentTimeMillis();
				}
				
				double diff = distance - travelDistance;
				
				//teste ob Wand vorne ist
				if (touchRight.isPressed() || touchLeft.isPressed()){
					Thread.sleep(100);
					int angle = 90;
					if(touchRight.isPressed() && touchLeft.isPressed()){
						//rechts drehen
						angle = 90;
					} else if(touchLeft.isPressed()){
						//kleine Linksdrehung
						angle = 100;
					} else if(touchRight.isPressed()){
						//kleine Rechtsdrehung
						angle = 30;
					}
					
					//etwas zur�ck
					pilot_reverse.travel(-10);
					//drehen
					pilot_reverse.rotate(angle);
					
				} else { //freie fahrt
				
					if(distance >= distanceWallLost) {//wall lost
		
						pilot_reverse.travel(10);
						pilot_reverse.rotate(-90);
						//vorw�rtsfahren bis Wand an der Seite sehen oder gegen Wand gefahren
						while(sonicSensor.getDistance() > distanceWallLost && !touchRight.isPressed() && !touchLeft.isPressed()){
							if(Thread.interrupted())
								throw new InterruptedException();
							
							pilot_reverse.travel(travelLengthUTurn, true);
							Thread.sleep(100);
						}
						//nur ein st�ck vorw�rtsfahren, wenn m�glich
						if(!touchRight.isPressed() && !touchLeft.isPressed())
							pilot_reverse.travel(5);
						
					} else {
						//normales Wand folgen
						if(Math.abs(diff) < ThresholdDistanceForward){
							pilot_reverse.travel(travelLengthUTurn, true);
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
									pilot_reverse.travel(travelLengthUTurn, true);
								} else { //roboter entfernt sich shon
									//starke ver�nderung
									//double value = -500 / diff;
									pilot_reverse.travelArc(value, travelLengthUTurn, true);
									//Sound.beep();
								}
							} else {
								//weg von der Wand
								if(lastDiff > diff - 1){ //sich immer weiter ann�hert (diff < -10 ? - 1 : + 1)
									//starke ver�nderung
									//double value = -300 / diff;
									value /= 2;
									pilot_reverse.travelArc(value, travelLengthUTurn, true);
									//Sound.buzz();
								} else { //roboter entfernt sich shon
									//nur geradeaus fahren
									pilot_reverse.travel(travelLengthUTurn, true);
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
			pilot_reverse.stop();
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
