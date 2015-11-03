package robot;

import robot.constants.Color;
import robot.constants.Position;

/**
 * This abstract class will allow us to define the methods that our robot needs to be able to do
 */
public abstract class Robot {
	public abstract void moveTo(double x, double y);
	
	/**
	 * This will rotate the robot to whatever angle is specified
	 * @param angle
	 */
	public abstract void turnTo(int angle);

	/**
	 * Orders robot to do a single ultrasonic scan
	 * @return distance scanned by ultrasonic sensor
	 */
	public abstract int usScan();

	/**
	 * Orders
	 * @return an abstract color object representing what was scanned
	 */
	public abstract Color colorScan();

	/**
	 * Orders the robot to capture a block in front of it
	 */
	public abstract void capture();

	/**
	 * Orders the robot to localize itself
	 */
	public abstract void localize();

	/**
	 * Gets the current position of the robot
	 * @return the odometer reading
	 */
	public abstract Position getOdometerReading();
	
}
