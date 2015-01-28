import java.lang.annotation.ElementType;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.Touch;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;
import lejos.util.TimerListener;

public class Labyrinth {

	private volatile static boolean impact = false, leftImpact = false,
			rightImpact = false, timerFinished = false;
	private static lejos.util.Timer timer = new lejos.util.Timer(0,
			new TimerListener() {

				@Override
				public void timedOut() {
					timerFinished = true;

				}
			});
	UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
	private static final int TIMER_DELAY = 2000;

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
		timer.setDelay(2000);

		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);

		// Detect when the robot hit a wall
		Thread collisionControl = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					if (sensorLeft.isPressed() && sensorRight.isPressed())
						impact = true;

					if (sensorLeft.isPressed())
						leftImpact = true;
					if (sensorRight.isPressed())
						rightImpact = true;

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
			if (impact) {
				pilot.stop();
				pilot.travel(-7);
				Sound.playTone(800, 1000);
				if (d > 20) {
					pilot.rotate(90);
				} else {
					pilot.rotate(90);
				}
				impact = false;
				leftImpact = false;
				rightImpact = false;
			} else if (leftImpact) {

				pilot.rotate(10);
				leftImpact = false;
			} else if (rightImpact) {

				pilot.stop();
				pilot.travel(-5);
				pilot.rotate(20);
				rightImpact = false;

			} else if (d > 40) {
				pilot.travel(5);
				pilot.rotate(-90);
				while (sonic.getDistance() > 20 && !impact())
					pilot.forward();
				pilot.travelArc(-50, 7);
			} else {
				drive(pilot, sonic);
			}
		}

	}

	private static void drive(DifferentialPilot pilot, UltrasonicSensor s) {

		int d = s.getDistance();
		if (d <= 20 && d > 10) { // If not too close or too
									// far to the // //
									// wall, move // forward
			pilot.forward();
		} else if (d < 30 && d >= 20) { // If a bit far move
										// // to the wall

			timer.setDelay(TIMER_DELAY);
			timer.start();
			while (!timerFinished && !impact() && d < 40) {
				pilot.steer(-5);
				d = s.getDistance();
			}

		} else if (d <= 10) {
			// If a bit too close, move away from the wall
			timer.setDelay(TIMER_DELAY);
			timer.start();
			while (!timerFinished && !impact() && d < 40) {
				pilot.steer(5);
				d = s.getDistance();
			}
		} else if (d <= 40 && d > 30) { // If too far move
										// toward the wall
			pilot.rotate(-40);
			timer.setDelay(TIMER_DELAY);
			timer.start();
			while (!timerFinished && !impact() && d < 40) {
				d = s.getDistance();
				pilot.steer(10);
			}
		}
		timerFinished = false;

	}

	private static boolean impact() {
		return impact || rightImpact || leftImpact;
	}

}
