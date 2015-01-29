package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
//import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


//TODO Rückwärtsfahren wenn Linie nicht erkannt? --> nur wenn Linie nicht einfach endet

public class LineRunner extends Thread{

	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
	final static int travelLengthLine = 3;
	final static int ThresholdAngleForward = 10;
	final static int ThresholdLine = 42;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		LineRunner line = new LineRunner(ligthSensor, pilot);
		line.start();
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		line.interrupt();
		try {
			line.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

	private LightSensor ligthSensor;
	private DifferentialPilot pilot;
	private LightSwitcher switchThread;
	private static int value = 0;
	
	public LineRunner(LightSensor ligthSensor, DifferentialPilot pilot) {
		this.ligthSensor = ligthSensor;
		this.pilot = pilot;
	}
	
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
			pilot.setTravelSpeed(travelSpeedLine);
			//int failure = 0; //zählt wie oft er stehen geblieben ist, aber die Linie nicht gesehen hat
			while(true){
				switchThread = new LightSwitcher();
				if(value >= 0 ) //^ (failure % 2 > 0)
					switchThread.setDirection(LightSwitcher.RotantionDirection.Right);
				else
					switchThread.setDirection(LightSwitcher.RotantionDirection.Left);
				
	//			switchThread.start();
				switchThread.startSweep();
				
				
				while(ligthSensor.readValue() < ThresholdLine){
					if(Thread.interrupted())
						throw new InterruptedException();
					Thread.yield();	
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
					//Sound.beep();
					if(Thread.interrupted())
						throw new InterruptedException();
					if(straight){
						pilot.travel(travelLengthLine, true);
					} else {
						//pilot.arcForward(-converted / 4.0);
						pilot.travelArc(-converted / 2.5, travelLengthLine, true);
						//pilot.rotate((LightSwitcher.getRegulatedCurrentAngleDouble() < 0) ? -angleRotateBridge : angleRotateBridge);
					}
					Thread.yield();
				} while(ligthSensor.readValue() >= ThresholdLine);
			} 
		} catch (InterruptedException e){
			pilot.stop();
			if(!switchThread.isInterrupted())
				switchThread.interrupt();
		}
	}
	
}
