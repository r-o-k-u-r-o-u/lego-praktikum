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
		while(!Kompaktor.isTouched());
		
		Kompaktor.startLevel(LEVEL_NAMES.LINE_FOLLOW);
		
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
				
				switchThread.startSweep();
				
				while(Kompaktor.LIGHT_SENSOR.readValue() < ThresholdLine){
					if(Thread.interrupted())
						throw new InterruptedException();
					Thread.sleep(Kompaktor.SLEEP_INTERVAL);
				}
				value = LightSwitcher.getRegulatedCurrentAngle();
				switchThread.interrupt();
				switchThread.join();
				
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
				
				do {
					if(Thread.interrupted())
						throw new InterruptedException();
					if(straight){
						Kompaktor.DIFF_PILOT.travel(travelLengthLine, true);
					} else {
						Kompaktor.DIFF_PILOT.travelArc(-converted / 2.25, travelLengthLine, true);
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
