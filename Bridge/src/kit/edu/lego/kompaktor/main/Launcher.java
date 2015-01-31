package kit.edu.lego.kompaktor.main;

import kit.edu.lego.kompaktor.behavior.ParcoursRunner;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner.LEVEL_NAMES;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;

public class Launcher {
	
	public static Launcher launcher = null;
	public static Launcher getLauncher() {
		if (launcher == null)
			launcher = new Launcher();
		return launcher;
	}
	
	private final int numMaxProgramms = ParcoursRunner.LEVEL_NAMES.values().length;
	
	private ParcoursRunner currentRunner;
	
	public static void main(String[] args) {	
		getLauncher();	
	}
	
	private Launcher() {

		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		
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

			LightSwitcher.initAngles();

			LCD.clear();
			System.out.println("Selected LEVEL =\n\n" + LEVEL_NAMES.values()[curr] + "\n\nUse BUMPER to start");
			// start something else

			while (!touchright.isPressed() && !touchleft.isPressed());

			currentRunner = ParcoursRunner.getNewRunner(LEVEL_NAMES.values()[curr]);

			currentRunner.init();
			currentRunner.start();

			while (!currentRunner.isDone());

			try {
				currentRunner.stop();
			} catch (InterruptedException e) {
				System.out.println("Launcher error.");
			}
			
		}
		Button.waitForAnyPress();
	}
	
	public ParcoursRunner getSegmentRunnerThread(ParcoursRunner.LEVEL_NAMES levelName) {
		return ParcoursRunner.getNewRunner(levelName);
	}

}
