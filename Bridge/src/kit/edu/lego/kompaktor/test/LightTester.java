package kit.edu.lego.kompaktor.test;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;


public class LightTester {

	public static void main(String[] args) {
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		
		//ligthSensor.setFloodlight(false);
		
		while(true){
			while(!touchright.isPressed() && !touchleft.isPressed());
			System.out.println("light: " + ligthSensor.readValue());
		}
		
		

	}

}
