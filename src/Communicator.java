import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import java.io.*;
import lejos.nxt.*;

/**
 * test communications with GrigNavControl
 * 
 * @author glassey
 */
public class Communicator {

	DataInputStream dataIn;
	DataOutputStream dataOut;
	private int x = 0;
	private int y = 0;

	/**
	 * connects
	 */
	public void connect() {
		LCD.drawString("connect waiting", 0, 0);
		BTConnection btc = Bluetooth.waitForConnection(); // this method is very
															// patient.
		LCD.clear();
		LCD.drawString("connected", 0, 0);
		try {
			dataIn = btc.openDataInputStream();
			dataOut = btc.openDataOutputStream();
		} catch (Exception e) {
		}
		;
		Sound.beepSequence();
	}

	/**
	 * return the array of destination coordinates.
	 * 
	 * @return
	 */
	public int[] getDestination() throws IOException {
		System.out.println("waiting dest");
		LCD.clear();
		LCD.drawString("Read ", 0, 5);
		LCD.drawInt(x, 4, 0, 6);
		LCD.drawInt(y, 4, 8, 6);
		int xy[] = { x, y };
		return xy;
	}

	public void sendData(int code, int x, int y) throws IOException {
		LCD.drawString("SEND " + code, 0, 5);
		LCD.drawInt(x, 4, 0, 6);
		LCD.drawInt(y, 4, 8, 6);
	}
}
