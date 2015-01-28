package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


public class BridgeRun extends Thread{
	
	final static int angleRotateBridge = 20;
	final static int travelSpeedBridge = 10;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
				
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		BridgeRun bridge = new BridgeRun(ligthSensor, pilot);
		bridge.start();
		while(!touchright.isPressed() && !touchleft.isPressed());
		bridge.interrupt();
		try {
			bridge.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}
	
	private LightSensor ligthSensor;
	private DifferentialPilot pilot;
	private LightSwitcher switchThread;
	
	public boolean isLightSwitcherActive(){
		return switchThread.isAlive();
	}
	
	public BridgeRun(LightSensor ligthSensor, DifferentialPilot pilot){
		this.ligthSensor = ligthSensor;
		this.pilot = pilot;
	}
	
	public void run(){
		try{
			pilot.setTravelSpeed(travelSpeedBridge);
			while(true){
				switchThread = new LightSwitcher();
				switchThread.start();
				pilot.stop();
				pilot.forward();
				while(ligthSensor.readValue() > 30){
					if(Thread.interrupted())
						throw new InterruptedException();
					Thread.yield();	
				}
				switchThread.interrupt();
				pilot.stop();
				switchThread.join();
				
				
				while(ligthSensor.readValue() <= 30) {
					if(Thread.interrupted())
						throw new InterruptedException();
					double value = LightSwitcher.getRegulatedCurrentAngleDouble();
					double converted = value < 0 ? - value : value;
					if(converted > 89.5)
						converted = 89.5;
					converted = -converted + 90;
					if(value < 0)
						converted *= -1;
					//System.out.println("value: " + value + "conv: " + converted);
					pilot.arcForward(converted / 10.0);
					//pilot.rotate((LightSwitcher.getRegulatedCurrentAngleDouble() < 0) ? -angleRotateBridge : angleRotateBridge);
				}
			}
		} catch (InterruptedException e){
			pilot.stop();
			if(!switchThread.isInterrupted())
				switchThread.interrupt();
		}
	}

}


