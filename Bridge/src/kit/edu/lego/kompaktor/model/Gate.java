package kit.edu.lego.kompaktor.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;


public class Gate {
	
	private static RemoteDevice remoteDevice;
	private static BTConnection connection;
	private static DataInputStream dataInputStream;
	private static DataOutputStream dataOutputStream;
	private static boolean success;
	private static Thread connectionThread;
	
	public static void main(String[] args) {
		
		System.out.println("Calling gate");
		// Wait for connection
		Gate.connect();
		try {
			Gate.waitForConnection();
		} catch (InterruptedException e1) {}
		System.out.println("Connected to the gate.");
		

		System.out.println("Sending passed");
		while (!Gate.sendPassed()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		System.out.println("success");

	}
	
	public static boolean sendPassed() {
		try {
			dataOutputStream.writeBoolean(true);
			dataOutputStream.flush();
			
		} catch (IOException e) {
			log("Could not send passed.");
			success = false;
			return false;
		}
		
		try {
			success = dataInputStream.readBoolean();
		} catch (IOException e) {
			log("Could not read boolean");
			success = false;
		}
		
		return success;
	}
	
	private static void log(String message) {
		System.out.println(message);
	}
	
	
	public static void connect() {
		connectionThread = new Thread() {
			public void run() {
				remoteDevice = new RemoteDevice("TestName", "00165304779A", 0);
				if (remoteDevice == null) {
					log("unknown device" + remoteDevice);
					log("cannot connect to TurnTable");
				}
				
				// check if device was found
				if (remoteDevice != null) {
					
					// check if connection was established
					while ((connection = Bluetooth.connect(remoteDevice)) == null)
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					
					dataOutputStream = connection.openDataOutputStream();
					dataInputStream = connection.openDataInputStream();
				}
			}
		};
		connectionThread.start();
	}
	
	/**
	 *  Call to wait for connection to be established
	 */
	public static void waitForConnection() throws InterruptedException {
		connectionThread.join();
	}
	
}
