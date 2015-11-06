package robot.navigation;

import robot.constants.Constants;
import robot.sensors.ColorSensor;
import robot.sensors.LeftLightSensor;
import robot.sensors.RightLightSensor;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Delay;

/**
 * This is another implementation of the navigation class, will travel using a direct path instead of straight path
 */
public class Navigation {

	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	public static Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor, clawMotor;
	private EV3MediumRegulatedMotor armMotor;
	private float rotationSpeed;
	private double RADIUS = 2.2, TRACK = 11.5;
	private int SEARCH_SPEED = 20;
	private boolean isNavigating;
	public boolean hasStyro = false;
	private static LeftLightSensor leftLS;
	private static RightLightSensor rightLS;
	
	// for odo correction
	public int horizontalLinesCrossed = -1;
	public int verticalLinesCrossed = -1; 
	private boolean hasLeftLine;
	private boolean hasRightLine;
	

	// this is a singleton class
	private static final Navigation ourInstance = new Navigation();

	public static Navigation getInstance() {
		return ourInstance;
	}

	/**
	 * Constructor
	 */
	public Navigation() {

		this.odometer = Odometer.getInstance();
		this.leftLS = LeftLightSensor.getInstance();
		this.rightLS = RightLightSensor.getInstance();

		EV3LargeRegulatedMotor[] motors = new EV3LargeRegulatedMotor[2];
		motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		EV3MediumRegulatedMotor armMotor = this.odometer.getArm();
		this.armMotor = armMotor;
		EV3LargeRegulatedMotor clawMotor = this.odometer.getClawMotor();
		this.clawMotor = clawMotor;

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
	 * TravelTo bacwards function 
	 */
	public void travelToBackwards(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			// this.setSpeeds(FAST, FAST);
			// calculates magnitude to travel
			double distance = Math.sqrt(Math.pow((y - odometer.getY()), 2) + Math.pow((x - odometer.getX()), 2));
			goBackward(distance);
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
	
	//######### CORRECTIVE NAVIGATION ########## --- Mahmood
	
	public void travelToWithCorrection(double x, double y, double theta)
	{
		odometer.getLeftMotor().setSpeed(Constants.SLOW);
		odometer.getRightMotor().setSpeed(Constants.SLOW);

		odometer.getLeftMotor().forward();
		odometer.getRightMotor().forward();

		Sound.buzz();
		// Heading EAST (x++)
		if(theta >= 45 && theta < 135)
		{
			while(odometer.getX() < x)
			{
				travelWithCorrection(leftLS, rightLS);
			}
			odometer.getLeftMotor().stop(true);
			odometer.getRightMotor().stop();
			if(odometer.getX() > x)
			{
				travelToBackwards(x, odometer.getY());
			}
		}
		// Heading SOUTH (y--)
		else if(theta >= 135 && theta < 225)
		{
			while(odometer.getY() > y)
			{
				travelWithCorrection(leftLS, rightLS);
			}
			odometer.getLeftMotor().stop(true);
			odometer.getRightMotor().stop();
			if(odometer.getY() < y)
			{
				travelToBackwards(odometer.getX(), y);
			}
		}
		// Heading WEST (x--)
		else if(theta >= 225 && theta < 315)
		{
			while(odometer.getX() > x )
			{
				travelWithCorrection(leftLS, rightLS);
			}
			odometer.getLeftMotor().stop(true);
			odometer.getRightMotor().stop();
			if(odometer.getX() < x)
			{
				travelToBackwards(x, odometer.getY());
			}

		}
		// Heading NORTH (y++)
		else
		{
			
			while(odometer.getY() < y)
			{
				travelWithCorrection(leftLS,rightLS );
			}
			odometer.getLeftMotor().stop(true);
			odometer.getRightMotor().stop();
			if(odometer.getY() > y)
			{
				travelToBackwards(odometer.getX(), y );
			}
		}
		Delay.msDelay(500);
	}
	
	
	public void travelWithCorrection(LeftLightSensor leftLS, RightLightSensor rightLS)
	{
		// Makes the robot go straight with correction. Needs a while loop to work until a certain condition

		// Case: Detecting both lines at the same time: Just do position and angle corrections
		if(((leftLS.getIntensity()< Constants.LIGHT_THRESHOLD) && (rightLS.getIntensity() <  Constants.LIGHT_THRESHOLD)))
		{
			
			performOdometerCorrection();
			Sound.beepSequenceUp();

			odometer.getLeftMotor().setSpeed(Constants.SLOW);
			odometer.getRightMotor().setSpeed(Constants.SLOW);
			odometer.getLeftMotor().forward();
			odometer.getRightMotor().forward();

			hasLeftLine = false;
			hasRightLine = false;

			Delay.msDelay(500);
		}
		/*
		 * Case: both color sensors are on the line after a correction 1- Stop the motors 2- adjust coordinates 3- reset the booleans 4-
		 * continue going forward
		 */
		else if(hasLeftLine && hasRightLine)
		{
			Delay.msDelay(500);
			odometer.getLeftMotor().stop(true);
			odometer.getRightMotor().stop();

			performOdometerCorrection();

			Sound.buzz();

			odometer.getLeftMotor().setSpeed(Constants.SLOW);
			odometer.getRightMotor().setSpeed(Constants.SLOW);

			odometer.getLeftMotor().forward();
			odometer.getRightMotor().forward();

			hasLeftLine = false;
			hasRightLine = false;

			Delay.msDelay(500);

			/*
			 * Case: detection of line with the left color sensor (drifting right)
			 */
		}
		else if((leftLS.getIntensity()< Constants.LIGHT_THRESHOLD) && !hasLeftLine)
		{
			odometer.getLeftMotor().stop(true);
			odometer.getRightMotor().stop();

			Delay.msDelay(500);

			// If you detect a line with the right color sensor after adjustment then you're good
			if((rightLS.getIntensity() <  Constants.LIGHT_THRESHOLD))
			{
				odometer.getLeftMotor().stop(true);
				odometer.getRightMotor().stop();

				hasRightLine = true;
				hasLeftLine = true;

				Delay.msDelay(250);
			}
			else
			{
				// Otherwise rotate the motor that did not detect it
				odometer.getRightMotor().setSpeed(Constants.SLOW);
				odometer.getRightMotor().forward();

				hasLeftLine = true;

				double detectionTime = System.currentTimeMillis();

				// Keep searching for a line with the right CS
				while(!(rightLS.getIntensity() <  Constants.LIGHT_THRESHOLD))
				{
					if((detectionTime - System.currentTimeMillis()) > 10000)
					{
						odometer.getLeftMotor().rotate(9000);
						break;
					}
				}

				// until you detect one
				hasRightLine = true;

				// Once you do then stop the motors and wait
				odometer.getLeftMotor().stop(true);
				odometer.getRightMotor().stop();

				Delay.msDelay(500);
			}

			// you're drifting right, so go left
			odometer.getRightMotor().rotate(30);

			/*
			 * Case: You detect a line with the right CS first (drifting left)
			 */
		}
		else if((rightLS.getIntensity() >  Constants.LIGHT_THRESHOLD) && !hasRightLine)
		{
			odometer.getRightMotor().stop(true);
			odometer.getLeftMotor().stop();

			Delay.msDelay(500);

			odometer.getLeftMotor().rotate(-30);

			if((leftLS.getIntensity()< Constants.LIGHT_THRESHOLD))
			{
				odometer.getRightMotor().stop(true);
				odometer.getLeftMotor().stop();

				hasRightLine = true;
				hasLeftLine = true;

				Delay.msDelay(500);
			}
			else
			{
				odometer.getLeftMotor().setSpeed(Constants.SLOW);
				odometer.getLeftMotor().forward();

				hasRightLine = true;

				double detectionTime = System.currentTimeMillis();
				while(!(leftLS.getIntensity()< Constants.LIGHT_THRESHOLD))
				{
					if((detectionTime - System.currentTimeMillis()) > 10000)
					{
						odometer.getRightMotor().rotate(30000);
						break;
					}
				}

				hasLeftLine = true;

				odometer.getRightMotor().stop(true);
				odometer.getLeftMotor().stop();

				Delay.msDelay(500);
			}
			// you're drifting left, so go right
			odometer.getLeftMotor().rotate(-10);
		}
	}
	//###### ODOMETRY CORRECTION ########
	// this could have been simpler but I needed to keep count of gridline number - Mahmood
	private void performOdometerCorrection()
	{
		// Updates the x, y and theta values on the Odometer according to the correction
		double heading = odometer.getAng();
		
		// Heading EAST (x++)
		if(heading >= 45 && heading < 135)
		{
			double xActual = odometer.getX() + Math.sin(odometer.getAng()*Constants.LIGHT_SENS_OFFSET);
			double correctionX = (xActual%30.48);
			horizontalLinesCrossed++;
			odometer.setPosition(new double[] {correctionX + odometer.getX(), 0.0, 90.0}, 
								  new boolean[] {true, false, true});
		}
		// Heading SOUTH (y--)
		else if(heading >= 135 && heading < 225)
		{
			if(verticalLinesCrossed < 0)
				verticalLinesCrossed = 0;
			
			double yActual = odometer.getY() + Math.cos(odometer.getAng()*Constants.LIGHT_SENS_OFFSET);
			double correctionY = (yActual%30.48);
			odometer.setPosition(new double[] {0.0, correctionY + odometer.getY() , 180.0}, 
								  new boolean[] {false, true, true});
			verticalLinesCrossed--;
		}
		// Heading WEST (x--)
		else if(heading >= 225 && heading < 315)
		{
			if(horizontalLinesCrossed < 0)
				horizontalLinesCrossed = 0;
			
			double xActual = odometer.getX() + Math.sin(odometer.getAng()*Constants.LIGHT_SENS_OFFSET);
			double correctionX = (xActual%30.48);
			
			odometer.setPosition(new double[] {correctionX + odometer.getX(), 0.0, 270.0},
								  new boolean[] {true, false, true});
			horizontalLinesCrossed--;
		}
		// Heading NORTH (y++)
		else
		{
			double yActual = odometer.getY() + Math.cos(odometer.getAng()*Constants.LIGHT_SENS_OFFSET);
			double correctionY = (yActual%30.48);
			verticalLinesCrossed++;
			odometer.setPosition(new double[] {0.0, correctionY + odometer.getY(), 0.0}, 
								  new boolean[] {false, true, true});
		}

	}
	public void clawUp(){
		clawMotor.backward();
		clawMotor.setSpeed(150);
		clawMotor.rotate(180);
		Delay.msDelay(250);
		clawMotor.stop();
	}
	public void clawDown(){
		clawMotor.forward();
		clawMotor.setSpeed(150);
		clawMotor.rotate(-150);
		Delay.msDelay(250);
		clawMotor.stop();
	}
    public void armOpen() {
        goForward(3);
        armMotor.forward();
        armMotor.setSpeed(150);
        armMotor.rotate(-180);
        Delay.msDelay(250);
        armMotor.stop();
    }
    public void grab() {
        goForward(3);
        armMotor.backward();
        armMotor.setSpeed(150);
        armMotor.rotate(240);
        Delay.msDelay(250);
        armMotor.stop();
        this.hasStyro = true;
    }
	
	

}
