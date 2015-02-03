package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.Kompaktor;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.Motor;

public class RopeBridgeRun extends ParcoursRunner{

	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor lightSensor = new LightSensor(SensorPort.S1, true);
//		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
				
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
			}
			
			before.stop();
			
			//ein Stück vorfahren damit auf Brücke
			Kompaktor.DIFF_PILOT.travel(28);
			
			boolean endBridge = false;
			while(!endBridge){
			
				bridge = new BridgeRun();
				bridge.setEndless(false);
				bridge.init();
				bridge.start();
				
				
				while(!bridge.isDone()){
					Thread.yield();
				}
				//bridge.stop();
				
				//Arm ganz an Seite
				
				Kompaktor.DIFF_PILOT.stop();
				
				float rotSpeed = Motor.A.getSpeed();
				LightSwitcher.setAngle(-90);
				Motor.A.setSpeed(100);
				Motor.A.forward();
				int positionLeftBlack, positionLeftWhite;
				while(Kompaktor.LIGHT_SENSOR.readValue() > 30 && LightSwitcher.getRegulatedCurrentAngle() < 85);
				positionLeftBlack = LightSwitcher.getRegulatedCurrentAngle();
				while(Kompaktor.LIGHT_SENSOR.readValue() <= 30 && LightSwitcher.getRegulatedCurrentAngle() < 85);
				positionLeftWhite = LightSwitcher.getRegulatedCurrentAngle();
				Motor.A.stop();
				Motor.A.setSpeed(rotSpeed);
				LightSwitcher.setAngle(90);
				Motor.A.setSpeed(100);
				Motor.A.backward();
				int positionRigthBlack, positionRigthWhite;
				while(Kompaktor.LIGHT_SENSOR.readValue() > 30 && LightSwitcher.getRegulatedCurrentAngle() > -85);
				positionRigthBlack = LightSwitcher.getRegulatedCurrentAngle();
				while(Kompaktor.LIGHT_SENSOR.readValue() <= 30 && LightSwitcher.getRegulatedCurrentAngle() > -85);
				positionRigthWhite = LightSwitcher.getRegulatedCurrentAngle();
				Motor.A.stop();
				
				Motor.A.setSpeed(rotSpeed);
				
				int diffLeft = Math.abs(positionLeftBlack - positionLeftWhite);
				int diffRight = Math.abs(positionRigthBlack - positionRigthWhite);
				if(diffLeft > 20 && diffRight > 20){
					endBridge = true;
				}
			}
			
//			//Erkennen dass über 1000ms kein Holz erkannt wurde, dann ist Ende der Hängebrücke
//			int value = 0;
//			boolean find = false;
//			long lastMillis = System.currentTimeMillis();
//			while(!find){
//				if(Thread.interrupted())
//					throw new InterruptedException();
//				value = Kompaktor.LIGHT_SENSOR.readValue();
//				if(value < 40 && value > 30){
//					lastMillis = System.currentTimeMillis();
//				}
//				find = (System.currentTimeMillis() - lastMillis > 1000);
//				//Thread.sleep(10);
//			}
//			
//			
//	//		System.out.println("Lighth detected: " + value);
//			bridge.stop();

	
			
			//drehen damit an linke seite schaut
			//pilot.rotate(bridge.getLastHole() == RotantionDirection.Left ? 50 : -50);
//			LightSwitcher.setAngle(0);
			Kompaktor.stretchArm();
			
			double rotSpeedDefalut = Kompaktor.DIFF_PILOT.getRotateSpeed();
			
			Kompaktor.DIFF_PILOT.setRotateSpeed(30);
			if(bridge.getLastHole() == RotantionDirection.Left)
				Kompaktor.DIFF_PILOT.rotateRight();
			else 
				Kompaktor.DIFF_PILOT.rotateLeft();
			//Licht mus an sein
			Kompaktor.setFloodlight(true);
			//bis Holz erkennt
			int valueWood = Kompaktor.LIGHT_SENSOR.readValue();
			
			double speed = Kompaktor.DIFF_PILOT.getRotateSpeed();
			int timeFor70Deg_ms = (int) (70 * 1000 / speed);
			
			long begin = System.currentTimeMillis();
			while(valueWood < 30 && (System.currentTimeMillis() - begin) < timeFor70Deg_ms){
				if(Thread.interrupted())
					throw new InterruptedException();
				valueWood = Kompaktor.LIGHT_SENSOR.readValue();
			}
			Kompaktor.DIFF_PILOT.stop();
			//30 Grad zurück drehen
			Kompaktor.DIFF_PILOT.setRotateSpeed(rotSpeedDefalut);
			if(bridge.getLastHole() == RotantionDirection.Left)
				Kompaktor.DIFF_PILOT.rotate(30);
			else
				Kompaktor.DIFF_PILOT.rotate(-30);
			//suchen nach der linie
			Kompaktor.DIFF_PILOT.setRotateSpeed(15);
			if(bridge.getLastHole() == RotantionDirection.Left)
				Kompaktor.DIFF_PILOT.rotateLeft();
			else
				Kompaktor.DIFF_PILOT.rotateRight();
			while(Kompaktor.LIGHT_SENSOR.readValue() < 35){
				if(Thread.interrupted())
					throw new InterruptedException();
			}
			Kompaktor.DIFF_PILOT.stop();
			//noch ein wenig nach vorn, damit von der Hängebrücke herunter
			Kompaktor.DIFF_PILOT.travel(20);
			//Ausgang wiederherstellen
			Kompaktor.DIFF_PILOT.setRotateSpeed(rotSpeedDefalut);
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
