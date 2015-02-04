package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class LabyrinthRunner extends ParcoursRunner {

	private volatile boolean impact = false, leftImpact = false,
			rightImpact = false, event = false;

	private volatile int d, pd;

	private final int STEER_POWER = 70, DISTANCE_TO_WALL = 8,
			OK_DEVIATION = 5, MAX_DISTANCE_TO_WALL = 35,
			TRAVEL_AFTER_LOSING_WALL = 6;

	private DifferentialPilot pilot; // volatile entfernt
	private TouchSensor sensorLeft, sensorRight;
	private Thread collisionControl = null;

	public LabyrinthRunner() {

	}
	/**
	 * Starts the labyrinth.
	 */
	public void run() {
		try {
			// Movement

			while (true) {
				if (Thread.interrupted())
					throw new InterruptedException();
				/*
				 * If wall is hit then go backward, if sonic distance (to the
				 * left) is small then the robot is close to the wall, rotate
				 * right by 90 degrees, else rotate left by 90 degrees.
				 */

				if (event) {
					pilot.stop();
					resolveCollision(30, 60);

				} else if (d > MAX_DISTANCE_TO_WALL) {

					Sound.beep();
					pilot.travel(TRAVEL_AFTER_LOSING_WALL);
					if (Thread.interrupted())
						throw new InterruptedException();
					if (d < MAX_DISTANCE_TO_WALL) {
						if (Thread.interrupted())
							throw new InterruptedException();
						continue;
					}
					
					
					pilot.rotate(-90);
					while (d > MAX_DISTANCE_TO_WALL) {
						if (Thread.interrupted())
							throw new InterruptedException();
						pilot.forward();
						if (impact()) {
							resolveCollision(30, 60);
						}

					}
					pilot.travel(20);
					if (d < DISTANCE_TO_WALL + 15) {
						if (Thread.interrupted())
							throw new InterruptedException();
						continue;
					}

					pilot.rotate(-90);

					while (d > DISTANCE_TO_WALL) {
						if (Thread.interrupted())
							throw new InterruptedException();
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
		} catch (InterruptedException e) {
			// System.out.println("interrupt start");
			collisionControl.interrupt();
			// System.out.println("dist inter");
			pilot.stop();
			try {
				collisionControl.join();
				// System.out.println("coll finish");
				// System.out.println("dist finish");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			pilot.stop();
		}
	}

	/**
	 * Method to solve a collision that happened.
	 * @param leftAngle The angle for turning when the left side is hit.
	 * @param rightAngle The angle for turning when the right side is hit.
	 * @throws InterruptedException
	 */
	private void resolveCollision(int leftAngle, int rightAngle)
			throws InterruptedException {

		while (!impact()) {
			if (Thread.interrupted())
				throw new InterruptedException();
		}

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
	
	/**
	 * Method for driving the robot along the wall.
	 * @param distanceToWall distance to be maintained to the wall
	 * @throws InterruptedException 
	 */
	synchronized void drive(int distanceToWall) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		if (d < distanceToWall + OK_DEVIATION && d > distanceToWall) { // If not
																		// too
																		// close
			// or too
			// far to the // //
			// wall, move // forward
			if (Thread.interrupted())
				throw new InterruptedException();

			double diff = d - pd;
			pilot.steer(-diff / (double) d * STEER_POWER);

		} else if (d <= MAX_DISTANCE_TO_WALL
				&& d >= distanceToWall + OK_DEVIATION) { // If
			// a
			// bit
			// far
			// move
			// // to the wall

			if (Thread.interrupted())
				throw new InterruptedException();
			double diff = d - pd;
			while (Math.abs(d - pd) > 0 && !impact()) {
				if (Thread.interrupted())
					throw new InterruptedException();
				pilot.steer(-diff / (double) d * STEER_POWER);
			}
			if (d - pd > 0) {
				while (Math.abs(d - pd) <= 3 && !impact()
						&& d >= distanceToWall + OK_DEVIATION) {
					if (Thread.interrupted())
						throw new InterruptedException();
					pilot.steer(-20);
					if (d > MAX_DISTANCE_TO_WALL) {
						return;
					}
				}
				while (Math.abs(d - pd) >= 0 && !impact()
						&& d <= MAX_DISTANCE_TO_WALL
						&& d >= distanceToWall + OK_DEVIATION) {
					if (Thread.interrupted())
						throw new InterruptedException();
					pilot.steer(20);
				}
			} else {
				while (d - pd >= 0 && !impact() && d <= MAX_DISTANCE_TO_WALL
						&& d >= distanceToWall + OK_DEVIATION) {
					if (Thread.interrupted())
						throw new InterruptedException();
					pilot.steer(-20);
				}

			}

		} else if (d <= distanceToWall) {

			if (Thread.interrupted())
				throw new InterruptedException();
			pilot.steer(10);
		}
		if (Thread.interrupted())
			throw new InterruptedException();

	}

	void drive(int wallDistance, int driveDistance) throws InterruptedException {

		while (pilot.getMovementIncrement() < driveDistance) {
			if (Thread.interrupted())
				throw new InterruptedException();
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

	/**
	 * Initialization has to be called before starting the Labyrinth.
	 */
	@Override
	public void init() {

		Kompaktor.parkArm();

		sensorLeft = Kompaktor.TOUCH_RIGHT;
		sensorRight = Kompaktor.TOUCH_LEFT;

		pilot = Kompaktor.DIFF_PILOT_REVERSE;

		// Detect when the robot hit a wall
		collisionControl = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					int i = 0;
					Kompaktor.SONIC_SENSOR.continuous();
					pd = Kompaktor.readDistanceValue();
					while (true) {
						if (Thread.interrupted())
							throw new InterruptedException();
						d = Kompaktor.readDistanceValue();

						if (i == 0)
							pd = d;
						i++;
						if (i == 25) {
							i = 0;
						}

						if (Thread.interrupted())
							throw new InterruptedException();
						if (sensorRight.isPressed()) {
							rightImpact = true;
							event = true;
							for (int j = 0; j < 25; j++) {
								if (Thread.interrupted())
									throw new InterruptedException();
								if (sensorLeft.isPressed()) {
									impact = true;
									rightImpact = false;
								}
							}
						} else if (sensorLeft.isPressed()) {
							leftImpact = true;
							event = true;
							for (int j = 0; j < 25; j++) {
								if (Thread.interrupted())
									throw new InterruptedException();
								if (sensorRight.isPressed()) {
									impact = true;
									leftImpact = false;
								}
							}
						}
					}

				} catch (InterruptedException e) {

				}
			}

		});

		collisionControl.start();

	}

	@Override
	public boolean isDone() {
		return false;
	}

	/**
	 * nur verwenden wenn start() nicht aufgerufen wurde
	 */
	public void stopHelpThreads() {
		// System.out.println("interrupt start");
		collisionControl.interrupt();
		// System.out.println("coll inter");
		pilot.stop();
		try {
			collisionControl.join();
			// System.out.println("dist finish");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		pilot.stop();
	}

}
