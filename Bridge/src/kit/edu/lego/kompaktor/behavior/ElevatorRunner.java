package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.LightSwitcher.RotationDirection;
import lejos.nxt.Sound;


public class ElevatorRunner extends ParcoursRunner {
	
	final static int angleRotateBridge = 20;
	final static int travelSpeedBridge = 10;
	static int thresholdWood = 30;
	
	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor lightSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT;
				
		while(!Kompaktor.isTouched());
//		LightSwitcher.initAngles();
		
		ParcoursRunner bridge = Kompaktor.startLevel(LEVEL_NAMES.ELEVATOR, true);
		
		while(!Kompaktor.isTouched());
		
		try {
			bridge.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//end
		while(!Kompaktor.isTouched());
	}
	
	private LightSwitcher switchThread;
	private LightSwitcher.RotationDirection lastHole;
	
	public LightSwitcher.RotationDirection getLastHole(){
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
				Kompaktor.DIFF_PILOT.stop();
				Kompaktor.DIFF_PILOT.forward();
				while(Kompaktor.LIGHT_SENSOR.readValue() > thresholdWood){
					if(Thread.interrupted())
						throw new InterruptedException();
					Thread.sleep(Kompaktor.SLEEP_INTERVAL_SHORT);
				}
				switchThread.interrupt();
				Kompaktor.DIFF_PILOT.stop();
				switchThread.join();
				
				boolean ledFound = false;
				
				while(Kompaktor.LIGHT_SENSOR.readValue() <= thresholdWood) {
					if(Thread.interrupted())
						throw new InterruptedException();
					double value = LightSwitcher.getRegulatedCurrentAngleDouble();
					if(value < 0)
						lastHole = RotationDirection.Left;
					else
						lastHole = RotationDirection.Right;

					double converted = value < 0 ? - value : value;
					if(converted > 89.5)
						converted = 89.5;
					converted = -converted + 90;
					if(value < 0)
						converted *= -1;
					//System.out.println("value: " + value + "conv: " + converted);
					Kompaktor.DIFF_PILOT.arcForward(converted / 10.0);
					//pilot.rotate((LightSwitcher.getRegulatedCurrentAngleDouble() < 0) ? -angleRotateBridge : angleRotateBridge);
					
					if (Kompaktor.LIGHT_SENSOR.readValue() >= 40) {
						Sound.beep();
						ledFound = true;
						break;
					}
					
					if (ledFound && Kompaktor.LIGHT_SENSOR.readValue() >= thresholdWood && Kompaktor.LIGHT_SENSOR.readValue() < 40) {
						break;
					}
				}

				while (true) {
					Sound.beep();
				}
				
			}
		} catch (InterruptedException e){
			Kompaktor.DIFF_PILOT.stop();
			if(!switchThread.isInterrupted())
				switchThread.interrupt();
		}
	}

	@Override
	public void init() {
		Kompaktor.DIFF_PILOT.setTravelSpeed(travelSpeedBridge);
	}

	@Override
	public boolean isDone() {
		// TODO wie soll das Ende der Br�cke erkannt werden?
		return false;
	}

}


