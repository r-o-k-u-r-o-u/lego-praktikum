import lejos.nxt.Motor;

/***
 * der Thread als LightSwitcher.
 * !!! am Anfang muss initAngles ausgeführt werden
 * !!! vor den start() muss init() ausgeführt werden (oder startWithInit())
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
	final static int angleMiddle = 90;
	final static int angleRigth = 180;
	
	public void run() {
		try {
			Motor.A.setSpeed(rotationSpeed);
			while(true){
				Motor.A.rotateTo(angleMiddle, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
				Motor.A.rotateTo(angleRigth, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
				Motor.A.rotateTo(angleMiddle, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
				Motor.A.rotateTo(angleLeft, true);
				Thread.sleep(rotationAngle * 1000 / (rotationSpeed * 2));
			}
		} catch (InterruptedException e) {
			Motor.A.stop();
		}
	}
	
	public void init(){
		Motor.A.setSpeed(rotationSpeed);
		Motor.A.rotateTo(90);
		Motor.A.rotateTo(0);
	}
	
	public void startWithInit(){
		this.init();
		super.start();
	}
	
	public static void initAngles(){
		Motor.A.setSpeed(rotationSpeedInit);
		Motor.A.rotate(-rotationAngle);
		Motor.A.stop();
		Motor.A.resetTachoCount();
	}
}
