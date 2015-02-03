package kit.edu.lego.kompaktor.behavior;


public abstract class ParcoursRunner extends Thread {
	
	public static enum LEVEL_NAMES {
		
		LINE_FOLLOW("LineFollow"),
		U_TURN("UTurn"),
		LABYRINTH("Labyrinth"),
		BRIDGE("Bridge"),
		BRIDGE_ELEVATOR("Bridge&Elevator"),
		ELEVATOR("Elevator"),
		LED_CUBE("LED-Cube"),
		ROLLS("Rolls"),
		ROPE_BRIDGE("RopeBridge"),
		TURN_TABLE("TurnTable"),
		DOOR("door"),
		SEESAW("Seesaw");
		
	    private final String text;

	    private LEVEL_NAMES(final String text) {
	        this.text = text;
	    }

	    @Override
	    public String toString() {
	        return text;
	    }
	    
	}
	
	

	public static final ParcoursRunner getNewRunner(LEVEL_NAMES levelName) {
		switch (levelName) {
		case LINE_FOLLOW:	return new LineRunner();
		case U_TURN:		return new UTurnRunner();
		case BRIDGE:		return new BridgeRun();
		case BRIDGE_ELEVATOR:return new BridgeWithCube();
//		case LED_CUBE:		return new ;
		case ELEVATOR:		return new ElevatorRunner();
		case LABYRINTH:		return new LabyrinthRunner();
//		case ROLLS: 		return new ;
		case DOOR: 			return new GateRunner();
		case ROPE_BRIDGE:	return new RopeBridgeRun();
		case TURN_TABLE:	return new TurnTableRunner();
		case SEESAW:        return new SeesawRunner();
		default: return null;
		}
	}
	
//	protected TouchSensor touchRight;
//	protected TouchSensor touchLeft;
//	protected LightSensor lightSensor;
//	protected UltrasonicSensor sonicSensor;
//	protected DifferentialPilot pilot;
//	protected DifferentialPilot pilot_reverse;
	
	public ParcoursRunner() {
//		touchRight = TOUCH_RIGHT;
//		touchLeft = TOUCH_LEFT;
//		lightSensor = LIGHT_SENSOR;
//		sonicSensor = SONIC_SENSOR;
//		pilot = DIFF_PILOT;
//		pilot_reverse = DIFF_PILOT_REVERSE;
	}
	
	public abstract void run();
	public abstract void init();
	public abstract boolean isDone();
	//public abstract void transition(String toLevelName);
	
	public void stop() throws InterruptedException{
		while(this.isAlive()){
			this.interrupt();
			this.join(100);
		}
	}
	
}
