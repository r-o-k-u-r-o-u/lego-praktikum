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
class LightSwitcher extends Thread{
	
	final static int rotationAngle = 200;
	final static int rotationSpeed = 500;
	final static int rotationSpeedInit = 100;
	final static int angleLeft = 0;
	final static int angleMiddle = 95;
	final static int angleRigth = 190;
	final static NXTRegulatedMotor motor = Motor.A;
	
	
	
	public void run() {
		try {
			motor.setSpeed(rotationSpeed);
			while(true){
				motor.rotateTo(angleMiddle, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
				motor.rotateTo(angleRigth, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
				motor.rotateTo(angleMiddle, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
				motor.rotateTo(angleLeft, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
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
		if(angle < angleLeft)
			angle = angleLeft;
		if (angle > angleRigth)
			angle = angleRigth;
		return (angle - angleMiddle) * 90 / angleMiddle;
	}
	
	public static double getRegulatedCurrentAngleDouble(){
		int angle = motor.getPosition();
		if(angle < angleLeft)
			angle = angleLeft;
		if (angle > angleRigth)
			angle = angleRigth;
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
