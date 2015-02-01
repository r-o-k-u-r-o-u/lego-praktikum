package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.behavior.ParcoursRunner.LEVEL_NAMES;
import kit.edu.lego.kompaktor.model.Kompaktor;

public class BridgeWithCube {

	public static void main(String[] args) {
		//wait until it is pressed
//		TouchSensor touchright = ParcoursRunner.TOUCH_RIGHT;
//		TouchSensor touchleft = ParcoursRunner.TOUCH_LEFT;
//		LightSensor lightsensor = ParcoursRunner.LIGHT_SENSOR;
//		DifferentialPilot pilot = ParcoursRunner.DIFF_PILOT;
				
		while(!Kompaktor.isTouched());
//		LightSwitcher.initAngles();
		
		ParcoursRunner bridge = Kompaktor.startLevel(LEVEL_NAMES.BRIDGE, true);
		
		while(Kompaktor.LIGHT_SENSOR.readValue() < 40);
		
		BridgeRun.thresholdWood = 40;
		
		while(!Kompaktor.isTouched());
		
		try {
			bridge.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//end
		while(!Kompaktor.isTouched());

	}

}
