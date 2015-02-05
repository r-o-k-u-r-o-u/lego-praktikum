package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.Motor;

public class RopeBridgeRun extends ParcoursRunner{

	final static int searchSpeed = 60;
	
	public static void main(String[] args) {
		
		//wait until it is pressed
		while(!Kompaktor.isTouched());
		LightSwitcher.initAngles();
		
		RopeBridgeRun ropeBridge = new RopeBridgeRun();
		ropeBridge.init();
		ropeBridge.start();
		while(!ropeBridge.isDone());
		try {
			ropeBridge.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!Kompaktor.isTouched());

	}

	private LineRunner before = null;
	private BridgeRun bridge = null;
	private LineRunner line = null;
	
	@Override
	public void run() {
		try{
			
			before = new LineRunner();
			before.init();
			before.start();
			double travelSpeedDefalut = Kompaktor.DIFF_PILOT.getTravelSpeed();
			//stoppen wenn zu oft keine Linie gefunden
			while(!before.isDone()){
				if(Thread.interrupted())
					throw new InterruptedException();
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			}
			before.interrupt();
			before.join();
			
			//before.stop();
			
			//ausrichten
			//messen des Winkels
			int left = -90, rigth = 90;
			Kompaktor.stretchArm();
			int oldSpeed = Motor.A.getSpeed();
			Motor.A.setSpeed(searchSpeed);
			Motor.A.forward();
			
			while(Kompaktor.LIGHT_SENSOR.readValue() > 28 & (rigth = LightSwitcher.getRegulatedCurrentAngle()) < 90)
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			
			Motor.A.setSpeed(oldSpeed);
			Kompaktor.stretchArm();
			Motor.A.setSpeed(searchSpeed);
			Motor.A.backward();
			
			while(Kompaktor.LIGHT_SENSOR.readValue() > 28 & (left = LightSwitcher.getRegulatedCurrentAngle()) < 90)
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			
			Motor.A.stop();
			Motor.A.setSpeed(oldSpeed);
			int diff = rigth + left;
			//anpassen dass genau 90°
			Kompaktor.DIFF_PILOT.rotate(-diff/2.0 + 5);
			
			Kompaktor.DIFF_PILOT.travel(130);
			
//			
			Kompaktor.DIFF_PILOT.setTravelSpeed(travelSpeedDefalut);
			
			line = new LineRunner();
			line.start();
			line.join();
			
		} catch (InterruptedException e){
			if(before != null && before.isAlive())
				before.interrupt();
			if(bridge != null && bridge.isAlive())
				bridge.interrupt();
			if(line != null && line.isAlive())
				line.interrupt();
			Kompaktor.DIFF_PILOT.stop();
			try{
				if(before != null)
					before.join();
				if(bridge != null)
					bridge.join();
				if(line != null)
					line.join();
			} catch (InterruptedException e1){	}
		}
		
	}

	@Override
	public void init() {
		//nothing to do
	}

	@Override
	public boolean isDone() {
		return line != null && line.isDone();
	}

}
