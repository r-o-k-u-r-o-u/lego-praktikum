package kit.edu.lego.kompaktor.model;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/***
 * der Thread als LightSwitcher.
 * !!! am Anfang muss initAngles ausgeführt werden
 * 
 * bei interrupt() wird der Motor grstoppt, danach muss ein neuer Thread erstellt werden
 * 
 * @author Florian
 *
 */
public class LightSwitcher extends Thread {
	
	final static int rotationAngle = 200;
	final static int rotationSpeedFull = 400;
	final static int rotationSpeedMiddle = 200;
	final static int rotationSpeedSmall = 200;
	final static int rotationSpeedInit = 100;
	final static int angleMiddle = 100;
	final static int angleLeftFull = 0;
	final static int angleRigthFull = 200;
	final static int angleLeftMiddle = angleMiddle - 45;
	final static int angleLeftSmall = angleMiddle - 20;
	final static int angleRigthMiddle = angleMiddle + 45;
	final static int angleRigthSmall = angleMiddle + 20;
	final static NXTRegulatedMotor motor = Motor.A;
	
	public enum RotationRange {
		SMALL, MIDDLE, FULL
	}
	
	public enum RotationDirection {
		Right, Left
	}
	
	private RotationRange rotationRange;
	private RotationDirection rotationDirection;
	private RotationDirection lastRotationDirection;
	private int switchCounter;
	
	public LightSwitcher() {
		rotationRange = RotationRange.FULL;
		rotationDirection = RotationDirection.Left;
		switchCounter = 0;
	}
	
	public int getSwitchCounter() {
		return switchCounter;
	}
	
	public void startSweep(){
		rotationRange = RotationRange.MIDDLE;
		super.start();
	}
	
	public void setDirection(RotationDirection rotationDirection){
		this.rotationDirection = rotationDirection;
	}
	
	public RotationDirection getLastRotationDirection() {
		return lastRotationDirection;
	}

	public void run() {
		try {
			while(true){
				int actualAngleLeft;
				int actualAngleRigth;
				int rotationSpeed;
				switch (rotationRange) {
				case SMALL:
					actualAngleLeft = angleLeftSmall;
					actualAngleRigth = angleRigthSmall;
					rotationSpeed = rotationSpeedSmall;
					rotationRange = RotationRange.MIDDLE;
					break;
				case MIDDLE:
					actualAngleLeft = angleLeftMiddle;
					actualAngleRigth = angleRigthMiddle;
					rotationSpeed = rotationSpeedMiddle;
					rotationRange = RotationRange.FULL;
					break;
				default:
					actualAngleLeft = angleLeftFull;
					actualAngleRigth = angleRigthFull;
					rotationSpeed = rotationSpeedFull;
					break;
				}
				motor.setSpeed(rotationSpeed);
				if(rotationDirection == RotationDirection.Right){
					lastRotationDirection = RotationDirection.Right;
					motor.rotateTo(angleMiddle, true);
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 4));
	//				Thread.sleep(angleMiddle * 1000 / (rotationSpeed * 2));
					motor.rotateTo(actualAngleRigth, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 2) + 100);
					lastRotationDirection = RotationDirection.Left;
					motor.rotateTo(angleMiddle, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 2));
					motor.rotateTo(actualAngleLeft, true);
	//				Thread.sleep((actualAngleLeft - angleMiddle) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 2) + 100);
				} else {
					lastRotationDirection = RotationDirection.Left;
					motor.rotateTo(angleMiddle, true);
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 4));
	//				Thread.sleep(angleMiddle * 1000 / (rotationSpeed * 2));
					motor.rotateTo(actualAngleLeft, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 2) + 100);
					lastRotationDirection = RotationDirection.Right;
					motor.rotateTo(angleMiddle, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 2));
					motor.rotateTo(actualAngleRigth, true);
	//				Thread.sleep((actualAngleLeft - angleMiddle) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeedFull * 2) + 100);
				}
				//set the switchCounter up
				switchCounter++;
			}
		} catch (InterruptedException e) {
			motor.stop();
		}
	}
	
	public void init(){
		motor.setSpeed(rotationSpeedFull);
		motor.rotateTo(90);
		motor.rotateTo(0);
	}
	
	@Deprecated
	/**
	 * use start() only instead
	 */
	public void startWithInit(){
		this.init();
		super.start();
	}
	
	public static void initAngles(){
		motor.setSpeed(rotationSpeedInit);
		motor.rotate(-rotationAngle);
		motor.stop();
		motor.resetTachoCount();
	}
	
	public static int getRegulatedCurrentAngle(){
		int angle = motor.getPosition();
		if(angle < angleLeftFull)
			angle = angleLeftFull;
		if (angle > angleRigthFull)
			angle = angleRigthFull;
		return (angle - angleMiddle) * 90 / angleMiddle;
	}
	
	public static double getRegulatedCurrentAngleDouble(){
		int angle = motor.getPosition();
		if(angle < angleLeftFull)
			angle = angleLeftFull;
		if (angle > angleRigthFull)
			angle = angleRigthFull;
		return (angle - angleMiddle) * 90.0 / angleMiddle;
	}
	
	/**
	 * only use when thread is not running
	 * @param angle angle from -90 to +90
	 */
	public static void setAngle(double angle){
		motor.rotateTo((int)((angle * angleMiddle / 90.0) + angleMiddle));
	}
}
