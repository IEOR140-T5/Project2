import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.util.*;
import lejos.nxt.comm.RConsole;

/**
 ** Shortest path navigation <br>
 * Uses Tracker, ButtonCounter, Node, Grid, and UltraSonic sensor <br>
 * Uses Node to represent its position and destination
 */
public class GridNavSP {
	/**
	 * controls the movement of the robot
	 */
	Tracker tracker;
	UltrasonicSensor sonar;
	/**
	 * used to iniput destination coordinates
	 */
	private ButtonCounter bc = new ButtonCounter();
	/**
	 * calculates shortest path
	 */
	Grid grid;
	/**
	 * represents the position of the robot; holds x and y coordinates
	 */
	Node position; // position coordinates

	/**
	 * direction of movement; multiple of 90 degrees
	 */
	public int heading;
	/**
	 * set by go()
	 */
	public boolean keepGoing = true;

	/**
	 * initialize touch sensor, listener
	 */
	public GridNavSP(Tracker theTracker, SensorPort echo, int Xlength,
			int Ylength) {
		tracker = theTracker;
		sonar = new UltrasonicSensor(echo);
		grid = new Grid(Xlength, Ylength);
		position = grid.nodes[0][0];
		heading = 0;
	}

	/**
	 * Calibrate line first, then background, calls setDestination()<br>
	 * calls navigate()
	 */
	public void go() {
		tracker.calibrate();
		keepGoing = true;
		heading = 0;

		// RConsole.openBluetooth(0);
		while (keepGoing) {
			System.out.println("SP dest");
			setDestination();
			if (grid.getDestination().isBlocked()) {
				Sound.buzz();
				return;
			}
			navigate();
			Sound.beepSequence();
			LCD.drawInt(10 * position.getX() + position.getY(), 0, 0);
		}
	}

	public void setDestination() {
		bc.count("Destination");
		grid.setDestination((int) bc.getLeftCount(), (int) bc.getRightCount());
		if (grid.getDestination().isBlocked()) {
			Sound.buzz();
			grid.setDestination(position.getX(), position.getY());
		}
		grid.recalc();
		RConsole.println("Dest " + grid.getDestination());
	}

	/**
	 * shows position ; override in communicating subclasses
	 */
	public void sendData(Node n) {
		int code = 0;
		if (n.isBlocked())
			code = 1;
		LCD.drawInt(n.getX(), 2, 0, 1 + code);
		LCD.drawInt(n.getY(), 2, 3, 1 + code);
		Sound.playTone(800 + 200 * code, 200);
	}

	/**
	 * called by go() Maintains position and heading, update after reaching a
	 * node<br>
	 * Return when destination is reached<br>
	 * calls tracker.turn(), tracker.trackline(), checkForData(),
	 * sendBlock(),sendPosition()
	 */
	public void navigate() {
		grid.recalc();
		RConsole.println("Neighbor 0" + position.getNeighbor(0));
		while (position.getDistance() > 0) {
			turnToBest();
			while (checkBlock())
				turnToBest(); // keep trying
			if (grid.getDestination().isBlocked()) {
				Sound.buzz();
				return;
			}
			tracker.trackLine();
			position = position.getNeighbor(heading);// update position
			RConsole.println("Position " + position);
			sendData(position);
		}
		Sound.beepSequenceUp();
	}

	/**
	 * check echo for a block in current heading. block the node and recalc
	 * shortest path.
	 * 
	 * @return true if node is blocked
	 */
	public boolean checkBlock() {
		Delay.msDelay(50);
		int dist = sonar.getDistance();
		LCD.drawInt(dist, 4, 0, 5);
		RConsole.println("checkBlock dist " + dist);
		if (dist > 27)
			return false;
		Node blockedNode;
		{
			blockedNode = position.getNeighbor(heading);
			if (blockedNode == null)
				return false;
		}
		// RConsole.println("Block node " + blockedNode.getX() + " " +
		// blockedNode.getY());
		if (blockedNode.isBlocked())
			return false;
		blockedNode.blocked();
		grid.recalc();
		sendData(blockedNode);
		return true;
	}

	protected void turnToBest() {

		int newHeading = bestDirection();
		tracker.turn(normalize(newHeading - heading));
		heading = newHeading;
	}

	/**
	 * uses and updates heading
	 */
	protected int bestDirection() {

		Node n = position.getNeighbor(heading);
		int minDist = Grid.BIG;
		int dir = heading;
		if (n != null && !n.isBlocked())
			minDist = n.getDistance();
		for (int d = 0; d < 4; d++) {// iterate over all neighbors
			n = position.getNeighbor(d);
			if (n != null && !n.isBlocked() && n.getDistance() < minDist) {
				minDist = n.getDistance();
				dir = d;
			}
		}
		return dir;
	}

	/**
	 * updates heading, returns angle between -2 and 2
	 */
	private int normalize(int angle) {
		if (angle < -2)
			angle += 4;
		else if (angle > 2)
			angle -= 4;
		return angle;
	}

	/**
	 * calls go() in new robot
	 */
	public static void main(String[] args) {
		DifferentialPilot pilot = new DifferentialPilot(5.6, 12, Motor.A,
				Motor.C);
		LightSensor left = new LightSensor(SensorPort.S1);
		LightSensor right = new LightSensor(SensorPort.S4);
		Tracker tracker = new Tracker(pilot, left, right);
		GridNavSP gridnav = new GridNavSP(tracker, SensorPort.S3, 6, 8);
		;
		gridnav.go();
	}
}
