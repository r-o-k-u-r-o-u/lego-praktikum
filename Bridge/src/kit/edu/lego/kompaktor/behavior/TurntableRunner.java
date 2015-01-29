package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
//import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
//import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


//TODO R�ckw�rtsfahren wenn Linie nicht erkannt? --> nur wenn Linie nicht einfach endet

public class TurntableRunner extends Thread{

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
		
		while (line.getSwitchCounter() < 3);
		
		line.interrupt();
		
		try {
			line.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		pilot.travel(10);
		pilot.rotate(-180);
		pilot.backward();
		LightSwitcher.setAngle(-90);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		
		// when on turntable
		
		Sound.beep();
		pilot.travel(15);
		LineRunner line2 = new LineRunner(ligthSensor, pilot);
		line2.start();
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

	private LightSensor ligthSensor;
	private DifferentialPilot pilot;
	private LightSwitcher switchThread;
	private static int value = 0;
	
	public TurntableRunner(LightSensor ligthSensor, DifferentialPilot pilot) {
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
//		try{
//			
//		
//		} catch (InterruptedException e){
//			pilot.stop();
//			if(!switchThread.isInterrupted())
//				switchThread.interrupt();
//		}
	}
	
}
