package robot.navigation;

import robot.constants.Constants;
import robot.constants.Position;
import robot.sensors.ColorSensor;
import robot.sensors.LeftLightSensor;
import robot.sensors.RightLightSensor;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Delay;

/**
 * This is another implementation of the navigation class, will travel using a
 * direct path instead of straight path
 */
public class Navigation {

	final int FAST = 200, SLOW = Constants.SLOW, ACCELERATION = 2000;
	final static double DEG_ERR = 1.2, CM_ERR = 1.0;
	public static Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor, clawMotor;
	private EV3MediumRegulatedMotor armMotor;
	private Traveler path;
	private float rotationSpeed;
	private double RADIUS = Constants.WHEEL_RADIUS, TRACK = Constants.TRACK;
	private int SEARCH_SPEED = 60;
	private boolean isSearchingBlock;
	private boolean isNavigating;
	public boolean hasStyro = false;
	private static LeftLightSensor leftLS;
	private static RightLightSensor rightLS;
	private boolean isBlackLineDetected;
	private boolean scanRight;
	private boolean scanLeft;
	private boolean lineDetected = false;
	private boolean isSweepMode;


	// for odo correction
	public static int horizontalLinesCrossed = 1;	//currentY
	public static int verticalLinesCrossed = 1; 	//currentX

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

		isSearchingBlock=false;
		isSweepMode=false;
	}

	public void setSweepMode(boolean isSweepMode) {
		this.isSweepMode = isSweepMode;
	}

	public void setSearchingBlock(boolean isSearchingBlock) {
		this.isSearchingBlock = isSearchingBlock;
	}

	public void setIsNavigating(boolean isNavigating) {
		this.isNavigating = isNavigating;
	}


	/**
	 * Functions to set the motor speeds jointly
	 */
	public synchronized void setSpeeds(float lSpd, float rSpd) {
	//public void setSpeeds(float lSpd, float rSpd) {
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

	//public synchronized void setSpeeds(int lSpd, int rSpd) {
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
	//public synchronized void setFloat() {
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/**
	 * Stop the motors of the robot
	 */
	///public synchronized void stopMoving() {
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
	//public synchronized void travelTo(double x, double y) {
	public void travelTo(double x, double y) {
		double minAng;
		int counter=0;
		//this should not be a while loop , it should be an if statement
		//if we want to have a while loop, we have to turn to the expected angle again

		//while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
		if (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0 && !(minAng>-1 && minAng<1))
				minAng += 360.0;
			//this.turnTo(minAng, false);

			//this.turnTo(minAng, true);
			turnToSearch2(odometer.getTheta(), minAng);

			/*if(counter==0){
				if(minAng<0){
					minAng+=360;;
				}
				this.turnTo(minAng, false);
				counter++;
			}*/

			//this.setSpeeds(FAST, FAST);
			// calculates magnitude to travel
			double distance = Math.sqrt(Math.pow((y - odometer.getY()), 2)
					+ Math.pow((x - odometer.getX()), 2));
			goForward(distance);

		}
		this.setSpeeds(0, 0);
	}

	/**
	 * TravelTo bacwards function
	 */
	//public synchronized void travelToBackwards(double x, double y) {
	public void travelToBackwards(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR
				|| Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX()))
					* (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			// this.setSpeeds(FAST, FAST);
			// calculates magnitude to travel
			double distance = Math.sqrt(Math.pow((y - odometer.getY()), 2)
					+ Math.pow((x - odometer.getX()), 2));
			goBackward(distance);
		}
		this.setSpeeds(0, 0);
	}

	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 */
	//public synchronized void turnTo(double angle, boolean stop) {
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getTheta();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getTheta();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				// Sound.beep();
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				// Sound.beepSequence();
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}

	public void turnToSearch(double angle, boolean stop) {

		leftMotor.setSpeed(FAST);
		rightMotor.setSpeed(FAST);

		double angleToTurn = Odometer.minimumAngleFromTo(
				this.odometer.getTheta(), angle);

		leftMotor.rotate(-convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angleToTurn), true);
		rightMotor.rotate(convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angleToTurn), false);
		Delay.msDelay(20);
	}
	
	
	
	public void turnToSearch1(double angle, boolean stop, boolean clockwiseTurn){
		double angleToTurn;
			if(isSweepMode==true){
				leftMotor.setSpeed(SEARCH_SPEED);
				rightMotor.setSpeed(SEARCH_SPEED);
			}
			else{
				leftMotor.setSpeed(Constants.SLOW);
				rightMotor.setSpeed(Constants.SLOW);
			}
			angleToTurn=Math.abs(angle);
			if(clockwiseTurn==true){
				leftMotor.rotate(convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angleToTurn), true);
				rightMotor.rotate(-convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angleToTurn), false);
			}
			else{
				leftMotor.rotate(-convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angleToTurn), true);
				rightMotor.rotate(convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angleToTurn), false);
			}
	}
	

	public void turnToSearch2(double odometerAngle,double destinationAngle){
		double angle=odometer.fixDegAngle(odometerAngle-destinationAngle);
		leftMotor.setSpeed(Constants.SLOW);
		rightMotor.setSpeed(Constants.SLOW);
		if(angle<180){
			leftMotor.rotate(convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angle), true);
			rightMotor.rotate(-convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, angle), false);
		}
		else{
			leftMotor.rotate(-convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, (360-angle)), true);
			rightMotor.rotate(convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, (360-angle)), false);
		}
	}



	/**
	 * Keep on going forward - This method is expected to be followed by another operation 
	 * that is supposed to stop the motors when a certain condition is met 
	 */
	//public synchronized void goForward() {
	public void goForward() {
		if(isSearchingBlock==true){
			leftMotor.setSpeed(FAST);
			rightMotor.setSpeed(FAST);
		}
		else{
			leftMotor.setSpeed(SLOW);
			rightMotor.setSpeed(SLOW);
		}
		leftMotor.forward();
		rightMotor.forward();
		isNavigating = true;
	}


	/**
	 * Go foward a set distance in cm
	 * 
	 * @param distance
	 *            the distance with which to move forward
	 */
	//public synchronized void goForward(double distance) {
	public void goForward(double distance) {
		if(isSearchingBlock==true){
			leftMotor.setSpeed(SLOW);
			rightMotor.setSpeed(SLOW);
		}
		else{
			leftMotor.setSpeed(SLOW);
			rightMotor.setSpeed(SLOW);
		}
		isNavigating = true;

		leftMotor.rotate(convertDistance(RADIUS, distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), false);

		isNavigating = false;
	}

	//public synchronized void goForward(double distance, boolean returnImmediately) {
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
	 * 
	 * @param distance
	 *            the distance with which to move backward
	 */
	//public synchronized void goBackward(double distance) {
	public void goBackward(double distance) {
		if(isSearchingBlock==true){
			leftMotor.setSpeed(FAST);
			rightMotor.setSpeed(FAST);
		}
		else{
			leftMotor.setSpeed(SLOW);
			rightMotor.setSpeed(SLOW);
		}
		//Sound.beep();
		isNavigating = true;

		leftMotor.rotate(-convertDistance(RADIUS, distance), true);
		rightMotor.rotate(-convertDistance(RADIUS, distance), false);

		isNavigating = false;
	}

	/**
	 * Motor Setters (FOR ROTATING)
	 * 
	 * @param speed
	 *            the speed at which the robot should be rotating
	 */
	//public synchronized void setRotationSpeed(float speed) {
	public void setRotationSpeed(float speed) {
		rotationSpeed = speed;
		setSpeeds(rotationSpeed, -rotationSpeed);
	}

	/**
	 * Make the robot perform a sweeping motion
	 * 
	 * @param forward
	 *            whether the robot sweeps forward or backward
	 */
	//public synchronized void search(boolean forward) {
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

	//public synchronized static int convertDistance(double radius, double distance) {
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	//public synchronized static int convertAngle(double radius, double width, double angle) {
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public boolean isNavigating() {
		return this.isNavigating;
	}

	//######### CORRECTIVE NAVIGATION ########## --- Mahmood
	//public synchronized void travelToWithCorrection(double x, double y, double theta){
	public void travelToWithCorrection(double x, double y, double theta)
	{
		Delay.msDelay(10);
		odometer.getLeftMotor().setSpeed(Constants.SLOW);
		odometer.getRightMotor().setSpeed(Constants.SLOW);

		odometer.getLeftMotor().forward();
		odometer.getRightMotor().forward();

		turnToSearch(theta, true);

		Sound.buzz();
		// Heading EAST (y++)
		if (theta >= 45 && theta < 135) {
			while (odometer.getY() < y) {
				setSpeeds(Constants.SLOW, Constants.SLOW);
				// performBlackLineDetection();
				if (isBlacklineDetected()) {
					performOdometerCorrection();
					goForward(6);
				}

			}
			System.out.println("1 tile moved");
			Delay.msDelay(20);
			stopMoving();
		}
		else if(theta >= 135 && theta < 225)
		{
			while(odometer.getX() > x)
			{
				setSpeeds(Constants.SLOW,Constants.SLOW);
				//				performBlackLineDetection();
				if (isBlacklineDetected()){
				performOdometerCorrection();
				goForward(2);
				}
			}
			System.out.println("1 tile moved");
			Delay.msDelay(20);
			stopMoving();
		}
		// Heading WEST (y--)

		else if(theta >= 225 && theta < 315)
		{
			while(odometer.getY() > y )
			{
				setSpeeds(Constants.SLOW,Constants.SLOW);
				//				performBlackLineDetection();
				if (isBlacklineDetected()){
				performOdometerCorrection();
				goForward(2);
				}

			}
			System.out.println("1 tile moved");
			Delay.msDelay(20);
			stopMoving();
		}
		// Heading NORTH (x++)
		else {

			while (odometer.getX() < x) {
				setSpeeds(Constants.SLOW, Constants.SLOW);
				// performBlackLineDetection();
				if (isBlacklineDetected()) {
					performOdometerCorrection();
					goForward(6);
				}

			}
			System.out.println("1 tile moved");
			Delay.msDelay(20);
			stopMoving();
		}
		Delay.msDelay(500);
	}

	//public synchronized boolean isBlacklineDetected(){
	public boolean isBlacklineDetected(){
		double rightReading1,rightReading2,leftReading1,leftReading2;
		rightReading1=rightLS.scan();
		Delay.msDelay(50);
		rightReading2=rightLS.scan();

		leftReading1=leftLS.scan();
		Delay.msDelay(50);
		leftReading2=leftLS.scan();

		scanRight=scanFilter(rightReading1,rightReading2);
		scanLeft=scanFilter(leftReading1,leftReading2);

		isBlackLineDetected = scanRight || scanLeft;


		if(scanRight==true && scanLeft==true){
			//do nothing
			Sound.beepSequence();
			Delay.msDelay(500);
			return isBlackLineDetected;
		}

		else if(scanRight==true){
			//turn clockwise leftMotor only
			while(scanLeft==false){
				setSpeeds(Constants.ROTATE_SPEED,0);
				leftReading1=leftLS.scan();
				Delay.msDelay(50);
				leftReading2=leftLS.scan();
				scanLeft=scanFilter(leftReading1,leftReading2);
			}
			Sound.beep();
			setSpeeds(Constants.SLOW, Constants.SLOW);
			//			Delay.msDelay(1000);
			return isBlackLineDetected;

		}

		else if(scanLeft==true){
			//turn counterclockwise rightMotor only
			while(scanRight==false){
				setSpeeds(0,Constants.ROTATE_SPEED);
				rightReading1=rightLS.scan();
				Delay.msDelay(50);
				rightReading2=rightLS.scan();
				scanRight=scanFilter(rightReading1,rightReading2);
			}
			Sound.beep();
			setSpeeds(Constants.SLOW, Constants.SLOW);
			return true;
			//			Delay.msDelay(1000);
		}
		return isBlackLineDetected;

	}





	public boolean scanFilter(double average1,double average2){
		if(average1< Constants.LIGHT_THRESHOLD || average2< Constants.LIGHT_THRESHOLD){
			return true;
		} else {
			return false;
		}

	}

	// ###### ODOMETRY CORRECTION ########
	// this could have been simpler but I needed to keep count of gridline
	// number - Mahmood
	private void performOdometerCorrection() {
		// Updates the x, y and theta values on the Odometer according to the
		// correction
		double heading = odometer.getTheta();

		// Heading EAST (y++)
		if (heading >= 45 && heading < 135) {
			double yActual = odometer.getY()
					+ Math.cos(odometer.getTheta()
							* Constants.LIGHT_SENS_OFFSET);
			double correctionY = 0;
			if (horizontalLinesCrossed != 1) {
				if (yActual > (horizontalLinesCrossed * Constants.SQUARE_WIDTH + Math
						.cos(odometer.getTheta() * Constants.LIGHT_SENS_OFFSET))) {
					correctionY = -(yActual % 30.48);
				} else {
					correctionY = 30.48 - (yActual % 30.48);
				}
			}
			horizontalLinesCrossed++;
			odometer.setPosition(
					new double[] { 0.0, correctionY + odometer.getY(), 90.0 },
					new boolean[] { false, true, true });
		}
		// Heading SOUTH (x--)
		else if (heading >= 135 && heading < 225) {
			double correctionX = 0;
			double xActual = odometer.getX()
					+ Math.sin(odometer.getTheta()
							* Constants.LIGHT_SENS_OFFSET);
			if (verticalLinesCrossed != 1) {
				if (xActual > (verticalLinesCrossed * Constants.SQUARE_WIDTH + Math
						.sin(odometer.getTheta() * Constants.LIGHT_SENS_OFFSET))) {
					correctionX = -(xActual % 30.48);
				} else {
					correctionX = 30.48 - (xActual % 30.48);
				}
			}
			verticalLinesCrossed--;
			// System.out.println("Curr X:"+verticalLinesCrossed);
			odometer.setPosition(new double[] { correctionX + odometer.getX(),
					0.0, 180.0 }, new boolean[] { true, false, true });

		}
		// Heading WEST (y--)
		else if (heading >= 225 && heading < 315) {
			double yActual = odometer.getY()
					+ Math.cos(odometer.getTheta()
							* Constants.LIGHT_SENS_OFFSET);
			double correctionY = 0;
			if (horizontalLinesCrossed != 1) {
				if (yActual > (horizontalLinesCrossed * Constants.SQUARE_WIDTH + Math
						.cos(odometer.getTheta() * Constants.LIGHT_SENS_OFFSET))) {
					correctionY = -(yActual % 30.48);
				} else {
					correctionY = 30.48 - (yActual % 30.48);
				}
			}
			horizontalLinesCrossed--;
			odometer.setPosition(
					new double[] { 0.0, correctionY + odometer.getY(), 270.0 },
					new boolean[] { false, true, true });

		}
		// Heading NORTH (x++)
		else {
			double correctionX = 0;
			double xActual = odometer.getX()
					+ Math.sin(odometer.getTheta()
							* Constants.LIGHT_SENS_OFFSET);
			if (verticalLinesCrossed != 1) {
				if (xActual > (verticalLinesCrossed * Constants.SQUARE_WIDTH + Math
						.sin(odometer.getTheta() * Constants.LIGHT_SENS_OFFSET))) {
					correctionX = -(xActual % 30.48);
				} else {
					correctionX = 30.48 - (xActual % 30.48);
				}
			}
			verticalLinesCrossed++;
			odometer.setPosition(new double[] { correctionX + odometer.getX(),
					0.0, 0.0 }, new boolean[] { true, false, true });
		}

	}

	public void clawUp() {
		clawMotor.backward();
		clawMotor.setSpeed(150);
		clawMotor.rotate(180);
		Sound.beep();
		Delay.msDelay(250);
		Sound.beepSequence();
		clawMotor.stop();
	}

	public void clawDown() {
		clawMotor.forward();
		clawMotor.setSpeed(150);
		clawMotor.rotate(-150);
		Sound.beep();
		Delay.msDelay(250);
		Sound.beepSequence();
		clawMotor.stop();
	}

    public void armOpen() {
        goForward(3);
        armMotor.forward();
        armMotor.setSpeed(150);
        armMotor.rotate(-120);
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
    
    public void capture(){
    	
    	grab();
    	Delay.msDelay(500);
    	clawUp();
    	Delay.msDelay(500);
    	clawDown();
    	Delay.msDelay(500);
    	armOpen();
    	Delay.msDelay(500);
    	grab();
    	Delay.msDelay(500);
    	clawUp();
    	Delay.msDelay(50);
    }


}
