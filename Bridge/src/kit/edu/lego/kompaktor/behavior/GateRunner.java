package kit.edu.lego.kompaktor.behavior;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class GateRunner extends ParcoursRunner {

	private RemoteDevice remoteDevice;
	private BTConnection connection;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private boolean success;
	private LabyrinthRunner driver;
	ParcoursRunner lineRunner;

	public static void main(String[] args) {

		GateRunner gr = new GateRunner();
		gr.run();

	}

	public GateRunner() {
		driver = new LabyrinthRunner();
	}

	/**
	 * Tries to connect to the gate
	 * 
	 * @return true if connection is establish, false otherwise
	 */
	public boolean connectionToGateSuccessful() {

		remoteDevice = new RemoteDevice("TestName", "00165304779A", 0);
		if (remoteDevice == null) {
			System.out.println("Found no remote device");
			return false;
		}

		connection = Bluetooth.connect(remoteDevice);
		if (connection == null) {
			System.out.println("Connection is null");
			return false;
		}

		inputStream = connection.openDataInputStream();
		outputStream = connection.openDataOutputStream();

		return (inputStream != null && outputStream != null);
	}

	/**
	 * Puts the thread to sleep.
	 * 
	 * @param millis
	 *            how long the thread sleeps
	 */
	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	@Override
	public void run() {

		driver.drive(14, 30);

		Sound.beepSequenceUp();
		System.out.println("Calling gate");
		// Wait for connection
		while (!this.connectionToGateSuccessful()) {
			sleep(50);
		}
		System.out.println("Connected to the gate.");

		// Now the gate opens & a timer of 20 seconds starts
		// in this time the robot has to drive through & send a "I passed"
		// signal
		this.sleep(1000);
		driver.straight(80);
		driver.rotate(-160);
		driver.straight(-20);

		// Robot drives through the gate
		System.out.println("Driving through.");

		// Tell the gate that robot passed, send a "I passed" signal
		try {
			this.outputStream.writeBoolean(true);
			this.outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Sent passing signal");

		// Wait for anwswer from gate
		try {
			this.success = this.inputStream.readBoolean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (this.success) {
			// success, gate received the "I passed" signal
			// gate is closed & connection is closed
		} else {
			// no success, connection timeouted before gate recieved anything
			// from robot
			// gate is closed & connection is closed -> robot has to try again
		}

		System.out.println("Recieved: " + this.success);

		lineRunner = new RopeBridgeRun();
		lineRunner.start();
		

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDone() {

		return lineRunner.isDone();
	}

}
