package kit.edu.lego.kompaktor.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.bluetooth.RemoteDevice;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Example stuff to communicate with a server, in this example with the lift
 *
 * @author The Coding-Team
 *
 */
public class Cube {
	private static final String LIFT_NAME = "Lift";
	private static final int GO_DOWN = 0;
	private static final int IS_DOWN = 1;
	private static final int CLOSE_CONNECTION = 2;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static BTConnection connection;

	/**
	 * just a main function...
	 *
	 * @param args
	 *            you know what this is for (at least i hope so)
	 */
	public static void main(String args[]) {
		while (!openConnection(LIFT_NAME)) {
			sleep(1000); // waiting for free connection
		}
		goDown();
		LCD.drawString("Going down", 0, 1);
		while (!canExit()) {
			LCD.drawString("Can exit: No", 0, 2);
			sleep(100);
		}
		LCD.drawString("Can exit: Yes", 0, 2);
		closeConnection();
	}

	/**
	 * opens a connection to a server
	 *
	 * @param server
	 *            name of the server (hope you already paired your device with
	 *            the server)
	 * @return if the connection could be established or not
	 */
	public static boolean openConnection(String server) {
		RemoteDevice btrd = Bluetooth.getKnownDevice(server);
		if (btrd == null) {
			// no such device, you should pair your devices first or check the
			// Devices name
			return false;
		}
		connection = Bluetooth.connect(btrd);
		if (connection == null) {
			// connection failed, try again...
			return false;
		}
		// LCD.clear();
		LCD.drawString("Connected", 0, 0);
		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();
		return true;
	}

	/**
	 * moves the lift down
	 *
	 * @return true
	 */
	public static boolean goDown() {
		writeInt(GO_DOWN);
		return readBool();
	}

	/**
	 * returns if you can exit the lift
	 *
	 * @return if the lift is on the bottom
	 */
	public static boolean canExit() {
		writeInt(IS_DOWN);
		return readBool();
	}

	/**
	 * method for sending an integer to the lift
	 *
	 * @param value
	 *            integer to send
	 */
	private static void writeInt(int value) {
		try {
			dos.writeInt(value);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * method to read a boolean variable
	 *
	 * @return answer of the lift
	 */
	private static boolean readBool() {
		boolean value = false;
		try {
			value = dis.readBoolean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * this method's name should be self explaining
	 */
	private static void closeConnection() {
		if (connection != null) {
			try {
				writeInt(CLOSE_CONNECTION);
				connection.close();
				dis.close();
				dos.close();
			} catch (IOException e) {
				// ignore
			}
		}
		LCD.drawString("Disconnected", 0, 0);
	}

	/**
	 * well, forces the thread to sleep...
	 *
	 * @param millis
	 *            time to sleep in milliseconds
	 */
	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
