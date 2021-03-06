package kit.edu.lego.kompaktor.behavior;

import java.util.LinkedList;


public abstract class ParcoursRunner extends Thread {
	
	public static enum LEVEL_NAMES {
		
		U_TURN("UTurn"),
		LINE_FOLLOW("LineFollow"),
		BRIDGE("Bridge"),
		BRIDGE_ELEVATOR("Bridge&Elevator"),
//		LED_CUBE("LED-Cube"),
		ELEVATOR("Elevator"),
		LABYRINTH("Labyrinth"),
		GATE("Gate"),
		ROPE_BRIDGE("RopeBridge"),
		LINE_AFTER_ROPE_BRIDGE("Line after RopeBridge"),
		SEESAW("Seesaw"),
		TURN_TABLE("TurnTable"),
		BOSS("Boss");
		
	    private final String text;

	    private LEVEL_NAMES(final String text) {
	        this.text = text;
	    }

	    @Override
	    public String toString() {
	        return text;
	    }
	    
	}
	
	public static LEVEL_NAMES currentLevel;
	public static LinkedList<LEVEL_NAMES> levelSequence;
	static {
		levelSequence = new LinkedList<LEVEL_NAMES>();
		levelSequence.add(LEVEL_NAMES.U_TURN);
		levelSequence.add(LEVEL_NAMES.LINE_FOLLOW);
		levelSequence.add(LEVEL_NAMES.BRIDGE_ELEVATOR);
		levelSequence.add(LEVEL_NAMES.LABYRINTH);
		levelSequence.add(LEVEL_NAMES.GATE);
		levelSequence.add(LEVEL_NAMES.ROPE_BRIDGE);
		levelSequence.add(LEVEL_NAMES.TURN_TABLE);
//		levelSequence.add(LEVEL_NAMES.BOSS);
	}

	public static final ParcoursRunner getNewRunner(LEVEL_NAMES levelName) {
		switch (levelName) {
		case LINE_FOLLOW:	return new LineRunner();
		case U_TURN:		return new UTurnRunner();
		case BRIDGE:		return new BridgeRun();
		case BRIDGE_ELEVATOR:return new BridgeWithCube();
//		case LED_CUBE:		return new ;
		case ELEVATOR:		return new ElevatorRunner();
		case LABYRINTH:		return new LabyrinthRunner(false);
		case GATE: 			return new GateRunner();
		case ROPE_BRIDGE:	return new RopeBridgeRun();
		case LINE_AFTER_ROPE_BRIDGE: return new LineRunner();
		case TURN_TABLE:	return new TurnTableRunner();
		case SEESAW:        return new SeesawRunner();
		default: return null;
		}
	}
	
	public ParcoursRunner() {
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
