import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.Touch;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class Labyrinth{

	private volatile static boolean impact = false;
	private static final short[] note = { 2349, 115, 0, 5, 1760, 165, 0, 35 };

	UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);

	public static void main(String[] args) {

		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		PilotProps pp = new PilotProps();
		Touch sensorLeft = new TouchSensor(SensorPort.S2);
		Touch sensorRight = new TouchSensor(SensorPort.S3);

		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(
				PilotProps.KEY_LEFTMOTOR, "C"));
		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(
				PilotProps.KEY_RIGHTMOTOR, "B"));
		boolean reverse = Boolean.parseBoolean(pp.getProperty(
				PilotProps.KEY_REVERSE, "false"));

		float wheelDiameter = Float.parseFloat(pp.getProperty(
				PilotProps.KEY_WHEELDIAMETER, "3.0"));
		float trackWidth = Float.parseFloat(pp.getProperty(
				PilotProps.KEY_TRACKWIDTH, "13.0"));
		DifferentialPilot pilot = new DifferentialPilot(wheelDiameter,
				trackWidth, leftMotor, rightMotor, reverse);

		sonic.continuous();

		// Detect when the robot hit a wall
		Thread collisionControl = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					if (sensorLeft.isPressed() || sensorRight.isPressed()) {
						impact = true;
						Sound.playTone(512, 200);
					}
				}
			}
		});

		collisionControl.start();

		// Movement
		while (sonic.getDistance() < 500) {

			int d = sonic.getDistance();

			/*
			 * If wall is hit then go backward, if sonic distance (to the left)
			 * is small then the robot is close to the wall, rotate right by 90
			 * degrees, else rotate left by 90 degrees.
			 */
			if (impact == true) {
				pilot.quickStop();
				pilot.backward();
				if (d > 100) {
					pilot.rotate(-90);
				} else {
					pilot.rotate(90);
				}

				impact = false;
			} else if (d < 30 && d > 10) { // If not too close to the wall, move
											// forward
				pilot.forward();
			} else if (d > 40 && d < 60) { // If a bit far move to the wall
				pilot.steer(-2);
			} else if (d < 10) {
				pilot.steer(2); // If a bit too close, move away from the wall
			}

		}

	}

}
