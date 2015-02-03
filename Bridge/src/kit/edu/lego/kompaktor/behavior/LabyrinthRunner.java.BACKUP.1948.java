package kit.edu.lego.kompaktor.behavior;

import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class LabyrinthRunner extends ParcoursRunner {

	private volatile boolean impact = false, leftImpact = false,
			rightImpact = false, event = false;

	private volatile int d, pd;

	private final int STEER_POWER = 70, DISTANCE_TO_WALL = 10,
			OK_DEVIATION = 5, MAX_DISTANCE_TO_WALL = 35,
			TRAVEL_AFTER_LOSING_WALL = 7;

	private DifferentialPilot pilot; //volatile entfernt
	private TouchSensor sensorleft, sensorRight;
	private Thread distanceMeasure = null;
	private Thread collisionControl = null;
	

	public static void main(String[] args) {

		LabyrinthRunner l = new LabyrinthRunner();
		l.init();
		l.start();
<<<<<<< HEAD

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

					if (sensorRight.isPressed()) {
						rightImpact = true;
						event = true;
						for (int j = 0; j < 50; j++) {
							if (sensorLeft.isPressed()) {
								impact = true;
								rightImpact = false;
							}
						}
					} else if (sensorLeft.isPressed()) {
						leftImpact = true;
						event = true;
						for (int j = 0; j < 50; j++) {
							if (sensorRight.isPressed()) {
								impact = true;
								leftImpact = false;
							}
						}
					}
				}

			}
		});
		// collisionControl.start();
		distanceMeasure.start();
=======
		
		try {
			Thread.sleep(10000);
			System.out.println("would interrupt");
			l.stop();
			System.out.println("is interrupted");
//			l.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(Kompaktor.isNotTouched());
>>>>>>> 2b77403b52ced8d9d426a913d06db75378ef4d5e

	}

	public void run() {
		try{
			// Movement
	
			while (true) {
				if(Thread.interrupted())
					throw new InterruptedException();
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
					if(Thread.interrupted())
						throw new InterruptedException();
					if (d < MAX_DISTANCE_TO_WALL){
						if(Thread.interrupted())
							throw new InterruptedException();
						continue;
					}
<<<<<<< HEAD
				}
				pilot.travel(20);
				if (d < MAX_DISTANCE_TO_WALL)
					continue;

				pilot.rotate(-90);

				while (d > DISTANCE_TO_WALL) {
					double diff = d - pd;
					if (Math.abs(diff) < 4) {
						pilot.steer(-20);
=======
					pilot.rotate(-90);
					while (d > MAX_DISTANCE_TO_WALL) {
						if(Thread.interrupted())
							throw new InterruptedException();
						pilot.forward();
						if (impact()) {
							resolveCollision(30, 60);
						}
>>>>>>> 2b77403b52ced8d9d426a913d06db75378ef4d5e
					}
					pilot.travel(20);
					if (d < MAX_DISTANCE_TO_WALL){
						if(Thread.interrupted())
							throw new InterruptedException();
						continue;
					}
						
					pilot.rotate(-90);
	
					while (d > DISTANCE_TO_WALL) {
						if(Thread.interrupted())
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
			//System.out.println("interrupt start");
			collisionControl.interrupt();
			//System.out.println("coll inter");
			distanceMeasure.interrupt();
			//System.out.println("dist inter");
			pilot.stop();
			try {
				collisionControl.join();
				//System.out.println("coll finish");
				distanceMeasure.join();
				//System.out.println("dist finish");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			pilot.stop();
		}
	}

	private void resolveCollision(int leftAngle, int rightAngle) throws InterruptedException {

<<<<<<< HEAD
		while (!impact())
			;
=======
		while (!impact()){
			if(Thread.interrupted())
				throw new InterruptedException();
		}
>>>>>>> 2b77403b52ced8d9d426a913d06db75378ef4d5e

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

	synchronized void drive(int distanceToWall) throws InterruptedException {
		if(Thread.interrupted())
			throw new InterruptedException();
		if (d < distanceToWall + OK_DEVIATION && d > distanceToWall) { // If not
																		// too
																		// close
			// or too
			// far to the // //
			// wall, move // forward
			if(Thread.interrupted())
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

			if(Thread.interrupted())
				throw new InterruptedException();
			double diff = d - pd;
			while (Math.abs(d - pd) > 0 && !impact()) {
				if(Thread.interrupted())
					throw new InterruptedException();
				pilot.steer(-diff / (double) d * STEER_POWER);
			}
			if (d - pd > 0) {
				while (Math.abs(d - pd) <= 3 && !impact()
						&& d >= distanceToWall + OK_DEVIATION) {
					if(Thread.interrupted())
						throw new InterruptedException();
					pilot.steer(-20);
					if (d > MAX_DISTANCE_TO_WALL) {
						return;
					}
				}
				while (Math.abs(d - pd) >= 0 && !impact()
						&& d <= MAX_DISTANCE_TO_WALL
						&& d >= distanceToWall + OK_DEVIATION) {
					if(Thread.interrupted())
						throw new InterruptedException();
					pilot.steer(20);
				}
			} else {
				while (d - pd >= 0 && !impact() && d <= MAX_DISTANCE_TO_WALL
						&& d >= distanceToWall + OK_DEVIATION) {
					if(Thread.interrupted())
						throw new InterruptedException();
					pilot.steer(-20);
				}

			}

		} else if (d <= distanceToWall) {

			if(Thread.interrupted())
				throw new InterruptedException();
			pilot.steer(10);
		}
		if(Thread.interrupted())
			throw new InterruptedException();

	}

	void drive(int wallDistance, int driveDistance) throws InterruptedException {

		while (pilot.getMovementIncrement() < driveDistance) {
			if(Thread.interrupted())
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

	@Override
	public void init() {

		Kompaktor.parkArm();
//		PilotProps pp = new PilotProps();
		sensorleft = Kompaktor.TOUCH_RIGHT;// new TouchSensor(SensorPort.S3);
		sensorRight = Kompaktor.TOUCH_LEFT;//new TouchSensor(SensorPort.S2);

//		RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(
//				PilotProps.KEY_LEFTMOTOR, "C"));
//		RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(
//				PilotProps.KEY_RIGHTMOTOR, "B"));
//		boolean reverse = Boolean.parseBoolean(pp.getProperty(
//				PilotProps.KEY_REVERSE, "false"));
//
//		float wheelDiameter = Float.parseFloat(pp.getProperty(
//				PilotProps.KEY_WHEELDIAMETER, "3.0"));
//		float trackWidth = Float.parseFloat(pp.getProperty(
//				PilotProps.KEY_TRACKWIDTH, "17.0"));
//		pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor,
//				rightMotor, reverse);
		pilot = Kompaktor.DIFF_PILOT_REVERSE;

		// Detect when the robot hit a wall
		collisionControl = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					while (true) {
						if(Thread.interrupted())
							throw new InterruptedException();
						if (sensorRight.isPressed()) {
							rightImpact = true;
							event = true;
							for (int i = 0; i < 50; i++) {
								if(Thread.interrupted())
									throw new InterruptedException();
								if (sensorleft.isPressed()) {
									impact = true;
									rightImpact = false;
								}
							}
						} else if (sensorleft.isPressed()) {
							leftImpact = true;
							event = true;
							for (int i = 0; i < 50; i++) {
								if(Thread.interrupted())
									throw new InterruptedException();
								if (sensorRight.isPressed()) {
									impact = true;
									leftImpact = false;
								}
							}
						}
	
					}
				} catch (InterruptedException e){
					
				}
			}

		});

		distanceMeasure = new Thread(new Runnable() {

			@Override
			public void run() {
				try{
					int i = 0;
					Kompaktor.SONIC_SENSOR.continuous();
					pd = Kompaktor.readDistanceValue();
					while (true) {
						if(Thread.interrupted())
							throw new InterruptedException();
						d = Kompaktor.readDistanceValue();
	
						if (i == 0)
							pd = d;
						i++;
						if (i == 25) {
							i = 0;
						}
					}
				} catch (InterruptedException e) { }
			}
		});
		collisionControl.start();
		distanceMeasure.start();

	}

	@Override
	public boolean isDone() {
		return false;
	}

//	@Override
//	public void stop() throws InterruptedException{
////		int counter = 0;
//		while(this.isAlive()){
//			//System.out.println("inter " + counter++);
//			this.interrupt();
//			this.join(100);
//		}
//	}
	
	/**
	 * nur verwenden wenn start() nicht aufgerufen wurde
	 */
	public void stopHelpThreads(){
		//System.out.println("interrupt start");
		collisionControl.interrupt();
		//System.out.println("coll inter");
		distanceMeasure.interrupt();
		//System.out.println("dist inter");
		pilot.stop();
		try {
			collisionControl.join();
			//System.out.println("coll finish");
			distanceMeasure.join();
			//System.out.println("dist finish");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		pilot.stop();
	}
	
}