package kit.edu.lego.kompaktor.model;

import kit.edu.lego.kompaktor.behavior.BarcodeDetector;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner;
import kit.edu.lego.kompaktor.behavior.ParcoursRunner.LEVEL_NAMES;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * 
 * @author Christian
 *
 */
public class Kompaktor {

	public static final TouchSensor TOUCH_RIGHT = new TouchSensor(SensorPort.S3);
	public static final TouchSensor TOUCH_LEFT = new TouchSensor(SensorPort.S2);
	public static final LightSensor LIGHT_SENSOR = new LightSensor(SensorPort.S1, true);
	public static final UltrasonicSensor SONIC_SENSOR = new UltrasonicSensor(SensorPort.S4);
	public static final DifferentialPilot DIFF_PILOT = new DifferentialPilot(3, 17, Motor.C, Motor.B, true);
	public static final DifferentialPilot DIFF_PILOT_REVERSE = new DifferentialPilot(3, 17, Motor.C, Motor.B, false);	
	
	private static boolean armInitialized = false;
	
	private static BarcodeDetector barcodeDetector = null;
	
	/**
	 * Startet level und beendet es wenn es fertig ist
	 * 
	 * @param level
	 */
	public static void startLevel(LEVEL_NAMES level) {
		
		// TODO: maybe only when needed
		if (!armInitialized) {
			LightSwitcher.initAngles();
			armInitialized = true;
		}
		
		ParcoursRunner currentRunner = ParcoursRunner.getNewRunner(level);

		currentRunner.init();
		currentRunner.start();

		while (!currentRunner.isDone());

		try {
			currentRunner.stop();
		} catch (InterruptedException e) {
			System.out.println("Launcher error.");
		}
	}
	
	/**
	 * Startet level und gibt ParcoursRunner zurück. Dieser muss selbt beendet werden.
	 * 
	 * @param level
	 * @param returnImmediately
	 * @return ParcoursRunner
	 */
	public static ParcoursRunner startLevel(LEVEL_NAMES level, boolean returnImmediately) {
		
		// TODO: maybe only when needed
		if (!armInitialized) {
			LightSwitcher.initAngles();
			armInitialized = true;
		}
		
		ParcoursRunner currentRunner = ParcoursRunner.getNewRunner(level);

		currentRunner.init();
		currentRunner.start();

		return currentRunner;
	}
	
	public static void parkArm() {
		if (!armInitialized) {
			LightSwitcher.initAngles();
			armInitialized = true;
		}
		LightSwitcher.setAngle(-90);
	}
	
	public static void stretchArm() {
		if (!armInitialized) {
			LightSwitcher.initAngles();
			armInitialized = true;
		}
		LightSwitcher.setAngle(0);
	}
	
	public static void startArm() {
		// TODO: implement
	}
	
	public static void stopArm() {
		// TODO: implement
	}
	
	public static void startBarcodeChecking() {
		
		// TODO: check
		if (barcodeDetector == null) {
			barcodeDetector = new BarcodeDetector();
		}
		
		barcodeDetector.init();
		barcodeDetector.start();
	}
	
	public static void stopBarcodeChecking() {
		
		// TODO: check
		if (barcodeDetector != null) {
			
			try {
				barcodeDetector.stop();
				barcodeDetector = null;
				
			} catch (InterruptedException e) {
				System.out.println(">Kompaktor<\n>>Error stopping BarcodeDetector");
			}
		}
	}
	
	public static void showText(String text) {
		LCD.clear();
		System.out.println(text);
	}
	
	public static boolean isTouched() {
		return TOUCH_LEFT.isPressed() || TOUCH_RIGHT.isPressed();
	}
	
	public static boolean isTouchedLeft() {
		return TOUCH_LEFT.isPressed();
	}
	
	public static boolean isTouchedRight() {
		return TOUCH_RIGHT.isPressed();
	}
	
	public static boolean isTouchedBoth() {
		return TOUCH_LEFT.isPressed() && TOUCH_RIGHT.isPressed();
	}
	
	public static boolean isNotTouched() {
		return !TOUCH_LEFT.isPressed() && !TOUCH_RIGHT.isPressed();
	}
}
