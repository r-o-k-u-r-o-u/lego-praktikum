package kit.edu.lego.kompaktor.main;

import kit.edu.lego.kompaktor.behavior.ParcoursRunner;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner.LEVEL_NAMES;
import kit.edu.lego.kompaktor.model.Kompaktor;
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
			while (!Kompaktor.isTouched());
			Sound.beepSequenceUp();
			
			// assert that the level will end normally for now
			Kompaktor.startLevel(LEVEL_NAMES.values()[curr]);
			
			
		}
		
	}
	
	private void runParcours() {
		while (true) {
			// do something
		}
	}
	
//	public ParcoursRunner getSegmentRunnerThread(ParcoursRunner.LEVEL_NAMES levelName) {
//		return ParcoursRunner.getNewRunner(levelName);
//	}

}
