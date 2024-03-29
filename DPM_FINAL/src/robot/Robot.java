package robot;

import robot.constants.Color;
import robot.constants.Position;

/**
 * This is the interface to the robot which the actual game program will control and use in order to complete all of the
 * stages of the game.
 */
public interface Robot {
	/**
	 * This will perform the object search algorithm
	 */
	void ObjectSearch();
	/**
	 * This will perform obstacle avoidance while traveling to the
	 * specified location
	 * @param x
	 * @param y
	 */
	void goTo(int x, int y);
	
	/**
	 * This will perform the standard travel to method,
	 * without moving from square center to square center
	 * @param x
	 * @param y
	 */
	void travelTo(double x, double y);
	/**
	 * This will rotate the robot to whatever angle is specified
	 * @param angle
	 */
	void turnTo(int angle);

	/**
	 * Orders robot to do a single ultrasonic scan
	 * @return distance scanned by ultrasonic sensor
	 */
	
	int usScan();

	/**
	 * Orders
	 * @return int value representing one of the 5 colors
	 */
	int colorScan();

	/**
	 * Orders the robot to capture a block in front of it
	 */
	void capture();

	/**
	 * Orders the robot to localize itself
	 */
	void localize();

	/**
	 * Gets the current position of the robot
	 * @return the odometer reading
	 */
	Position getOdometerReading();
	
}
