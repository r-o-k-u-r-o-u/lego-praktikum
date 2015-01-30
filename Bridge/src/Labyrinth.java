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
			rightImpact = false, timerFinished = false, event = false;
	private static lejos.util.Timer timer = new lejos.util.Timer(0,
			new TimerListener() {

				@Override
				public void timedOut() {
					timerFinished = true;

				}
			});
	private volatile static int d, pd;

	private static final int TIMER_DELAY = 200, STEER_POWER = 50;

	public static void main(String[] args) {

		PilotProps pp = new PilotProps();
		Touch sensorLeft = new TouchSensor(SensorPort.S3);
		Touch sensorRight = new TouchSensor(SensorPort.S2);

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

		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);

		// Detect when the robot hit a wall
		Thread collisionControl = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					if (sensorLeft.isPressed() && sensorRight.isPressed()) {
						impact = true;
						event = true;
					}

					if (sensorLeft.isPressed() && !impact) {
						event = true;
						for (int i = 0; i < 200; i++) {
							if (sensorRight.isPressed()) {
								impact = true;
							}
						}
						if (!impact)
							leftImpact = true;
					}
					if (sensorRight.isPressed() && !impact) {
						event = true;
						for (int i = 0; i < 200; i++) {
							if (sensorLeft.isPressed()) {
								impact = true;
							}
						}
						if (!impact)
							rightImpact = true;
					}

				}
			}
		});

		Thread distanceMeasure = new Thread(new Runnable() {

			@Override
			public void run() {

				UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
				int i = 0;
				sonic.continuous();
				pd = sonic.getDistance();
				while (true) {

					d = sonic.getDistance();

					if (i == 0)
						pd = d;
					i++;
					if (i == 75) {
						i = 0;
					}
				}

			}
		});

		collisionControl.start();
		distanceMeasure.start();
		d = 100;

		while (d > 40) {

			pilot.forward();

		}

		// Movement
		while (true) {

			/*
			 * If wall is hit then go backward, if sonic distance (to the left)
			 * is small then the robot is close to the wall, rotate right by 90
			 * degrees, else rotate left by 90 degrees.
			 */
			if (event) {
				pilot.stop();
				while (!impact())
					;
				event = false;
			}

			if (impact) {
				pilot.stop();
				pilot.travel(-7);
				Sound.beep();
				pilot.rotate(90);
				impact = false;
				rightImpact = false;
				leftImpact = false;
				event = false;
			} else if (leftImpact) {

				pilot.travel(-4);
				pilot.rotate(30);
				Sound.beep();
				impact = false;
				rightImpact = false;
				leftImpact = false;
				event = false;

			} else if (rightImpact) {

				pilot.stop();
				pilot.travel(-5);
				Sound.beep();
				pilot.rotate(50);
				impact = false;
				rightImpact = false;
				leftImpact = false;
				event = false;

			} else if (d > 35) {

				Sound.playTone(800, 2000);
				pilot.travel(7);
				pilot.rotate(-90);
				double angle = 30;
				while (d > 35) {
					pilot.forward();
					if (impact()) {
						pilot.travel(-7);
						pilot.rotate(angle);
						leftImpact = false;
						impact = false;
						rightImpact = false;
						event = false;
					}
				}
				pilot.travel(25);
				pilot.rotate(-90);

				while (d > 10 && !impact() || Math.abs(d - pd) > 4) {
					pilot.steer(-25);

				}
				leftImpact = false;
				impact = false;
				rightImpact = false;
				event = false;

			} else {
				drive(pilot);
			}
		}

	}

	private static void drive(DifferentialPilot pilot) {

		if (d < 12 && d > 8) { // If not too close or too
								// far to the // //
								// wall, move // forward

			double diff = d - pd;
			pilot.steer(-diff / (double) d * STEER_POWER);

		} else if (d <= 30 && d >= 12) { // If a bit far move
											// // to the wall

			/*
			 * double diff = d - pd; if (diff > 2 || diff < -2) {
			 * pilot.steer(-diff / (double) d * STEER_POWER); } else {
			 * pilot.steer(-5, 10, false); while (!impact() && d - pd != diff) {
			 * pilot.steer(5, 10); } }
			 */

			pilot.steer(-10, 20, false);
			double diff = d - pd;
			pilot.steer(-diff / (double) d * STEER_POWER);

		} else if (d <= 8) {
			pilot.steer(4);
		} else if (d <= 35 && d > 30) { // If too far move
										// toward the wall
			pilot.steer(-8);

		}
		timerFinished = false;

	}

	private static boolean impact() {
		return impact || rightImpact || leftImpact;
	}

}
