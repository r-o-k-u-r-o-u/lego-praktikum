package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import kit.edu.lego.kompaktor.threading.ParcoursRunner;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


public class RopeBridgeRun extends ParcoursRunner{

	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
				
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		RopeBridgeRun ropeBridge = new RopeBridgeRun(ligthSensor, pilot);
		ropeBridge.init();
		ropeBridge.start();
		while(!ropeBridge.isDone());
		ropeBridge.stop();
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

	private LightSensor ligthSensor;
	private DifferentialPilot pilot;
	private LineRunner before;
	private BridgeRun bridge;
	private LineRunner line;
	
	public RopeBridgeRun(LightSensor ligthSensor, DifferentialPilot pilot) {
		this.ligthSensor = ligthSensor;
		this.pilot = pilot;
	}
	
	@Override
	public void run() {
		try{
			
			before = new LineRunner(ligthSensor, pilot);
			before.start();
			
			//stoppen wenn zu oft keine Linie gefunden
			while(before.getSwitchCounter() < 4){
				if(Thread.interrupted())
					throw new InterruptedException();
			}
			
			before.interrupt();
			before.join();
			
			//ein St�ck vorfahren damit auf Br�cke
			pilot.travel(28);
			
			bridge = new BridgeRun(ligthSensor, pilot);
			bridge.start();
			
			//Erkennen dass �ber 1000ms kein Holz erkannt wurde, dann ist Ende der H�ngebr�cke
			int value = 0;
			boolean find = false;
			long lastMillis = System.currentTimeMillis();
			while(!find){
				if(Thread.interrupted())
					throw new InterruptedException();
				value = ligthSensor.readValue();
				if(value < 40 && value > 30){
					lastMillis = System.currentTimeMillis();
				}
				find = (System.currentTimeMillis() - lastMillis > 1000);
			}
			
			
	//		System.out.println("Lighth detected: " + value);
			bridge.interrupt();
			bridge.join();

	
			
			//drehen damit an linke seite schaut
			//pilot.rotate(bridge.getLastHole() == RotantionDirection.Left ? 50 : -50);
			LightSwitcher.setAngle(0);
			double rotSpeedDefalut = pilot.getRotateSpeed();
			pilot.setRotateSpeed(30);
			if(bridge.getLastHole() == RotantionDirection.Left)
				pilot.rotateRight();
			else 
				pilot.rotateLeft();
			//bis Holz erkennt
			int valueWood = ligthSensor.readValue();
			
			double speed = pilot.getRotateSpeed();
			int timeFor70Deg_ms = (int) (70 * 1000 / speed);
			
			long begin = System.currentTimeMillis();
			while(valueWood < 30 && (System.currentTimeMillis() - begin) < timeFor70Deg_ms){
				if(Thread.interrupted())
					throw new InterruptedException();
				valueWood = ligthSensor.readValue();
			}
			pilot.stop();
			//30 Grad zur�ck drehen
			pilot.setRotateSpeed(rotSpeedDefalut);
			if(bridge.getLastHole() == RotantionDirection.Left)
				pilot.rotate(30);
			else
				pilot.rotate(-30);
			//suchen nach der linie
			pilot.setRotateSpeed(15);
			if(bridge.getLastHole() == RotantionDirection.Left)
				pilot.rotateLeft();
			else
				pilot.rotateRight();
			while(ligthSensor.readValue() < 35){
				if(Thread.interrupted())
					throw new InterruptedException();
			}
			pilot.stop();
			//noch ein wenig nach vorn, damit von der H�ngebr�cke herunter
			pilot.travel(20);
			//Ausgang wiederherstellen
			pilot.setRotateSpeed(rotSpeedDefalut);
			
			line = new LineRunner(ligthSensor, pilot);
			line.start();
			
		} catch (InterruptedException e){
			if(before != null && before.isAlive())
				before.interrupt();
			if(bridge != null && bridge.isAlive())
				bridge.interrupt();
			if(line != null && line.isAlive())
				line.interrupt();
			pilot.stop();
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
		
	}

	@Override
	public boolean isDone() {
		return line != null && line.getSwitchCounter() > 3;
	}

}
