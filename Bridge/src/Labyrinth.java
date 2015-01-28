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

	private volatile static boolean impact = false;
	private volatile static boolean impactRepeat = false;
	private volatile static boolean closeToWall = false;
	private static volatile Stopwatch sw;

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
				pilot.travel(-10);
				wait(pilot);
				Sound.playTone(800, 1000);
				if (d > 20) {
					pilot.rotate(-80);
					wait(pilot);
				} else {
					pilot.rotate(80);
					wait(pilot);
				}
				impact = false;

			} else if (d < 15 && d > 10) { // If not too close or too far to the
											// // wall, move // forward
				pilot.forward();
			} else if (d > 15 && d < 20) { // If a bit far move to the wall
				if (!pilot.isMoving())
					pilot.forward();
				pilot.steer(-10);
			} else if (d < 10) {
				if (!pilot.isMoving())
					pilot.forward();
				pilot.steer(10); // If a bit too close, move away from the wall
			} else if (d > 50){
				if (!pilot.isMoving())
					pilot.forward();
				pilot.rotate(-80);
				wait(pilot);
				pilot.travel(10);
				wait(pilot);
			} else {
				
			}
		}

	}

	

	private static void wait(DifferentialPilot pilot) {

		while (pilot.isMoving())
			;

	}

	/*
	 * private void runBehaviour(Behaviour b) { b.run(); }
	 * 
	 * private abstract class Behaviour {
	 * 
	 * protected DifferentialPilot pilot; protected UltrasonicSensor us;
	 * protected TouchSensor l, r;
	 * 
	 * protected Behaviour(DifferentialPilot p, UltrasonicSensor us, TouchSensor
	 * l, TouchSensor r) { pilot = p; this.us = us; this.l = l; this.r = r; }
	 * 
	 * abstract void run();
	 * 
	 * }
	 * 
	 * private class CloseToWall extends Behaviour {
	 * 
	 * protected CloseToWall(DifferentialPilot p, UltrasonicSensor us,
	 * TouchSensor l, TouchSensor r) { super(p, us, l, r);
	 * 
	 * }
	 * 
	 * @Override public void run() {
	 * 
	 * while(impact != true) { int d = us.getDistance(); if(d < 30 && d > 10) {
	 * pilot.forward(); } else if ( d < 10) { pilot.steer(4); } else if (d > 30
	 * && d < 50) { pilot.steer(-4); } else { pilot.rotate(40);
	 * pilot.travel(20); while(pilot.isMoving()){}
	 * 
	 * 
	 * 
	 * } } }
	 * 
	 * }
	 * 
	 * private class Impact extends Behaviour {
	 * 
	 * protected Impact(DifferentialPilot p, UltrasonicSensor us, TouchSensor l,
	 * TouchSensor r) { super(p, us, l, r);
	 * 
	 * }
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * }
	 */

}
