package kit.edu.lego.kompaktor.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;


//import lejos.nxt.Motor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
//import lejos.robotics.navigation.DifferentialPilot;

public class TurnTable {

	private enum TurnTableCommand {
		HELLO, TURN, DONE, CYA, UNKNOWN;

		public static TurnTableCommand getByOrdinal(int commandOrdinal) {
			if (commandOrdinal >= values().length) {
				return UNKNOWN;
			}
			return values()[commandOrdinal];
		}
	}

	public static void main(String[] args) {
		TurnTable turnTable = new TurnTable();
		turnTable.use();
	}

	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;

	public boolean connect() {
		String deviceName = "TurnTable";
		RemoteDevice device = lookupDevice(deviceName);
		BTConnection connection = Bluetooth.connect(device);

		dataOutputStream = connection.openDataOutputStream();
		dataInputStream = connection.openDataInputStream();

		return true;
	}
	
	public boolean waitHello() {
		try {
			TurnTableCommand command = receiveCommand();
			assertCommand(command, TurnTableCommand.HELLO);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean turn() {
		try {
			sendCommand(TurnTableCommand.TURN);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean waitDone() {
		try {
			TurnTableCommand command = receiveCommand();
			assertCommand(command, TurnTableCommand.DONE);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean sendCYA() {
		try {
			sendCommand(TurnTableCommand.CYA);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public void use() {
    
		String deviceName = "TurnTable";
		RemoteDevice device = lookupDevice(deviceName);
		BTConnection connection = Bluetooth.connect(device);
		try {
			dataOutputStream = connection.openDataOutputStream();
			dataInputStream = connection.openDataInputStream();

			TurnTableCommand command = receiveCommand();
			assertCommand(command, TurnTableCommand.HELLO);

			// drive forward

			sendCommand(TurnTableCommand.TURN);

			command = receiveCommand();
			assertCommand(command, TurnTableCommand.DONE);

			// drive backward

			sendCommand(TurnTableCommand.CYA);

			
		} catch (IOException e) {
			System.out.println("IOException");
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public RemoteDevice lookupDevice(String deviceName) {
		RemoteDevice device = Bluetooth.getKnownDevice(deviceName);
		if (device == null) {
			log("unknown device" + deviceName);
			log("cannot connect to TurnTable");
		}
		return device;
	}

	private void assertCommand(TurnTableCommand command,
			TurnTableCommand assertetedCommand) throws IOException {
		if (command != assertetedCommand) {
			log("Invalid command:");
			log("Expected:" + assertetedCommand);
			throw new IOException("Invalid Command");
		}
	}

	public TurnTableCommand receiveCommand() throws IOException {
		int commandOrdinal = dataInputStream.readInt();
		TurnTableCommand command = TurnTableCommand
				.getByOrdinal(commandOrdinal);
		log("Receive:" + command.name());
		return command;
	}

	private void sendCommand(TurnTableCommand command) throws IOException {
		dataOutputStream.writeInt(command.ordinal());
		dataOutputStream.flush();
		log("Send: " + command.name());
	}

	private void log(String message) {
		System.out.println(message);
	}

}