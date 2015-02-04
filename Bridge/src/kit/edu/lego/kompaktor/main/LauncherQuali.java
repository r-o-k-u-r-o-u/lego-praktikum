package kit.edu.lego.kompaktor.main;

import kit.edu.lego.kompaktor.behavior.BarcodeDetector;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner;
//import kit.edu.lego.kompaktor.behavior.UTurnRunner;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner.LEVEL_NAMES;
import kit.edu.lego.kompaktor.model.Kompaktor;
//import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

public class LauncherQuali {
	
	public static LauncherQuali launcher = null;
	public static LauncherQuali getLauncher() {
		if (launcher == null)
			launcher = new LauncherQuali();
		return launcher;
	}
	
	private final int numMaxProgramms = ParcoursRunner.LEVEL_NAMES.values().length;
	
	public static void main(String[] args) {	
		getLauncher();	
	}
	
	private LauncherQuali() {

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
			while (!Kompaktor.isTouched());
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
		if(current.equals(LEVEL_NAMES.LINE_FOLLOW)){
			transitionStartLineFollow();
			Kompaktor.startLevel(LEVEL_NAMES.LINE_FOLLOW);
			transitionEndLineFollow();
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
			while (!bar.isDone()) {
				Thread.yield();
			}
			//Labyrinth und barcode stoppen
			labyrinth.stop();
			bar.stop();
			//transition zu Gate
			transitionEndLabyrinth();
		}
	}
	
	private void transitionStartLineFollow(){
		//detector ausrichten
				Kompaktor.stretchArm();
		Kompaktor.DIFF_PILOT.travel(50);
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
		while(!bar.isDone());
		Kompaktor.DIFF_PILOT.stop();
		bar.stop();
		Kompaktor.DIFF_PILOT.travel(20);
	}
	
	private void transitionStartLabyrinth(){
		//Arm einfahren
		Kompaktor.parkArm();
		Kompaktor.DIFF_PILOT.rotate(180);
	}
	
	private void transitionEndLabyrinth(){
		
	}
	

}
