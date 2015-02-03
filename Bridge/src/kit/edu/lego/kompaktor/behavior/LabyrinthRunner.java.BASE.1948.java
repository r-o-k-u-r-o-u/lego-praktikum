package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class LabyrinthRunner extends ParcoursRunner {

	private volatile boolean impact = false, leftImpact = false,
			rightImpact = false, event = false;

	private volatile int d, pd;

	private final int STEER_POWER = 70, DISTANCE_TO_WALL = 10, OK_DEVIATION = 5,
			MAX_DISTANCE_TO_WALL = 35, TRAVEL_AFTER_LOSING_WALL = 5;

	private volatile DifferentialPilot pilot;
	private TouchSensor sensorLeft, sensorRight;

	public static void main(String[] args) {

		LabyrinthRunner l = new LabyrinthRunner();
		l.init();
		l.start();

	}

	public LabyrinthRunner() {

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

	public void run() {

		// Movement

		while (true) {

			/*
			 * If wall is hit then go backward, if sonic distance (to the left)
			 * is small then the robot is close to the wall, rotate right by 90
			 * degrees, else rotate left by 90 degrees.
			 */

			if (event) {
				pilot.stop();
				resolveCollision(30, 60);

			} else if (d > MAX_DISTANCE_TO_WALL) {

				Sound.beep();
				pilot.travel(TRAVEL_AFTER_LOSING_WALL);
				if (d < MAX_DISTANCE_TO_WALL)
					continue;
				pilot.rotate(-90);
				while (d > MAX_DISTANCE_TO_WALL) {
					pilot.forward();
					if (impact()) {
						resolveCollision(30, 60);
					}
				}
				pilot.travel(20);
				if (d < MAX_DISTANCE_TO_WALL)
					continue;
					
				pilot.rotate(-90);

				while (d > DISTANCE_TO_WALL) {
					double diff = d - pd;
					if (Math.abs(diff) < 4) {
						pilot.steer(-20);
					}
					if (impact()) {
						resolveCollision(30, 40);
					}

				}
			} else {
				drive(DISTANCE_TO_WALL);
			}
		}
	}

	private void resolveCollision(int leftAngle, int rightAngle) {

		while (!impact());

		if (impact) {
			pilot.stop();
			pilot.travel(-3);
			Sound.playTone(800, 100);
			pilot.rotate(90);
		} else if (leftImpact) {

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

	synchronized void drive(int distanceToWall) {

		if (d < distanceToWall + OK_DEVIATION && d > distanceToWall) { // If not
																		// too
																		// close
			// or too
			// far to the // //
			// wall, move // forward

			double diff = d - pd;
			pilot.steer(-diff / (double) d * STEER_POWER);

		} else if (d <= MAX_DISTANCE_TO_WALL
				&& d >= distanceToWall + OK_DEVIATION) { // If
			// a
			// bit
			// far
			// move
			// // to the wall

			double diff = d - pd;
			while (Math.abs(d - pd) > 0 && !impact()) {
				pilot.steer(-diff / (double) d * STEER_POWER);
			}
			if (d - pd > 0) {
				while (Math.abs(d - pd) <= 3 && !impact()
						&& d >= distanceToWall + OK_DEVIATION) {
					pilot.steer(-20);
					if (d > MAX_DISTANCE_TO_WALL) {
						return;
					}
				}
				while (Math.abs(d - pd) >= 0 && !impact()
						&& d <= MAX_DISTANCE_TO_WALL
						&& d >= distanceToWall + OK_DEVIATION) {
					pilot.steer(20);
				}
			} else {
				while (d - pd >= 0 && !impact() && d <= MAX_DISTANCE_TO_WALL
						&& d >= distanceToWall + OK_DEVIATION) {
					pilot.steer(-20);
				}

			}

		} else if (d <= distanceToWall) {

			pilot.steer(10);
		}

	}

	void drive(int wallDistance, int driveDistance) {

		while (pilot.getMovementIncrement() < driveDistance) {
			drive(wallDistance);
		}
		pilot.stop();

	}

	void straight(int distance) {

		pilot.travel(distance);

	}

	void rotate(int angle) {
		pilot.rotate(angle);
	}

	private boolean impact() {
		return impact || rightImpact || leftImpact;
	}

	@Override
	public void init() {

		Kompaktor.parkArm();

	}

	@Override
	public boolean isDone() {
		return false;
	}

}
