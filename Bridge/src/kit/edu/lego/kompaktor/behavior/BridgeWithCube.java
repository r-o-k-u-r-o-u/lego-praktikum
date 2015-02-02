package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Cube;
import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.Sound;

public class BridgeWithCube extends ParcoursRunner{

	final static int lightThreshold = 40;
	
	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor lightsensor = ParcoursRunner.LIGHT_SENSOR;
//		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT;
				
		while(!Kompaktor.isTouched());
//		LightSwitcher.initAngles();
		
		//Kompaktor.LIGHT_SENSOR.setFloodlight(false);
		
		ParcoursRunner elevatorBridge = Kompaktor.startLevel(LEVEL_NAMES.BRIDGE_ELEVATOR, true);
		
		while(!elevatorBridge.isDone());
		
		//fertig
		Sound.beep();
		//end
		while(!Kompaktor.isTouched());

	}

	private BridgeRun bridge;
	private boolean finish;
	
	@Override
	public void run() {
		try{
			//Brücke faren
			bridge = (BridgeRun)Kompaktor.startLevel(LEVEL_NAMES.BRIDGE, true);
			//warten auf Licht
			//int[] ligths = new int[]{0, 0, 0};
			while(Kompaktor.LIGHT_SENSOR.readValue() < lightThreshold){
			//while(ligths[0] > 5 || ligths[1] < 26){
				if(Thread.interrupted())
					throw new InterruptedException();
				//ligths = Kompaktor.readLightDifferenceArr();
				Thread.yield();
			}
			//Brückenfahrt stoppen
			bridge.stop();
			//drehen vom Abhang weg
			if(((BridgeRun)bridge).getLastHole() == RotantionDirection.Left){
				Kompaktor.DIFF_PILOT.rotate(-20);
			} else {
				Kompaktor.DIFF_PILOT.rotate(20);
			}
			//etwas vorwärtsfahren
			Kompaktor.DIFF_PILOT.travel(15);
			//Grenzwert erhöhen für Licht
			//BridgeRun.thresholdWood = 40;
			//Brückenfahrt
			bridge = (BridgeRun)Kompaktor.startLevel(LEVEL_NAMES.BRIDGE, true);
			//soll nur 2 Sekunden laufen
			long time = System.currentTimeMillis();
			while(Math.abs(System.currentTimeMillis() - time) < 2000){
				if(Thread.interrupted())
					throw new InterruptedException();
			}
			//stoppen
			bridge.stop();
			//zum Loch drehen
			if(((BridgeRun)bridge).getLastHole() == RotantionDirection.Left){
				Kompaktor.DIFF_PILOT.rotate(-85);
			} else {
				Kompaktor.DIFF_PILOT.rotate(85);
			}
			//vorwärtsfahren bis am Rand
			//LightSwitcher.setAngle(0);
			Kompaktor.stretchArm();
			Kompaktor.DIFF_PILOT.forward();
			while(Kompaktor.LIGHT_SENSOR.readValue() > 33){
				if(Thread.interrupted())
					throw new InterruptedException();
			}
			Kompaktor.DIFF_PILOT.stop();
			//noch ein kleines Stück vor
			Kompaktor.DIFF_PILOT.travel(1);
			//messen des Winkels
			int left = -90, rigth = 90;
			int angle = 0;
			while(Kompaktor.LIGHT_SENSOR.readValue() < 30){
				if(Thread.interrupted())
					throw new InterruptedException();
				LightSwitcher.setAngle(angle);
				rigth = angle;
				angle += 3;
			}
			//LightSwitcher.setAngle(0);
			Kompaktor.stretchArm();
			angle = 0;
			while(Kompaktor.LIGHT_SENSOR.readValue() < 30){
				if(Thread.interrupted())
					throw new InterruptedException();
				LightSwitcher.setAngle(angle);
				left = angle;
				angle -= 3;
			}
			int diff = rigth + left;
			//anpassen dass genau 90°
			Kompaktor.DIFF_PILOT.rotate(-diff/2);
			//Stück zurück
			Kompaktor.DIFF_PILOT.travel(-8);
			//drehen zum Fahrstuhl
			if(((BridgeRun)bridge).getLastHole() == RotantionDirection.Left){
				Kompaktor.DIFF_PILOT.rotate(-85);
			} else {
				Kompaktor.DIFF_PILOT.rotate(85);
			}
			//Sensor einfahren
			//LightSwitcher.setAngle(-90);
			Kompaktor.parkArm();
			//warten auf verbindung
			while(!Cube.openConnection(Cube.LIFT)){
				Thread.sleep(1000);
			}
			//warten auf grün
			while(Kompaktor.LIGHT_SENSOR.readValue() < 40){
				if(Thread.interrupted())
					throw new InterruptedException();
			}
			//hineinfahren
			Kompaktor.DIFF_PILOT.backward();
			while(!Kompaktor.isTouched());
			Kompaktor.DIFF_PILOT.stop();
			//runter fahren
			Cube.goDown();
			//warten bis beendet
			while(!Cube.canExit()){
				Thread.sleep(1000);
			}
			//raus fahren
			Kompaktor.DIFF_PILOT.travel(-30);
			//Verbindung schließen
			Cube.closeConnection();
			finish = true;
			//wait until interrupted
			Thread.sleep(Long.MAX_VALUE);
			
		} catch (InterruptedException e){
			Kompaktor.DIFF_PILOT.stop();
			if(bridge != null && bridge.isAlive())
				try {
					bridge.stop();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
		}
		
	}

	@Override
	public void init() {
		finish = false;
	}

	@Override
	public boolean isDone() {
		return finish;
	}

}
