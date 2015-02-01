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
			
			if (clicked == Button.ID_LEFT) {
				curr = (curr==0) ? numMaxProgramms-1 : curr-1;
			} else if (clicked == Button.ID_RIGHT) {
				curr = (curr+1) % numMaxProgramms;
			} else if (clicked == Button.ID_ENTER) {
				selected = true;
			} else if (clicked == Button.ID_ESCAPE) {
				selected = true;
				curr = -1;
			}
			clicked = 0;
		}
		
		if (curr == -1) {
			Sound.buzz();
		} else {
			Sound.beep();

			// Hier initialisieren sonst muss der LightSwitcher initialisiert werden
			// obwohl er möglicherweise gar nicht gebraucht wird.

			Kompaktor.showText("Selected LEVEL =\n\n" + LEVEL_NAMES.values()[curr] + "\n\nUse BUMPER to start");

			Sound.beepSequenceUp();
			while (Kompaktor.isTouched());

			// assert that the level will end normally for now
			Kompaktor.startLevel(LEVEL_NAMES.values()[curr]);
			
		}
	}
	
	public ParcoursRunner getSegmentRunnerThread(ParcoursRunner.LEVEL_NAMES levelName) {
		return ParcoursRunner.getNewRunner(levelName);
	}

}
