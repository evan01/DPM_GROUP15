package robot;

import robot.constants.Color;
import robot.constants.Position;

/**
 * This is the interface to the robot which the actual game program will control and use in order to complete all of the
 * stages of the game.
 */
public interface Robot {
	void moveTo(double x, double y);
	
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
	 * @return an abstract color object representing what was scanned
	 */
	Color colorScan();

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
