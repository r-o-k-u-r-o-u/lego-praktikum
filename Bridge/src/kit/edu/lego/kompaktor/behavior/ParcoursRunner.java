package kit.edu.lego.kompaktor.behavior;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public abstract class ParcoursRunner extends Thread {
	
	public static enum LEVEL_NAMES {
		
		LINE_FOLLOW("LineFollow"),
		U_TURN("UTurn"),
		LABYRINTH("Labyrinth"),
		BRIDGE("Bridge"),
		ELEVATOR("Elevator"),
		LED_CUBE("LED-Cube"),
		ROLLS("Rolls"),
		ROPE_BRIDGE("RopeBridge"),
		TURN_TABLE("TurnTable"),
		DOOR("door");
		
	    private final String text;

	    private LEVEL_NAMES(final String text) {
	        this.text = text;
	    }

	    @Override
	    public String toString() {
	        return text;
	    }
	    
	}
	
	public static final TouchSensor TOUCH_RIGHT = new TouchSensor(SensorPort.S3);
	public static final TouchSensor TOUCH_LEFT = new TouchSensor(SensorPort.S2);
	public static final LightSensor LIGHT_SENSOR = new LightSensor(SensorPort.S1, true);
	public static final UltrasonicSensor SONIC_SENSOR = new UltrasonicSensor(SensorPort.S4);
	public static final DifferentialPilot DIFF_PILOT = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
	public static final DifferentialPilot DIFF_PILOT_REVERSE = new DifferentialPilot(3, 17, Motor.C, Motor.B, false);	

	public static final ParcoursRunner getNewRunner(LEVEL_NAMES levelName) {
		switch (levelName) {
		case LINE_FOLLOW:	return new LineRunner();
		case U_TURN:		return new UTurnRunner();
		case BRIDGE:		return new BridgeRun();
//		case LED_CUBE:		return new ;
//		case ELEVATOR:		return new ;
//		case LABYRINTH:		return new ;
//		case ROLLS: 		return new ;
		case DOOR: 			return new GateRunner();
		case ROPE_BRIDGE:	return new RopeBridgeRun();
		case TURN_TABLE:	return new TurnTableRunner();
		default: return null;
		}
	}
	
	protected TouchSensor touchRight;
	protected TouchSensor touchLeft;
	protected LightSensor lightSensor;
	protected UltrasonicSensor sonicSensor;
	protected DifferentialPilot pilot;
	protected DifferentialPilot pilot_reverse;
	
	public ParcoursRunner() {
		touchRight = TOUCH_RIGHT;
		touchLeft = TOUCH_LEFT;
		lightSensor = LIGHT_SENSOR;
		sonicSensor = SONIC_SENSOR;
		pilot = DIFF_PILOT;
		pilot_reverse = DIFF_PILOT_REVERSE;
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
