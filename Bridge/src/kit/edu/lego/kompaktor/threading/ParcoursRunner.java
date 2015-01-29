package kit.edu.lego.kompaktor.threading;

import kit.edu.lego.kompaktor.behavior.*;
import lejos.nxt.LightSensor;
import lejos.robotics.navigation.DifferentialPilot;

public abstract class ParcoursRunner extends Thread {
	
	public static final String LINEFOLLOW = "LineFollow";
	public static final String UTURN = "UTurn";
	public static final String LABYRINTH = "Labyrinth";
	public static final String BRIDGE = "Bridge";
	public static final String ELEVATOR = "Elevator";
	public static final String LED_CUBE = "LED-Cube";
	public static final String ROPE_BRIDGE = "RopeBridge";
	public static final String ROLLS = "Rolls";
	
	public static final String[] LEVELNAMES = {
		UTURN,
		LINEFOLLOW,
		BRIDGE,
		LED_CUBE,
		ELEVATOR,
		LABYRINTH,
		ROLLS,
		ROPE_BRIDGE};
	
	public static final String[] getLevelNames() {
		return LEVELNAMES;
	}
	
	public static final ParcoursRunner getNewRunner(String levelName, LightSensor lightSensor, DifferentialPilot pilot) {
		switch (levelName) {
		//case LINEFOLLOW:	return new LineRunner(lightSensor, pilot);
		//case UTURN:			return new UTurnRunner(lightSensor, pilot);
		default: return null;
		}
	}
	
	public abstract void run();
	public abstract void stop();
	public abstract void init();
	//public abstract void transition(String toLevelName);
	
}
