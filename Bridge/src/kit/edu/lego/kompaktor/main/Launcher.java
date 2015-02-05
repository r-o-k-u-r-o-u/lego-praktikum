package kit.edu.lego.kompaktor.main;

import kit.edu.lego.kompaktor.behavior.BarcodeDetector;
import kit.edu.lego.kompaktor.behavior.LineRunner;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner;
//import kit.edu.lego.kompaktor.behavior.UTurnRunner;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner.LEVEL_NAMES;
import kit.edu.lego.kompaktor.model.Kompaktor;
//import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

public class Launcher {
	
	public static Launcher launcher = null;
	public static Launcher getLauncher() {
		if (launcher == null)
			launcher = new Launcher();
		return launcher;
	}
	
	private final int numMaxProgramms = ParcoursRunner.LEVEL_NAMES.values().length;
	
	public static void main(String[] args) {	
		getLauncher();	
	}
	
	private Launcher() {

		boolean selected = false;
		int curr = 0;
		int clicked = 0;
		
		while (!selected) {
			
			LCD.clear();
			System.out.println("Level=\n\n"+ParcoursRunner.LEVEL_NAMES.values()[curr]+"\n\nStart with ENTER");
			
			clicked = Button.waitForAnyPress();
			
			switch (clicked) {
				case Button.ID_LEFT: 
					curr = (curr == 0) ? numMaxProgramms-1 : curr - 1; 
					break;
				case Button.ID_RIGHT:
					curr = (curr + 1) % numMaxProgramms;
					break;
				case Button.ID_ENTER:
					selected = true;
					break;
				case Button.ID_ESCAPE:
					selected = true;
					curr = -1;
			}
			clicked = 0;
		}
		
		if (curr == -1) {
			Sound.buzz();
		} else {
			Sound.beep();

			Kompaktor.showText("Selected LEVEL =\n\n" + LEVEL_NAMES.values()[curr] + "\n\nUse BUMPER to start");

			Sound.beepSequenceUp();
			while (!Kompaktor.isTouched())
				try {
					Thread.sleep(Kompaktor.SLEEP_INTERVAL);
				} catch (InterruptedException e1) {}
			
			Sound.beepSequenceUp();
			
			// assert that the level will end normally for now
			//Kompaktor.startLevel(LEVEL_NAMES.values()[curr]);
			try {
				runParcours(curr);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void runParcours(int curr) throws InterruptedException {
		LEVEL_NAMES current = LEVEL_NAMES.values()[curr];
		//wenn UTurn ausgewählt
		if(current.equals(LEVEL_NAMES.U_TURN)){
			transitionStartUTurn();
			//Barcode erstellen und starten
			BarcodeDetector bar = new BarcodeDetector();
			bar.init();
			bar.start();
			//UTurn starten
			ParcoursRunner uturn = Kompaktor.startLevel(LEVEL_NAMES.LABYRINTH, true);
			//warten bis Barcode gefunden
			while (!bar.isDone())
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);

			//uturn und barcode stoppen
			uturn.stop();
			bar.stop();
			//transition zu LineFollow
			transitionEndUTurn();
			//auf LineRunner setzen
			current = LEVEL_NAMES.LINE_FOLLOW;
		}	
		if(current.equals(LEVEL_NAMES.LINE_FOLLOW)){
			transitionStartLineFollow();
			Kompaktor.startLevel(LEVEL_NAMES.LINE_FOLLOW);
			transitionEndLineFollow();
			current = LEVEL_NAMES.BRIDGE_ELEVATOR;
		}
		if(current.equals(LEVEL_NAMES.BRIDGE_ELEVATOR)){
			transitionStartBridgeWithCube();
			Kompaktor.startLevel(LEVEL_NAMES.BRIDGE_ELEVATOR);
			transitionEndBridgeWithCube();
			current = LEVEL_NAMES.LABYRINTH;
		}
		if(current.equals(LEVEL_NAMES.LABYRINTH)){
			transitionStartLabyrinth();
			//Barcode erstellen und starten
			BarcodeDetector bar = new BarcodeDetector();
			bar.init();
			bar.start();
			//Labyrinth starten
			ParcoursRunner labyrinth = Kompaktor.startLevel(LEVEL_NAMES.LABYRINTH, true);
			//warten bis Barcode gefunden			
			while (!bar.isDone()) 
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			
			//Labyrinth und barcode stoppen
			labyrinth.stop();
			bar.stop();
			//transition zu Gate
			transitionEndLabyrinth();
			//auf LineRunner setzen
			current = LEVEL_NAMES.GATE;
		}
		if(current.equals(LEVEL_NAMES.GATE)){
			transitionStartGate();
			Kompaktor.startLevel(LEVEL_NAMES.GATE);
			transitionEndGate();
			current = LEVEL_NAMES.TURN_TABLE;
		}
		if(current.equals(LEVEL_NAMES.TURN_TABLE)){
			transitionStartTurnTable();
			Kompaktor.startLevel(LEVEL_NAMES.TURN_TABLE);
			transitionEndTurnTable();
			current = LEVEL_NAMES.BOSS;
		}
		if(current.equals(LEVEL_NAMES.BOSS)){
			transitionStartBoss();
			//Barcode erstellen und starten
//			BarcodeDetector bar = new BarcodeDetector();
//			bar.init();
//			bar.start();
			//Labyrinth starten
//			ParcoursRunner boss = Kompaktor.startLevel(LEVEL_NAMES.LABYRINTH, true);
			Kompaktor.startLevel(LEVEL_NAMES.LABYRINTH);
			//warten bis Barcode gefunden			
//			while (!bar.isDone()) 
//				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			
			//Labyrinth und barcode stoppen
//			boss.stop();
//			bar.stop();
			//Ende
			transitionEndBoss();
		}
	}
	
	
	private void transitionStartUTurn(){
		//Arm parken
		Kompaktor.parkArm();
	}
	
	private void transitionEndUTurn(){
		//drehen
		Kompaktor.DIFF_PILOT.rotate(180);
	}
	
	private void transitionStartLineFollow() throws InterruptedException {
		//Sensor ausrichten
		Kompaktor.stretchArm();
		//vorwärts fahren bis Linie erkannt
		Kompaktor.DIFF_PILOT.forward();
		
		while(Kompaktor.LIGHT_SENSOR.readValue() < LineRunner.ThresholdLine)
			Thread.sleep(Kompaktor.SLEEP_INTERVAL);
		
		Kompaktor.DIFF_PILOT.stop();
	}
	
	private void transitionEndLineFollow() throws InterruptedException{
		//detector ausrichten
		Kompaktor.stretchArm();
		//neuen Barcode scannen
		BarcodeDetector bar = new BarcodeDetector();
		bar.init();
		bar.start();
		//vorwärts fahren
		Kompaktor.DIFF_PILOT.forward();
		
		//sobald barcode gefunden wird gestoppt
		while(!bar.isDone())
			Thread.sleep(Kompaktor.SLEEP_INTERVAL);
		Kompaktor.DIFF_PILOT.stop();
		bar.stop();
	}
	
	private void transitionStartBridgeWithCube(){
		//etwas vorfahren auf die Brücke
		Kompaktor.parkArm();
		Kompaktor.DIFF_PILOT.travel(20);
	}
	
	private void transitionEndBridgeWithCube(){
		
	}
	
	private void transitionStartLabyrinth(){
		//Arm einfahren
		Kompaktor.parkArm();
	}
	
	private void transitionEndLabyrinth(){
		
	}
	
	private void transitionStartGate(){
		//Arm einfahren
		Kompaktor.parkArm();
		//etwas vorwärtsfahren
		Kompaktor.DIFF_PILOT_REVERSE.travel(30);
	}
	
	private void transitionEndGate() throws InterruptedException{
		//detector ausrichten
		Kompaktor.stretchArm();
		//neuen Barcode scannen
		BarcodeDetector bar = new BarcodeDetector();
		bar.init();
		bar.start();
		//vorwärts fahren
		Kompaktor.DIFF_PILOT.forward();
		
		//sobald barcode gefunden wird gestoppt
		while(!bar.isDone())
			Thread.sleep(Kompaktor.SLEEP_INTERVAL);
		Kompaktor.DIFF_PILOT.stop();
		bar.stop();
	}
	
	private void transitionStartTurnTable(){
		//Arm einfahren
		Kompaktor.startArm();
		//etwas vorwärtsfahren
		Kompaktor.DIFF_PILOT.travel(10);
	}
	
	private void transitionEndTurnTable(){
		
	}
	
	private void transitionStartBoss(){
		Kompaktor.parkArm();
		Kompaktor.DIFF_PILOT_REVERSE.rotate(180);
		Kompaktor.DIFF_PILOT_REVERSE.travel(20);
		Kompaktor.DIFF_PILOT_REVERSE.rotate(-60);
		Kompaktor.DIFF_PILOT_REVERSE.travel(40);
	}
	
	private void transitionEndBoss(){
		Sound.beepSequenceUp();
		Sound.beepSequenceUp();
		Sound.beepSequenceUp();
		Sound.beepSequenceUp();
	}

}
