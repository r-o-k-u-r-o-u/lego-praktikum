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
	public static final int SLEEP_INTERVAL = 10;
	public static final int SLEEP_INTERVAL_SHORT = 1;
	public static final int SLEEP_INTERVAL_LONG = 100;
	
	private static boolean armInitialized = false;
	
	private static BarcodeDetector barcodeDetector = null;
	
	
	/**
	 * Startet level und beendet es wenn es fertig ist
	 * 
	 * @param level
	 */
	public static void startLevel(LEVEL_NAMES level) {
		
		if (!armInitialized) {
			LightSwitcher.initAngles();
			armInitialized = true;
		}
		
		ParcoursRunner currentRunner = ParcoursRunner.getNewRunner(level);

		currentRunner.init();
		currentRunner.start();

		while (!currentRunner.isDone()) {
			try {
				Thread.sleep(Kompaktor.SLEEP_INTERVAL);
			} catch (InterruptedException e) {}
		}

		try {
			currentRunner.stop();
		} catch (InterruptedException e) {
			System.out.println("Launcher error.");
		}
	}
	
	/**
	 * Startet level und gibt ParcoursRunner zur�ck. Dieser muss selbt beendet werden.
	 * 
	 * @param level
	 * @param returnImmediately
	 * @return ParcoursRunner
	 */
	public static ParcoursRunner startLevel(LEVEL_NAMES level, boolean returnImmediately) {
		
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
	
	public static void setFloodlight(boolean val) {
		LIGHT_SENSOR.setFloodlight(val);
	}
	
	public static int readLightValue() {
		return LIGHT_SENSOR.readValue();
	}
	
	public static int readDistanceValue() {
		return SONIC_SENSOR.getDistance();
	}
	
	
	public static boolean onLED() throws InterruptedException {
		
		int[] val = readLightDifferenceArr();
		
		if (val[0] < 6) {
			
			if (val[2] >= 40) {
				return true;
			} else if (val[2] >= 25) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
		
	}
	
	
 public static boolean onRedLED() throws InterruptedException {
		
		int[] val = readLightDifferenceArr();
		
		if (val[0] < 6) {
			
			if (val[2] >= 40) {
				return false;
			} else if (val[2] >= 25) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
		
	}
	
	
 public synchronized static int[] readLightDifferenceArr() throws InterruptedException {
		
		boolean floodOn = LIGHT_SENSOR.isFloodlightOn();
		int vals[] = new int[3];
		
		setFloodlight(true);
		Thread.sleep(20);
		vals[1] = LIGHT_SENSOR.readValue();

		setFloodlight(false);
		Thread.sleep(20);
		vals[2] = LIGHT_SENSOR.readValue();
		
		setFloodlight(floodOn);
		
		vals[0] = Math.abs(vals[1]-vals[2]);
		
		return vals;
		
	}
 
	public synchronized static int readLightDifferenceOnOff() throws InterruptedException {
		
		boolean floodOn = LIGHT_SENSOR.isFloodlightOn();
		
		setFloodlight(true);
		Thread.sleep(20);
		int val1 = LIGHT_SENSOR.readValue();
		
		setFloodlight(false);
		Thread.sleep(20);
		int val2 = LIGHT_SENSOR.readValue();
		
		setFloodlight(floodOn);
		
		return Math.abs(val1-val2);
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
