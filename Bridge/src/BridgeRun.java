import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;


public class BridgeRun {

	final static int rotationAngle = 200;
	final static int rotationSpeed = 500;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		while(!touchright.isPressed() && !touchleft.isPressed());
		
		//init Light-Sensor rotation
		System.out.println(Motor.A.getPosition());
		Motor.A.setSpeed(100);
		//Motor.A.setStallThreshold(5, 200);
		Motor.A.rotate(-rotationAngle);
		Motor.A.stop();
		//Motor.A.resetTachoCount();
		System.out.println(Motor.A.getPosition());
		
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		
		
		
		//Thread zum switchen
		Thread switchThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Motor.A.setSpeed(rotationSpeed);
					while(true){
						Motor.A.rotate(rotationAngle, true);
						Thread.sleep(rotationAngle * 1000 / rotationSpeed);
						Motor.A.rotate(-rotationAngle, true);
						Thread.sleep(rotationAngle * 1000 / rotationSpeed);
					}
				} catch (InterruptedException e) {
					Motor.A.stop();
				}
				
			}
		});
		
		
		// Thread zum Piepen
		Thread lightRecognition = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while(true){
						if(ligthSensor.readValue() >= 40){
							Sound.beep();
							System.out.println(Motor.A.getPosition());
						}
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					
				}
				
			}
		});
		
		
		
		
		switchThread.start();
		lightRecognition.start();
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		switchThread.interrupt();
		lightRecognition.interrupt();
		System.out.println("interrupted");
		try {
			switchThread.join();
			lightRecognition.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("join is finish");

		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}

}
