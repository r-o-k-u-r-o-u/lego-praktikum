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
		DifferentialPilot pilot = new DifferentialPilot(3, 13, Motor.C, Motor.B, true);
				
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		BridgeRun bridge = new BridgeRun(ligthSensor, pilot);
		bridge.start();
		int value;
		boolean find = false;
		while((value = ligthSensor.readValue()) < 55);
		System.out.println("Lighth detected: " + value);
		bridge.interrupt();
		try {
			bridge.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

}
