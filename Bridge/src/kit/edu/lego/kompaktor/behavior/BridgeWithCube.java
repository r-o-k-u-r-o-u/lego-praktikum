package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.LightSensor;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class BridgeWithCube {

	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
		LightSensor lightsensor = ParcoursRunner.LIGHT_SENSOR;
		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT;
				
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		LightSwitcher.initAngles();
		
		BridgeRun bridge = new BridgeRun();
		bridge.init();
		bridge.start();
		
		while(lightsensor.readValue() < 40);
		
		BridgeRun.thresholdWood = 40;
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		try {
			bridge.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//end
		while(!touchright.isPressed() && !touchleft.isPressed());

	}

}
