import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


public class BridgeRun {
	
	final static int angleRotateBridge = 20;
	final static int travelSpeedBridge = 10;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		DifferentialPilot pilot = new DifferentialPilot(3, 13, Motor.C, Motor.B, true);
				
		//Thread zum switchen
		LightSwitcher switchThread = new LightSwitcher();
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		pilot.setTravelSpeed(travelSpeedBridge);
		while(!touchright.isPressed() && !touchleft.isPressed()){
			switchThread = new LightSwitcher();
			switchThread.start();
			pilot.stop();
			pilot.forward();
			while(ligthSensor.readValue() > 30){
				Thread.yield();	
			}
			switchThread.interrupt();
			pilot.stop();
			
			while(ligthSensor.readValue() <= 30) {
				double value = LightSwitcher.getRegulatedCurrentAngleDouble();
				double converted = value < 0 ? - value : value;
				if(converted > 89.5)
					converted = 89.5;
				converted = -converted + 90;
				if(value < 0)
					converted *= -1;
				System.out.println("value: " + value + "conv: " + converted);
				pilot.arcForward(converted / 10.0);
				//pilot.rotate((LightSwitcher.getRegulatedCurrentAngleDouble() < 0) ? -angleRotateBridge : angleRotateBridge);
			}
			//pilot.forward();
			//pilot.stop();
		}
		
		
//		switchThread.start();
//		lightRecognition.start();
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		switchThread.interrupt();
//		lightRecognition.interrupt();
		System.out.println("interrupted");
		try {
			switchThread.join();
//			lightRecognition.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("join is finish");

		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}

}


