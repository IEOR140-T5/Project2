package milestone5;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import milestone4.*;
import tracker.*;

import java.io.IOException;

public class BTShortestPath extends ShortestPath {

	private BTCommunicator btc;

	/**
	 * 
	 * @param tracker
	 * @param usensor
	 * @param x
	 * @param y
	 * @param btc
	 */
	public BTShortestPath(Tracker tracker, UltrasonicSensor usensor, int x,
			int y, BTCommunicator btc) {
		super(tracker, usensor, x, y);
		this.btc = btc;
	}

	/**
	 * Sets the destination using Bluetooth
	 */
	public void updateDestination() {
		try {
			int[] xy = btc.receive();
			super.setDestinationCoord(xy[0], xy[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Blocks the specified node, and tells the computer that the node has been
	 * blocked
	 */
	public void updateObstaclePosition(Node obstacleNode) {
		obstacleNode.blocked();
		try {
			btc.send(1, obstacleNode.getX(), obstacleNode.getY());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the robot position, and tells the computer the robot's new
	 * position.
	 */
	public void updateRobotPosition(Node destination) {
		currentPosition = destination;
		try {
			btc.send(0, currentPosition.getX(), currentPosition.getY());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carries out the mission
	 */
	public void go() {
		tracker.calibrate();
		heading = 0;
		while (true) {
			updateDestination();
			toDestination();
			Sound.beepSequence();
			LCD.drawString("(" + currentPosition.getX() + "," + currentPosition.getY() + ")", 0, 0);
		}
	}
}
