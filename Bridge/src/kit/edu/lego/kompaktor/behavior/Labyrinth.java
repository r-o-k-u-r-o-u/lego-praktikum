package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class Labyrinth extends ParcoursRunner {

	private volatile boolean impact = false, leftImpact = false,
			rightImpact = false, event = false, done = false;

	private volatile int d, pd;

	private final int STEER_POWER = 65;

	private volatile DifferentialPilot pilot;
	private TouchSensor sensorLeft, sensorRight;
	private Object lock;

	public static void main(String[] args) {

		Labyrinth l = new Labyrinth();
		l.init();
		l.run();

	}

	public Labyrinth() {

		PilotProps pp = new PilotProps();
		sensorLeft = new TouchSensor(SensorPort.S3);
		sensorRight = new TouchSensor(SensorPort.S2);

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
		pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor,
				rightMotor, reverse);

		lock = new Object();

		// Detect when the robot hit a wall
		Thread collisionControl = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {

					if (sensorRight.isPressed()) {
						rightImpact = true;
						event = true;
						for (int i = 0; i < 50; i++) {
							if (sensorLeft.isPressed()) {
								impact = true;
								rightImpact = false;
							}
						}
					} else if (sensorLeft.isPressed()) {
						leftImpact = true;
						event = true;
						for (int i = 0; i < 50; i++) {
							if (sensorRight.isPressed()) {
								impact = true;
								leftImpact = false;
							}
						}
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
					if (i == 25) {
						i = 0;
					}
				}

			}
		});
		collisionControl.start();
		distanceMeasure.start();

	}

	public synchronized void run() {

		// Movement

		while (true) {

			/*
			 * If wall is hit then go backward, if sonic distance (to the left)
			 * is small then the robot is close to the wall, rotate right by 90
			 * degrees, else rotate left by 90 degrees.
			 */

			if (event) {
				pilot.stop();
				resolveCollision(30, 50);

			} else if (d > 35) {

				Sound.beep();
				pilot.travel(5);
				pilot.rotate(-90);
				while (d > 40) {
					pilot.forward();
					if (impact()) {
						resolveCollision(30, 50);
					}
				}
				pilot.travel(20);
				pilot.rotate(-90);

				while (d > 10) {
					if (Math.abs(d - pd) < 6) {
						pilot.steer(-15);
					}

					if (impact()) {
						resolveCollision(20, 40);
					}

				}
			} else {
				drive();
			}
		}
	}

	private synchronized void resolveCollision(int leftAngle, int rightAngle) {

		while (!impact())
			;

		if (impact) {
			pilot.stop();
			pilot.travel(-7);
			Sound.playTone(800, 100);
			pilot.rotate(90);
		} else if (leftImpact) {

			pilot.travel(-5);
			pilot.rotate(leftAngle);

		} else if (rightImpact) {

			pilot.stop();
			pilot.travel(-5);
			pilot.rotate(rightAngle);

		}

		impact = false;
		rightImpact = false;
		leftImpact = false;
		event = false;
	}

	private synchronized void drive() {

		if (d < 12 && d > 8) { // If not too close or too
								// far to the // //
								// wall, move // forward

			double diff = d - pd;
			pilot.steer(-diff / (double) d * STEER_POWER);

		} else if (d <= 35 && d >= 12) { // If a bit far move
											// // to the wall

			double diff = d - pd;
			while (Math.abs(d - pd) > 0 && !impact()) {
				pilot.steer(-diff / (double) d * STEER_POWER);
			}
			if (d - pd > 0) {
				while (Math.abs(d - pd) <= 3 && !impact() && d <= 35 && d >= 12) {
					pilot.steer(-20);
					if (d > 35) {
						return;
					}
				}
				while (Math.abs(d - pd) >= 0 && !impact() && d <= 35 && d >= 12) {
					pilot.steer(20);
				}
			} else {
				while (d - pd >= 0 && !impact() && d <= 35 && d >= 12) {
					pilot.steer(-20);
				}

			}

		} else if (d <= 8) {

			pilot.steer(4);
		}

	}

	private boolean impact() {
		return impact || rightImpact || leftImpact;
	}

	@Override
	public void init() {

		LightSwitcher.initAngles();
		LightSwitcher.setAngle(-90);

	}

	@Override
	public boolean isDone() {
		synchronized (this) {
			return done;
		}
	}

}