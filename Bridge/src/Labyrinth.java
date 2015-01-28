import java.util.Timer;

import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.Touch;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;
import lejos.util.Stopwatch;

public class Labyrinth {

	private volatile static boolean impact = false, leftImpact = false,
			rightImpact = false;
	private volatile static int usedAngle = 0;
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
				PilotProps.KEY_TRACKWIDTH, "17.0"));
		DifferentialPilot pilot = new DifferentialPilot(wheelDiameter,
				trackWidth, leftMotor, rightMotor, reverse);

		sonic.continuous();

		// Detect when the robot hit a wall
		Thread collisionControl = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					if (sensorLeft.isPressed() && sensorRight.isPressed()) {
						impact = true;
					} else if (sensorLeft.isPressed()) {
						leftImpact = true;
					} else if (sensorRight.isPressed()) {
						rightImpact = true;
					}
				}
			}
		});

		collisionControl.start();
		// Movement
		while (true) {

			int d = sonic.getDistance();

			/*
			 * If wall is hit then go backward, if sonic distance (to the left)
			 * is small then the robot is close to the wall, rotate right by 90
			 * degrees, else rotate left by 90 degrees.
			 */
			if (impact == true) {
				pilot.stop();
				pilot.travel(-7);
				wait(pilot);
				Sound.playTone(800, 1000);
				if (d > 20) {
					pilot.rotate(-90);
					wait(pilot);
				} else {
					pilot.rotate(90);
					wait(pilot);
				}
				impact = false;
			} else if (leftImpact) {

				pilot.stop();
				pilot.travel(-5);
				wait(pilot);
				pilot.rotate(-20);
				wait(pilot);
				leftImpact = false;
			} else if (rightImpact) {

				pilot.stop();
				pilot.travel(-5);
				wait(pilot);
				pilot.rotate(20);
				wait(pilot);
				rightImpact = false;

			} else if (d > 40) {
				pilot.travel(10);
				wait(pilot);
				pilot.rotate(-85);
				wait(pilot);
				pilot.travel(20);
				wait(pilot);
			} else {
				drive(pilot, sonic);
			}
		}

	}

	private static void drive(DifferentialPilot pilot, UltrasonicSensor s) {

		int d = s.getDistance();
		if (d < 15 && d > 10) { // If not too close or too far to the // //
								// wall, move // forward
			pilot.forward();
		} else if (d > 15 && d < 20) { // If a bit far move to the wall
			if (!pilot.isMoving())
				pilot.forward();
			pilot.steer(-20);
		} else if (d < 10) {
			if (!pilot.isMoving())
				pilot.forward();
			pilot.steer(10); // If a bit too close, move away from the wall
		} else {
			if (!pilot.isMoving())
				pilot.forward();
			pilot.steer(-20);
		}

	}

	private static void wait(DifferentialPilot pilot) {

		while (pilot.isMoving())
			;

	}

}
