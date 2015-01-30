package kit.edu.lego.kompaktor.threading;

import lejos.nxt.LightSensor;
import lejos.robotics.navigation.DifferentialPilot;

public abstract class ParcoursRunner extends Thread {
	
	public static final String LINE_FOLLOW = "LineFollow";
	public static final String U_TURN = "UTurn";
	public static final String LABYRINTH = "Labyrinth";
	public static final String BRIDGE = "Bridge";
	public static final String ELEVATOR = "Elevator";
	public static final String LED_CUBE = "LED-Cube";
	public static final String ROLLS = "Rolls";
	public static final String ROPE_BRIDGE = "RopeBridge";
	public static final String TURN_TABLE = "TurnTable";
	
	public static final String[] LEVEL_NAMES = {
		U_TURN,
		LINE_FOLLOW,
		BRIDGE,
		LED_CUBE,
		ELEVATOR,
		LABYRINTH,
		ROPE_BRIDGE,
		ROLLS,
		TURN_TABLE};
	
	public static final String[] getLevelNames() {
		return LEVEL_NAMES;
	}
	
	public static final ParcoursRunner getNewRunner(String levelName, LightSensor lightSensor, DifferentialPilot pilot) {
		switch (levelName) {
		//case LINEFOLLOW:	return new LineRunner(lightSensor, pilot);
		//case UTURN:			return new UTurnRunner(lightSensor, pilot);
		default: return null;
		}
	}
	
	public abstract void run();
	public abstract void init();
	public abstract boolean isDone();
	//public abstract void transition(String toLevelName);
	
	public void stop() throws InterruptedException{
		this.interrupt();
		this.join();
	}
	
}
