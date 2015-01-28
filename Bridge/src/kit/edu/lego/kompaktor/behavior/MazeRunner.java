package kit.edu.lego.kompaktor.behavior;
import kit.edu.lego.kompaktor.model.LightSwitcher;
//import kit.edu.lego.kompaktor.model.LightSwitcher.RotantionDirection;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;


/**
 * Orientiert sich immer rechts
 * 
 * 
 * @author Christian
 *
 */

public class MazeRunner {

//	final static int angleRotateLine = 20;
	final static int travelSpeedLine = 20;
//	final static int travelLengthLine = 3;
//	final static int ThresholdAngleForward = 5;
	
	public static void main(String[] args) {
		//wait until it is pressed
		TouchSensor touchright = new TouchSensor(SensorPort.S3);
		TouchSensor touchleft = new TouchSensor(SensorPort.S2);
//		LightSensor ligthSensor = new LightSensor(SensorPort.S1, true);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		DifferentialPilot pilot = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
				
		// Stelle sicher dass der Lichtsensor nicht im Weg ist.
		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);
		
//		double value = 0;
		
		pilot.setTravelSpeed(travelSpeedLine);
		while(!touchright.isPressed() && !touchleft.isPressed())
		{
			// Starte Maze
			
			int dist = sonic.getDistance();
			
			if (dist <= 10) {
				if (dist < 5) {
					
				} 
				pilot.travelArc(30, 5);
			} else {
				if (dist > 30) {
					pilot.travelArc(-10, 5);
					pilot.travel(5);
				} else {
					pilot.travelArc(-30, 5);
				}
				
			}
		}
		
		while(!touchright.isPressed() && !touchleft.isPressed());
		System.out.println("interrupted");
		
		//end
		while(!touchright.isPressed() && !touchleft.isPressed());
	}

}
