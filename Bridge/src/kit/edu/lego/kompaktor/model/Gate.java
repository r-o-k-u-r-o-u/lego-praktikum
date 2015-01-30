package kit.edu.lego.kompaktor.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;


public class Gate {
	
	private RemoteDevice remoteDevice;
	private BTConnection connection;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private boolean success;
	
	public static void main(String[] args) {
		
		Gate gate = new Gate();
		
		System.out.println("Calling gate");
		// Wait for connection
		while (!gate.connect()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Connected to the gate.");
		

		System.out.println("Sending passed");
		while (!gate.sendPassed()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		System.out.println("success");

	}
	
	public boolean sendPassed() {
		try {
			dataOutputStream.writeBoolean(true);
			dataOutputStream.flush();
			
		} catch (IOException e) {
			success = false;
			return false;
		}
		
		try {
			success = dataInputStream.readBoolean();
		} catch (IOException e) {
//			log("Could not read boolean");
			success = false;
		}
		
		return success;
	}
	
	private void log(String message) {
		System.out.println(message);
	}
	
	
	public boolean connect() {
		
		remoteDevice = new RemoteDevice("TestName", "00165304779A", 0);
		if (remoteDevice == null) {
			log("unknown device" + remoteDevice);
			log("cannot connect to TurnTable");
		}
		
		// check if device was found
		if (remoteDevice != null) {
			connection = Bluetooth.connect(remoteDevice);
			
			// check if connection was established
			if (connection != null) {
				dataOutputStream = connection.openDataOutputStream();
				dataInputStream = connection.openDataInputStream();
				
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	
}
