package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Kompaktor;

public class UTurnRunner extends ParcoursRunner{
	
	final static int travelSpeedUTurn = 200;
	final static int travelLengthUTurn = 5;
	final static int travelDistance = 30;
	final static int distanceWallLost = 70;
	final static int ThresholdDistanceForward = 2;

	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchRight = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchLeft = ParcoursRunner.TOUCH_LEFT;
		
		while(!Kompaktor.isTouched());
//		LightSwitcher.initAngles();
		
		Kompaktor.startLevel(LEVEL_NAMES.U_TURN);
		
//		UTurnRunner uturn = new UTurnRunner();
//		uturn.init();
//		uturn.start();
//		
//		while(!uturn.isDone());
//
//		uturn.interrupt();
//		try {
//			uturn.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		//end
		while(!Kompaktor.isTouched());

	}
	
	
	public void run(){
		try{
			double lastDiff = 0;
			int lastDistance = 0;
			long timeLastCorrect = System.currentTimeMillis();
			//pilot_reverse.setTravelSpeed(travelSpeedUTurn);
			
			Kompaktor.DIFF_PILOT_REVERSE.setTravelSpeed(600);
			
			while(true){ //!touchRight.isPressed() && !touchLeft.isPressed()
				if(Thread.interrupted())
					throw new InterruptedException();
				int distance = Kompaktor.SONIC_SENSOR.getDistance();
				if(distance >= 255){ //reject errors
					if((System.currentTimeMillis() - timeLastCorrect) < 350){
						distance = lastDistance;
					}
				} else { //correct value
					timeLastCorrect = System.currentTimeMillis();
				}
				
				double diff = distance - travelDistance;
				
				//teste ob Wand vorne ist
				if (Kompaktor.isTouched()){
					Thread.sleep(100);
					int angle = 90;
					if(Kompaktor.isTouchedBoth()){
						//rechts drehen
						angle = 90;
					} else if(Kompaktor.isTouchedLeft()){
						//kleine Linksdrehung
						angle = 100;
					} else if(Kompaktor.isTouchedRight()){
						//kleine Rechtsdrehung
						angle = 30;
					}
					
					//etwas zurück
					Kompaktor.DIFF_PILOT_REVERSE.travel(-10);
					//drehen
					Kompaktor.DIFF_PILOT_REVERSE.rotate(angle);
					
				} else { //freie fahrt
				
					if(distance >= distanceWallLost) {//wall lost
		
						Kompaktor.DIFF_PILOT_REVERSE.travel(10);
						Kompaktor.DIFF_PILOT_REVERSE.rotate(-90);
						//vorwärtsfahren bis Wand an der Seite sehen oder gegen Wand gefahren
						while(Kompaktor.SONIC_SENSOR.getDistance() > distanceWallLost && Kompaktor.isNotTouched()){
							if(Thread.interrupted())
								throw new InterruptedException();
							
							Kompaktor.DIFF_PILOT_REVERSE.travel(travelLengthUTurn, true);
							Thread.sleep(100);
						}
						//nur ein stück vorwärtsfahren, wenn möglich
						if(Kompaktor.isNotTouched())
							Kompaktor.DIFF_PILOT_REVERSE.travel(5);
						
					} else {
						//normales Wand folgen
						if(Math.abs(diff) < ThresholdDistanceForward){
							Kompaktor.DIFF_PILOT_REVERSE.travel(travelLengthUTurn, true);
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
								//zurück zur Wand
								if(lastDiff > diff + 1){ //sich immer weiter annähert (diff > 10 ? + 1 : - 1)
									//nur geradeaus fahren
									Kompaktor.DIFF_PILOT_REVERSE.travel(travelLengthUTurn, true);
								} else { //roboter entfernt sich shon
									//starke veränderung
									//double value = -500 / diff;
									Kompaktor.DIFF_PILOT_REVERSE.travelArc(value, travelLengthUTurn, true);
									//Sound.beep();
								}
							} else {
								//weg von der Wand
								if(lastDiff > diff - 1){ //sich immer weiter annähert (diff < -10 ? - 1 : + 1)
									//starke veränderung
									//double value = -300 / diff;
									value /= 2;
									Kompaktor.DIFF_PILOT_REVERSE.travelArc(value, travelLengthUTurn, true);
									//Sound.buzz();
								} else { //roboter entfernt sich shon
									//nur geradeaus fahren
									Kompaktor.DIFF_PILOT_REVERSE.travel(travelLengthUTurn, true);
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
			Kompaktor.DIFF_PILOT_REVERSE.stop();
		}
	}


	@Override
	public void init() {
		//LightSwitcher.setAngle(-90);
		Kompaktor.parkArm();
		Kompaktor.DIFF_PILOT_REVERSE.setTravelSpeed(Kompaktor.DIFF_PILOT_REVERSE.getMaxTravelSpeed());
	}


	@Override
	public boolean isDone() {
		//kann das Ende nicht selbst erkennen
		return false;
	}
	
}
