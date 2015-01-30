package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


public class RopeBridgeRun {

	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
				
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		LineRunner before = new LineRunner(ligthSensor, pilot);
		before.start();
		
		//stoppen wenn zu oft keine Linie gefunden
		while(before.getSwitchCounter() < 4);
		
		before.interrupt();
		try {
			before.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//ein Stück vorfahren damit auf Brücke
		pilot.travel(28);
		
		BridgeRun bridge = new BridgeRun(ligthSensor, pilot);
		bridge.start();
		
		//Erkennen dass über 1000ms kein Holz erkannt wurde, dann ist Ende der Hängebrücke
		int value = 0;
		boolean find = false;
		long lastMillis = System.currentTimeMillis();
		while(!find){
			value = ligthSensor.readValue();
			if(value < 40 && value > 30){
				lastMillis = System.currentTimeMillis();
			}
			find = (System.currentTimeMillis() - lastMillis > 1000);
		}
		
		
//		System.out.println("Lighth detected: " + value);
		bridge.interrupt();
		try {
			bridge.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		
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
			valueWood = ligthSensor.readValue();
		}
		pilot.stop();
		//30 Grad zurück drehen
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
		while(ligthSensor.readValue() < 35);
		pilot.stop();
		//noch ein wenig nach vorn, damit von der Hängebrücke herunter
		pilot.travel(20);
		//Ausgang wiederherstellen
		pilot.setRotateSpeed(rotSpeedDefalut);
		
		LineRunner line = new LineRunner(ligthSensor, pilot);
		line.start();
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		line.interrupt();
		try {
			line.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

}
