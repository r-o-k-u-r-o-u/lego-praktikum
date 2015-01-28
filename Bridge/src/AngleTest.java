import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;


public class AngleTest {

	public static void main(String[] args) {
		
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		LightSwitcher test = new LightSwitcher();
		test.startSweep();
		while(!touchright.isPressed() && !touchleft.isPressed());
		test.interrupt();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int angle = -40;
		
		while(!touchright.isPressed() && !touchleft.isPressed()){
			LightSwitcher.setAngle(angle);
			System.out.println("ist: " + LightSwitcher.getRegulatedCurrentAngle() + " soll: " + angle);
			while(!touchright.isPressed() && !touchleft.isPressed());
			angle+=10;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
