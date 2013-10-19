package milestone5;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import java.io.*;
import lejos.nxt.*;

/**
 * test communications with GrigNavControl
 * 
 * @author glassey
 */
public class BTCommunicator {

	BTConnection btc;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	private int x;
	private int y;

	/**
	 * Creates a BTCommunicator object and connects it to the computer, then
	 * sets up the data streams
	 */
	public BTCommunicator() {
		connect();
		OutputStream os = btc.openOutputStream();
		dataOut = new DataOutputStream(os);
		InputStream is = btc.openInputStream();
		dataIn = new DataInputStream(is);
		System.out.println("Data stream opened.");
	}

	/**
	 * Connects the robot and the computer
	 */
	public void connect() {
		LCD.drawString("connect waiting", 0, 0);
		BTConnection btc = Bluetooth.waitForConnection();
		LCD.clear();
		LCD.drawString("connected", 0, 0);
		LCD.refresh();
		Sound.beepSequence();
	}

	/**
	 * @return the array of destination coordinates
	 */
	public int[] receive() throws IOException {
		System.out.println("waiting dest");
		LCD.clear();
		LCD.drawString("Read ", 0, 5);
		x = dataIn.readInt();
		y = dataIn.readInt();
		LCD.drawInt(x, 4, 0, 6);
		LCD.drawInt(y, 4, 8, 6);
		int xy[] = { x, y };
		return xy;
	}

	/**
	 * Sends Data from computer to robot
	 * 
	 * @param header
	 *            - new destination or blocked node
	 * @param x
	 * @param y
	 * @throws IOException
	 */
	public void send(int header, int x, int y) throws IOException {
		LCD.drawString("SEND " + header, 0, 5);
		LCD.drawInt(x, 4, 0, 6);
		LCD.drawInt(y, 4, 8, 6);
		dataOut.writeInt(header);
		dataOut.writeInt(x);
		dataOut.writeInt(y);
		dataOut.flush();
	}

	/**
	 * Closes everything
	 * @throws IOException
	 */
	public void exit() throws IOException {
		dataIn.close();
		dataOut.close();
		btc.close();
	}
}
