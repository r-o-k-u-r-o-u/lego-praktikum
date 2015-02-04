package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.LightSwitcher;


public class LineRunner extends ParcoursRunner {

	public final static int angleRotateLine = 20;
	public final static int travelSpeedLine = 20;
	public final static int travelLengthLine = 3;
	public final static int ThresholdAngleForward = 10;
	public final static int ThresholdLine = 40;
	public final static int numberSwitchFinish = 3;
	
	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		
		while(!Kompaktor.isTouched());
//		LightSwitcher.initAngles();
		
		Kompaktor.startLevel(LEVEL_NAMES.LINE_FOLLOW);
		
//		LineRunner line = new LineRunner();
//		line.init();
//		line.start();
//		
//		while(!line.isDone());
//		try {
//			line.stop();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		//end
		while(!Kompaktor.isTouched());

	}

	private LightSwitcher switchThread;
	private static int value = 0;
	
	@Deprecated
	public int getSwitchCounter() {
		if (switchThread != null)
			return switchThread.getSwitchCounter();
		else
			return 0;
	}
	
	public boolean isLightSwitcherActive(){
		return switchThread.isAlive();
	}
	
	public void run(){
		try{
			//int failure = 0; //zählt wie oft er stehen geblieben ist, aber die Linie nicht gesehen hat
			while(true){
				switchThread = new LightSwitcher();
				if(value >= 0 ) //^ (failure % 2 > 0)
					switchThread.setDirection(LightSwitcher.RotationDirection.Right);
				else
					switchThread.setDirection(LightSwitcher.RotationDirection.Left);
				
	//			switchThread.start();
				switchThread.startSweep();
				
				
				while(Kompaktor.LIGHT_SENSOR.readValue() < ThresholdLine){
					if(Thread.interrupted())
						throw new InterruptedException();
					Thread.sleep(Kompaktor.SLEEP_INTERVAL);
				}
				value = LightSwitcher.getRegulatedCurrentAngle();
				switchThread.interrupt();
				switchThread.join();
				//setzen des Winkels wo etwas entdeckt wurde, damit die verzögerung durch interrupt (Motor stoppen) korrigiert wird
				//wenn failure zu groß dann hat er durch einen Überschwinger etwas gefunden --> Überschwinger hinzuaddieren
//				if(failure < 2){
//					LightSwitcher.setAngle(value);
//				} else {
//					int overSwing = switchThread.getLastRotationDirection() == RotantionDirection.Right ? value - 10 : (value + 10);
//					LightSwitcher.setAngle(overSwing);
//				}
				
				double converted = value < 0 ? - value : value;
				boolean straight = converted < ThresholdAngleForward;
				if(converted > 50)
					converted = 50;
				converted = -converted + 90;
				converted = converted * converted;
				converted *= 90;
				converted /= 90 * 90;
				if(value < 0)
					converted *= -1;
				//System.out.println("value: " + value + "conv: " + converted);
				
//				if(ligthSensor.readValue() < 40)
//					failure++;
//				else
//					failure = 0;
				
				do {
//					Sound.beep();
					if(Thread.interrupted())
						throw new InterruptedException();
					if(straight){
						Kompaktor.DIFF_PILOT.travel(travelLengthLine, true);
					} else {
						//pilot.arcForward(-converted / 4.0);
						Kompaktor.DIFF_PILOT.travelArc(-converted / 2.5, travelLengthLine, true);
						//pilot.rotate((LightSwitcher.getRegulatedCurrentAngleDouble() < 0) ? -angleRotateBridge : angleRotateBridge);
					}
					Thread.yield();
				} while(Kompaktor.LIGHT_SENSOR.readValue() >= ThresholdLine);
			} 
		} catch (InterruptedException e){
			Kompaktor.DIFF_PILOT.stop();
			if(!switchThread.isInterrupted())
				switchThread.interrupt();
		}
	}

	@Override
	public void init() {
		Kompaktor.DIFF_PILOT.setTravelSpeed(travelSpeedLine);
	}

	@Override
	public boolean isDone() {
		return switchThread != null && switchThread.getSwitchCounter() >= numberSwitchFinish;
	}
	
}
