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
public class LightSwitcher extends Thread{
	
	final static int rotationAngle = 200;
	final static int rotationSpeed = 500;
	final static int rotationSpeedInit = 100;
	final static int angleMiddle = 100;
	final static int angleLeftFull = 0;
	final static int angleRigthFull = 200;
	final static int angleLeftMiddle = angleMiddle - 45;
	final static int angleLeftSmall = angleMiddle - 20;
	final static int angleRigthMiddle = angleMiddle + 45;
	final static int angleRigthSmall = angleMiddle + 20;
	final static NXTRegulatedMotor motor = Motor.A;
	
	public enum RotantionRange{
		SMALL, MIDDLE, FULL
	}
	
	public enum RotantionDirection{
		Right, Left
	}
	
	private RotantionRange rotationRange;
	private RotantionDirection rotationDirection;
	
	public LightSwitcher() {
		rotationRange = RotantionRange.FULL;
		rotationDirection = RotantionDirection.Left;
	}
	
	public void startSweep(){
		rotationRange = RotantionRange.MIDDLE;
		super.start();
	}
	
	public void setDirection(RotantionDirection rotationDirection){
		this.rotationDirection = rotationDirection;
	}
	
	public void run() {
		try {
			motor.setSpeed(rotationSpeed);
			while(true){
				int actualAngleLeft;
				int actualAngleRigth;
				switch (rotationRange) {
				case SMALL:
					actualAngleLeft = angleLeftSmall;
					actualAngleRigth = angleRigthSmall;
					rotationRange = RotantionRange.MIDDLE;
					break;
				case MIDDLE:
					actualAngleLeft = angleLeftMiddle;
					actualAngleRigth = angleRigthMiddle;
					rotationRange = RotantionRange.FULL;
					break;
				default:
					actualAngleLeft = angleLeftFull;
					actualAngleRigth = angleRigthFull;
					break;
				}
				if(rotationDirection == RotantionDirection.Left){
					motor.rotateTo(angleMiddle, true);
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
	//				Thread.sleep(angleMiddle * 1000 / (rotationSpeed * 2));
					motor.rotateTo(actualAngleRigth, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2) + 100);
					motor.rotateTo(angleMiddle, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
					motor.rotateTo(actualAngleLeft, true);
	//				Thread.sleep((actualAngleLeft - angleMiddle) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2) + 100);
				} else {
					motor.rotateTo(angleMiddle, true);
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
	//				Thread.sleep(angleMiddle * 1000 / (rotationSpeed * 2));
					motor.rotateTo(actualAngleLeft, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2) + 100);
					motor.rotateTo(angleMiddle, true);
	//				Thread.sleep((angleMiddle - actualAngleRigth) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
					motor.rotateTo(actualAngleRigth, true);
	//				Thread.sleep((actualAngleLeft - angleMiddle) * 1000 / (rotationSpeed));
					Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2) + 100);
				}
			}
		} catch (InterruptedException e) {
			motor.stop();
		}
	}
	
	public void init(){
		motor.setSpeed(rotationSpeed);
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
