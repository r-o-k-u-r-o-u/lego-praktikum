import kit.edu.lego.kompaktor.model.LightSwitcher;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.Touch;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class Labyrinth {

	private volatile boolean impact = false, leftImpact = false,
			rightImpact = false, event = false;

	private volatile int d, pd;

	private final int STEER_POWER = 65;

	public void main(String[] args) {

		Labyrinth l = new Labyrinth();
		l.run();

	}

	public synchronized void run() {

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
					synchronized (sensorLeft) {
						if (sensorLeft.isPressed() && sensorRight.isPressed()) {
							impact = true;
							event = true;
						}
					}

					if (sensorLeft.isPressed() && !impact) {
						event = true;
						for (int i = 0; i < 50; i++) {
							if (sensorRight.isPressed()) {
								impact = true;
							}
						}
						if (!impact)
							leftImpact = true;
					}
					if (sensorRight.isPressed() && !impact) {
						event = true;
						for (int i = 0; i < 50; i++) {
							if (sensorLeft.isPressed()) {
								impact = true;
							}
						}
						Sound.beep();
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
					if (i == 25) {
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
				Sound.playTone(800, 100);
				pilot.rotate(90);
				impact = false;
				rightImpact = false;
				leftImpact = false;
				event = false;
			} else if (leftImpact) {

				pilot.travel(-4);
				pilot.rotate(30);
				impact = false;
				rightImpact = false;
				leftImpact = false;
				event = false;

			} else if (rightImpact) {

				pilot.stop();
				pilot.travel(-5);
				pilot.rotate(50);
				impact = false;
				rightImpact = false;
				leftImpact = false;
				event = false;

			} else if (d > 35) {

				Sound.beep();
				pilot.travel(5);
				pilot.rotate(-90);
				double angle = 30;
				int impacts = 0;
				while (d > 40) {
					pilot.forward();
					if (impact()) {
						pilot.travel(-7);
						if (impacts > 0)
							angle *= 1.1;
						pilot.rotate(angle);
						leftImpact = false;
						impact = false;
						rightImpact = false;
						event = false;
					}
				}
				pilot.travel(15);
				pilot.rotate(-90);

				while (d > 10) {
					if (Math.abs(d - pd) < 6) {
						pilot.steer(-20);
					}

					if (impact()) {
						pilot.travel(-5);
						pilot.rotate(20);
						leftImpact = false;
						impact = false;
						rightImpact = false;
						event = false;
					}

				}
			} else {
				drive(pilot);
			}
		}

	}

	private synchronized void drive(DifferentialPilot pilot) {

		if (d < 12 && d > 8) { // If not too close or too
								// far to the // //
								// wall, move // forward

			double diff = d - pd;
			pilot.steer(-diff / (double) d * STEER_POWER);

		} else if (d <= 35 && d >= 12) { // If a bit far move
											// // to the wall

			double diff = d - pd;
			while (Math.abs(d - pd) > 2 && !impact()) {
				pilot.steer(-diff / (double) d * STEER_POWER);
			}
			if (d - pd > 0) {
				while (Math.abs(d - pd) <= 4 && !impact() && d <= 35 && d >= 12) {
					pilot.steer(-30);
				}
				if (d > 35) {
					return;
				}
				while (Math.abs(d - pd) >= 0 && !impact() && d <= 35 && d >= 12) {
					pilot.steer(30);
				}
			} else {
				while (d - pd >= 0 && !impact() && d <= 35 && d >= 12) {
					pilot.steer(-30);
				}

			}

		} else if (d <= 8) {

			pilot.steer(4);
		}

	}

	private boolean impact() {
		return impact || rightImpact || leftImpact;
	}

}
