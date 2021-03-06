package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.LightSwitcher.RotationDirection;

public class BridgeRun extends ParcoursRunner {
	
	final static int angleRotateBridge = 20;
	final static int travelSpeedBridge = 10;
	static int thresholdWood = 30;
	
	public static void main(String[] args) {
		
		//wait until it is pressed
		while(!Kompaktor.isTouched());
		
		ParcoursRunner bridge = Kompaktor.startLevel(LEVEL_NAMES.BRIDGE, true);
		
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
	private boolean discoLight = false;
	private boolean endless = true;
	private boolean isDone = false;
	private long lastCorrectionTime;
	
	public long getLastCorrectionTime() {
		return lastCorrectionTime;
	}

	public boolean isEndless() {
		return endless;
	}

	public void setEndless(boolean endless) {
		this.endless = endless;
	}

	public boolean isDiscoLight() {
		return discoLight;
	}

	public void setDiscoLight(boolean discoLight) {
		this.discoLight = discoLight;
	}

	public LightSwitcher.RotationDirection getLastHole(){
		return lastHole;
	}
	
	public boolean isLightSwitcherActive(){
		return switchThread.isAlive();
	}
	
	public void run(){
		try{
			boolean first = true;
			while(first || endless){
				first = false;
				switchThread = new LightSwitcher();
				switchThread.start();
				Kompaktor.DIFF_PILOT.stop();
				Kompaktor.DIFF_PILOT.forward();
				//while(Kompaktor.LIGHT_SENSOR.readValue() > thresholdWood){
//				while(Kompaktor.readLightDifferenceArr()[1] > thresholdWood){
				while(discoLight && Kompaktor.readLightDifferenceArr()[1] > thresholdWood ||
						!discoLight && Kompaktor.LIGHT_SENSOR.readValue() > thresholdWood){
					if(Thread.interrupted())
						throw new InterruptedException();
					Thread.yield();	
				}
				switchThread.interrupt();
				Kompaktor.DIFF_PILOT.stop();
				switchThread.join();
				
				
				//while(Kompaktor.LIGHT_SENSOR.readValue() <= thresholdWood) {
//				while(Kompaktor.readLightDifferenceArr()[1] <= thresholdWood) {
				while(discoLight && Kompaktor.readLightDifferenceArr()[1] <= thresholdWood ||
						!discoLight && Kompaktor.LIGHT_SENSOR.readValue() <= thresholdWood) {
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
					Thread.yield();
				}
				//neue Zeit
				lastCorrectionTime = System.currentTimeMillis();
			}
			
			isDone = true;
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e){
			Kompaktor.DIFF_PILOT.stop();
			if(!switchThread.isInterrupted())
				switchThread.interrupt();
		}
	}

	@Override
	public void init() {
		Kompaktor.DIFF_PILOT.setTravelSpeed(travelSpeedBridge);
		lastCorrectionTime = System.currentTimeMillis();
	}

	@Override
	public boolean isDone() {
		// Ende ist nur im nicht endless-Modus
		return isDone;
	}

}


