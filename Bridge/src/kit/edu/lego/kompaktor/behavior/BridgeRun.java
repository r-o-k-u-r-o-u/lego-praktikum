package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.TouchSensor;


public class BridgeRun extends ParcoursRunner {
	
	final static int angleRotateBridge = 20;
	final static int travelSpeedBridge = 10;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor lightSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT;
				
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		BridgeRun bridge = new BridgeRun();
		bridge.init();
		bridge.start();
		while(!touchright.isPressed() && !touchleft.isPressed());
		try {
			bridge.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}
	
	private LightSwitcher switchThread;
	private LightSwitcher.RotantionDirection lastHole;
	
	public LightSwitcher.RotantionDirection getLastHole(){
		return lastHole;
	}
	
	public boolean isLightSwitcherActive(){
		return switchThread.isAlive();
	}
	
	public void run(){
		try{
			while(true){
				switchThread = new LightSwitcher();
				switchThread.start();
				pilot.stop();
				pilot.forward();
				while(lightSensor.readValue() > 30){
					if(Thread.interrupted())
						throw new InterruptedException();
					Thread.yield();	
				}
				switchThread.interrupt();
				pilot.stop();
				switchThread.join();
				
				
				while(lightSensor.readValue() <= 30) {
					if(Thread.interrupted())
						throw new InterruptedException();
					double value = LightSwitcher.getRegulatedCurrentAngleDouble();
					if(value < 0)
						lastHole = RotantionDirection.Left;
					else
						lastHole = RotantionDirection.Right;

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

	@Override
	public void init() {
		pilot.setTravelSpeed(travelSpeedBridge);
	}

	@Override
	public boolean isDone() {
		// TODO wie soll das Ende der Brücke erkannt werden?
		return false;
	}

}


