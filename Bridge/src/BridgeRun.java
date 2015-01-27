import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;


public class BridgeRun {
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		
		DifferentialPilot pilot = new DifferentialPilot(3, 13, Motor.C, Motor.B, true);
				
		//Thread zum switchen
		LightSwitcher switchThread = new LightSwitcher();
		
//		// Thread zum Piepen
//		Thread lightRecognition = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					while(true){
//						if(ligthSensor.readValue() < 30){
//							Sound.beep();
//							
//						}
//						Thread.sleep(10);
//					}
//				} catch (InterruptedException e) {
//					
//				}
//				
//			}
//		});
		
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		
//		while(!touchright.isPressed() && !touchleft.isPressed());
//		switchThread = new LightSwitcher();
//		switchThread.init();
//		switchThread.start();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		while(!touchright.isPressed() && !touchleft.isPressed());
//		switchThread.interrupt();
//		try {
//			switchThread.join();
//		} catch (InterruptedException e2) {
//			e2.printStackTrace();
//		}
//		switchThread = new LightSwitcher();
//		switchThread.init();
//		switchThread.start();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		while(!touchright.isPressed() && !touchleft.isPressed());
//		switchThread.interrupt();
//		try {
//			switchThread.join();
//		} catch (InterruptedException e2) {
//			e2.printStackTrace();
//		}
//		switchThread = new LightSwitcher();
//		switchThread.init();
//		switchThread.start();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
		
		
		int counter = 0;
		while(!touchright.isPressed() && !touchleft.isPressed()){
			System.out.println("counter: " + counter);
			switchThread = new LightSwitcher();
			switchThread.startWithInit();
			pilot.forward();
			while(ligthSensor.readValue() > 30){
				Thread.yield();	
			}
			switchThread.interrupt();
			pilot.stop();
			
			while(ligthSensor.readValue() <= 30) {
				pilot.rotate((Motor.A.getPosition() < 90) ? -10 : 10);
			}
			counter++;
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


