package robot.navigation;

/**
 * The navigator class is in charge of the motors of the robot. ALL the motors, including the arms to capture blocks,
 * arguably, this is the single most important class in the entire project, all commands sent to the navigator will
 * be executed
 */
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Delay;

public class Navigator {
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private EV3MediumRegulatedMotor armMotor;
	private float rotationSpeed ;
	private double RADIUS = 2.2, TRACK = 11.5;
	private int SEARCH_SPEED = 20;
	private boolean isNavigating;
	public boolean hasStyro = false;

	public Navigator(Odometer odo) {
		this.odometer = odo;

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

	/*
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

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}
	
	public void stopMoving() {																	 		// This method will just stop the motors
		
		this.rightMotor.setSpeed(0);
		this.leftMotor.setSpeed(0);
		this.leftMotor.forward();
		this.rightMotor.forward();
	}


	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
		//	this.setSpeeds(FAST, FAST);
			//calculates magnitude to travel
			double distance  = Math.sqrt(Math.pow((y-odometer.getY()), 2) + Math.pow((x-odometer.getX()),2));
			goForward(distance);
		}
		this.setSpeeds(0, 0);
	}
	
	public void travelToBackwards(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
		//	this.setSpeeds(FAST, FAST);
			//calculates magnitude to travel
			double distance  = Math.sqrt(Math.pow((y-odometer.getY()), 2) + Math.pow((x-odometer.getX()),2));
			goBackward(distance);
		}
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
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
	
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance){
		;
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		isNavigating = true;
		
		leftMotor.rotate(convertDistance(RADIUS, distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), true);
		
		isNavigating = false;
	}
	public void goForward(double distance, boolean returnImmediately){
		
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		isNavigating = true;
		
		leftMotor.rotate(convertDistance(RADIUS, distance), true);
		rightMotor.rotate(convertDistance(RADIUS, distance), returnImmediately);
		
		isNavigating = false;
	}
	/*
	 * Go Backward a set distance in cm
	 */
	public void goBackward(double distance){

		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		Sound.beep();
		isNavigating = true;
		
		leftMotor.rotate(-convertDistance(RADIUS, distance), true);
		rightMotor.rotate(-convertDistance(RADIUS, distance), false);
		
		isNavigating = false;
	}
	
	//Motor Setters (FOR ROTATING)
	public void setRotationSpeed(float speed) {
		rotationSpeed = speed;
		setSpeeds(rotationSpeed, -rotationSpeed);
		
	}
	// Takes a sweep to detect a block (controlled by main)
	public void search (boolean forward){
		leftMotor.setSpeed(SEARCH_SPEED);
		rightMotor.setSpeed(SEARCH_SPEED);
		if (forward){
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
	// arm motor will grab the block
	public void grab(){
		this.goForward(3);
		armMotor.backward();
		armMotor.setSpeed(150);
		armMotor.rotate(180);
		Delay.msDelay(250);
		armMotor.stop();
		this.hasStyro = true;
	}
	
	public boolean hasStyro() {
		boolean foo;
		synchronized(this){ foo = hasStyro;}
		return foo;
	}
	
	public boolean isNavigating(){
		return this.isNavigating;
	}
	
	
	
	
	
	
	
}

