package milestone4;
/**
 * represents a junction in a rectangular grid<br>
 * uses Grid ; uses Grid In calculating shortest path, Node has the
 * responsibility of deciding if it should be added to the end of the list in
 * Grid
 * 
 * @author Roger Glassey, Khoa Tran
 */
public class Node {
	/**
	 * Static and instance variables
	 */
	public static final int BIG = 99; // infinity in the grid
	
	private int _x, _y;             // row and column index of my location in grid
	private int _distance;          // shortest distance from destination set by reset(), newDistance()
	Node[] neighbor = new Node[4];  // array of neighboring nodes, in order of direction
	private boolean blocked = false;// is it blocked?

	/**
	 * Allocates a new Node at location x,y in the Grid
	 * @param x
	 * @param y
	 */
	public Node(int x, int y) {
		_x = x;
		_y = y;
	}

	/**
	 * Returns the x coordinate
	 * @return x coordinate
	 */
	public int getX() {
		return _x;
	}

	/**
	 * Returns the y coordinate
	 * @return the y coordinate
	 */
	public int getY() {
		return _y;
	}
	
	/**
	 * Returns the length of the shortest path to the destination
	 * @return shortest distance to the destination
	 */
	public int getDistance() {
		return _distance;
	}

	/**
	 * prepare for shortest path calculation; called by Grid; sets the mode
	 * distance to a big number, which indicates shortest distance not yet known
	 */
	public void reset() {
		_distance = BIG;
	}

	/**
	 * updates distance if parameter d is smaller than current value, in which
	 * case return true <br>
	 * indicating node should be added to list.
	 */
	public boolean newDistance(int d) {
		if (blocked) {
			return false;
		} else if (d < _distance) {
			_distance = d;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * node is now blocked; distance to destination is BIG
	 */
	public void blocked() {
		blocked = true;
		_distance = BIG;
	}

	/**
	 * @return is the node blocked?
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * resets blocked flag
	 */
	public void unblocked() {
		blocked = false;
	}

	/**
	 * Returns the neighbor in the direction
	 */
	public Node neighbor(int direction) {
		return neighbor[direction];
	}
}
