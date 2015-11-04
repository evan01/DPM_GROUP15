package robot.navigation;

import robot.constants.Constants;
import robot.sensors.LeftLightSensor;
import robot.sensors.RightLightSensor;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

/**
 * This is another implementation of the navigation class, will travel using a direct path instead of straight path
 */
public class Navigation {

	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	public static Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private EV3MediumRegulatedMotor armMotor;
	private float rotationSpeed;
	private double RADIUS = 2.2, TRACK = 11.5;
	private int SEARCH_SPEED = 20;
	private boolean isNavigating;
	public boolean hasStyro = false;
	private static LeftLightSensor leftLs;
	private static RightLightSensor rightLs;
	
	// for odo correction
	public int horizontalLinesCrossed = -1;
	public int verticalLinesCrossed = -1;
	private boolean hasLeftLine;
	private boolean hasRightLine;
	

	// this is a singleton class
	private static final Navigation ourInstance = new Navigation();

	public static Navigation getInstance() {
		leftLs = LeftLightSensor.getInstance();
		rightLs = RightLightSensor.getInstance();
		odometer = Odometer.getInstance();
		return ourInstance;
	}

	/**
	 * Constructor
	 */
	private Navigation() {

		this.odometer = Odometer.getInstance();

		EV3LargeRegulatedMotor[] motors = new EV3LargeRegulatedMotor[2];
		motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		EV3MediumRegulatedMotor armMotor = this.odometer.getArm();
		this.armMotor = armMotor;

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/**
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/**
	 * Stop the motors of the robot
	 */
	public void stopMoving() {

		this.rightMotor.setSpeed(0);
		this.leftMotor.setSpeed(0);
		this.leftMotor.forward();
		this.rightMotor.forward();
	}

	/**
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			// this.setSpeeds(FAST, FAST);
			// calculates magnitude to travel
			double distance = Math.sqrt(Math.pow((y - odometer.getY()), 2) + Math.pow((x - odometer.getX()), 2));
			goForward(distance);
		}
		this.setSpeeds(0, 0);
	}

	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	/**
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		isNavigating = true;

		leftMotor.rotate(convertDistance(RADIUS, distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), true);

		isNavigating = false;
	}

	public void goForward(double distance, boolean returnImmediately) {

		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		isNavigating = true;

		leftMotor.rotate(convertDistance(RADIUS, distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), returnImmediately);

		isNavigating = false;
	}

	/**
	 * Go Backward a set distance in cm
	 * @param distance the distance with which to move backward
	 */
	public void goBackward(double distance) {

		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		Sound.beep();
		isNavigating = true;

		leftMotor.rotate(-convertDistance(RADIUS, distance), true);
		rightMotor.rotate(-convertDistance(RADIUS, distance), false);

		isNavigating = false;
	}

	/** Motor Setters (FOR ROTATING)
	 *
	 * @param speed the speed at which the robot should be rotating
	 */
	public void setRotationSpeed(float speed) {
		rotationSpeed = speed;
		setSpeeds(rotationSpeed, -rotationSpeed);
	}

	/**
	 * Make the robot perform a sweeping motion
	 * @param forward whether the robot sweeps forward or backward
	 */
	public void search(boolean forward) {
		leftMotor.setSpeed(SEARCH_SPEED);
		rightMotor.setSpeed(SEARCH_SPEED);
		if (forward) {
			leftMotor.forward();
			rightMotor.backward();
		} else {
			leftMotor.backward();
			rightMotor.forward();
		}
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public boolean isNavigating() {
		return this.isNavigating;
	}
	
	//###### ODOMETRY CORRECTION ########
	private void performOdometerCorrection()
	{
		// Updates the x, y and theta values on the Odometer according to the correction
		double heading = odometer.getAng();
		
		// Heading EAST (x++)
		if(heading >= 45 && heading < 135)
		{
			horizontalLinesCrossed++;
			odometer.setPosition(new double[] {horizontalLinesCrossed * Constants.SQUARE_WIDTH + Constants.LIGHT_SENS_OFFSET , 0.0, 90.0}, 
								  new boolean[] {true, false, true});
		}
		// Heading SOUTH (y--)
		else if(heading >= 135 && heading < 225)
		{
			if(verticalLinesCrossed < 0)
			{
				verticalLinesCrossed = 0;
			}
			odometer.setPosition(new double[] {0.0, verticalLinesCrossed * Constants.SQUARE_WIDTH - Constants.LIGHT_SENS_OFFSET , 180.0}, 
								  new boolean[] {false, true, true});
			verticalLinesCrossed--;
		}
		// Heading WEST (x--)
		else if(heading >= 225 && heading < 315)
		{
			if(horizontalLinesCrossed < 0)
			{
				horizontalLinesCrossed = 0;
			}
			odometer.setPosition(new double[] {horizontalLinesCrossed * Constants.SQUARE_WIDTH - Constants.LIGHT_SENS_OFFSET , 0.0, 270.0},
								  new boolean[] {true, false, true});
			horizontalLinesCrossed--;
		}
		// Heading NORTH (y++)
		else
		{
			verticalLinesCrossed++;
			odometer.setPosition(new double[] {0.0, verticalLinesCrossed * Constants.SQUARE_WIDTH + Constants.LIGHT_SENS_OFFSET , 0.0}, 
								  new boolean[] {false, true, true});
		}

	}

}
