package kit.edu.lego.kompaktor.main;

import kit.edu.lego.kompaktor.behavior.LineRunner;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
//import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class Launcher {

	static String[] levelNames = {"UTurn",
								  "LineFollow",
								  "Bridge",
								  "LED-Cube",
								  "Fahrstuhl",
								  "Labyrinth",
								  "Rollenfeld"};
	
	static int numMaxProgramms = levelNames.length;
	
	public static void main(String[] args) {
		
		
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor lightSensor = new LightSensor(SensorPort.S1, true);
//		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		
		
		boolean selected = false;
		int curr = 0;
		int clicked = 0;
		
		while (!selected) {
			
			LCD.clear();
			System.out.println("Level=\n"+levelNames[curr]+"\n\nStart with ENTER.");
			
			clicked = Button.waitForAnyPress();
			
			if (clicked == Button.ID_LEFT) {
				curr = (curr==numMaxProgramms-1) ? 0 : curr+1;
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
			
			LCD.clear();
			System.out.println("Selected LEVEL = "+levelNames[curr]);
			// start something else
			
			while(!touchright.isPressed() && !touchleft.isPressed());
			
			if (curr == 1) {
				LineRunner run = new LineRunner(lightSensor, pilot);
				LightSwitcher.initAngles();
				
				while(!touchright.isPressed() && !touchleft.isPressed());
				
				run.start();
			}
			
			
		}

		Button.waitForAnyPress();
		
	}

}
