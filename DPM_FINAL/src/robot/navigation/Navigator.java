package robot.navigation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import robot.constants.Constants;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import robot.constants.NavInstruction;
import robot.constants.Position;
import robot.sensors.USSensor;

/**
 * The navigator class is in charge of the motors of the robot. ALL the motors,
 * including the arms to capture blocks, arguably, this is the single most
 * important class in the entire project, all commands sent to the navigator
 * will be executed
 */
public class Navigator implements Runnable {

	// this is a singleton class
	private static Navigator ourInstance = new Navigator();

	public static Navigator getInstance() {
		return ourInstance;
	}

	// Motors
	private static EV3LargeRegulatedMotor rightMotor = Motors.getInstance().getRightMotor();
	private static EV3LargeRegulatedMotor leftMotor = Motors.getInstance().getLeftMotor();

	// class variables
	private static Odometer odometer = Odometer.getInstance();
	private static USSensor us = USSensor.getInstance();
	private static boolean collisionDetectionEnabled = false;
	private static boolean isNavigating = true; // Always true to start
	private static boolean movingInX;
	private static boolean movingInY;
	private float rotationSpeed;
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	final static double DEG_ERR = 10, CM_ERR = 1.0;
	private double RADIUS = Constants.WHEEL_RADIUS;
	private double TRACK = Constants.TRACK;
	private int SEARCH_SPEED = 20;

	// lock object
	private Object lock = new Object();

	/**
	 * Queue that holds the instructions to execute! ie: the locations that we
	 * need to navigate to (it's a blocking queue so we can update it from
	 * outside classes)
	 */
	private static BlockingQueue<NavInstruction> instructions = new LinkedBlockingQueue<NavInstruction>();

	// Double array that holds the current goal ie: the position that we want to
	private static NavInstruction goal;

	// NavVector that holds our current position
	private static Position currentPosition;

	/**
	 * This is the main navigation loop for the robot, as it moves, the loop
	 * will run always! If there are no places to go to, then the thread will
	 * wait until something notifies it to continue (assuming that whatever
	 * notifies it adds more information to the blocking queue before resuming
	 * the thread) Another thread MUST call the notify() function for this
	 * thread to be activated
	 */
	public void run() {
		if (goal == null) {
			fetchNextInstruction();
		}

		while (isNavigating) {
			//currentPosition = odometer.getPosition();

			// First check for a collision avoidance routine
			if (isUpcomingColision()) {
				stopMoving();
				avoidCollision();
			}

			// If we haven't reached the goal yet, then continue to goal
			if (isRobotAtDestination(currentPosition)) {
				// Stop the motors, we're at the location!
				stopMoving();

				// Check to see if there is another instruction, calling fetch
				if (fetchNextInstruction() == false) {
					// Then we are done traveling all together, wait until next instruction
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} else {
				//We need to travel to one of the goals we've set
				if(goal.movingInX){
					travelTo(goal.coordinate,0);
				}else{
					travelTo(0,goal.coordinate);
				}
			}

			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * This method will determine whether or not there is an object along the
	 * robots path
	 * 
	 * @return must return whether we are about to collide with an object or not
	 *         based on the us sensor
	 */
	private boolean isUpcomingColision() {

		return false;
	}

	/**
	 *
	 * @return a boolean indicating if there is an instruction to fetch or not
	 */
	private boolean fetchNextInstruction() {
		try {
			synchronized (this) {
				if (instructions.size() > 1) {
					Navigator.goal = instructions.take();
					return true;
				} else {
					return false;
				}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false; // Just in case, return false
	}

	/**
	 *
	 * @param currentPosition
	 *            pass in our current position
	 * @return whether or not we are there yet
	 */
	private boolean isRobotAtDestination(Position currentPosition) {
		// calculate distance from the goal
		double error;
		if (goal.movingInX) {
			error = goal.coordinate - currentPosition.getX();
		} else {
			error = goal.coordinate - currentPosition.getY();
		}

		if (Math.abs(error) > Constants.THRESHOLD_DISTANCE_ERROR) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * This method will be called when the robot needs to avoid a collision
	 */
	public void avoidCollision() {

		Sound.twoBeeps(); // To show we are running this specific method
	}

	private int convertAngle(double radius, double width, double angle) {
		// tells robot how much it should turn in degrees.
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public double calcAngle(double x, double y) {
		// calculating changes in X and Y
		double dX = x - odometer.getX();
		double dY = y - odometer.getY();
		double angle; // angle to be calculated initialized

		// the following statements ensures that the calculated angle is between
		// PI and -PI.
		if (dY >= 0) {
			angle = Math.atan(dX / dY);
		} else if (dY <= 0 && dX >= 0) {
			angle = Math.atan(dX / dY) + Math.PI;
		} else {
			angle = Math.atan(dX / dY) - Math.PI;
		}

		return angle;
	}

	/*
	 * Functions to set the motor speeds jointly
	 */

	public void setSpeeds(int lSpd, int rSpd) {
		Navigator.leftMotor.setSpeed(lSpd);
		Navigator.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			Navigator.leftMotor.backward();
		else
			Navigator.leftMotor.forward();
		if (rSpd < 0)
			Navigator.rightMotor.backward();
		else
			Navigator.rightMotor.forward();
	}

	public void setSpeeds(float lSpd, float rSpd) {
		Navigator.leftMotor.setSpeed(lSpd);
		Navigator.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			Navigator.leftMotor.backward();
		else
			Navigator.leftMotor.forward();
		if (rSpd < 0)
			Navigator.rightMotor.backward();
		else
			Navigator.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		Navigator.leftMotor.stop();
		Navigator.rightMotor.stop();
		Navigator.leftMotor.flt(true);
		Navigator.rightMotor.flt(true);
	}

	public void stopMoving() { // This method will just stop the motors

		Navigator.rightMotor.setSpeed(0);
		Navigator.leftMotor.setSpeed(0);
		Navigator.leftMotor.forward();
		Navigator.rightMotor.forward();
	}

	/*
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

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean
	 * controls whether or not to stop the motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - Navigator.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - Navigator.odometer.getAng();

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
	public void goForward(double distance) {
		;
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

	/*
	 * Go Backward a set distance in cm
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

	// Motor Setters (FOR ROTATING)
	public void setRotationSpeed(float speed) {
		rotationSpeed = speed;
		setSpeeds(rotationSpeed, -rotationSpeed);

	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public boolean isNavigating() {
		return Navigator.isNavigating;
	}

	// Getters and setters for the positions

	public synchronized void addInstructions(NavInstruction instruction) {
		instructions.add(instruction);
	}

	public static boolean isMovingInX() {
		return movingInX;
	}

	public static void setMovingInX(boolean movingInX) {
		Navigator.movingInX = movingInX;
	}

	public static boolean isMovingInY() {
		return movingInY;
	}

	public static void setMovingInY(boolean movingInY) {
		Navigator.movingInY = movingInY;
	}
}

//// Takes a sweep to detect a block (controlled by main)
// public void search (boolean forward){
// leftMotor.setSpeed(SEARCH_SPEED);
// rightMotor.setSpeed(SEARCH_SPEED);
// if (forward){
// leftMotor.forward();
// rightMotor.backward();
// } else {
// leftMotor.backward();
// rightMotor.forward();
// }
// }

//// arm motor will grab the block
// public void grab(){
// this.goForward(3);
// armMotor.backward();
// armMotor.setSpeed(150);
// armMotor.rotate(180);
// Delay.msDelay(250);
// armMotor.stop();
// this.hasStyro = true;
// }

/// **
// * This method will move our robot in either the x or y direction, but not
/// both,
// * @param goal , the goal that we need to travel towards
// * As the robot is travelling, odometry correction should be working.
// */
// public void travelTo(NavInstruction goal,Position currentPosition) {
//
// if (goal.movingInX){
// this.movingInX = true;
// if(currentPosition.getX()<goal.coordinate){
// turnTo(0,true);
// }else{
// turnTo(180,true);
// }
// }else{
// this.movingInX = true;
// if (currentPosition.getY()<goal.coordinate){
// turnTo(90,true);
// }else{
// turnTo(270,true);
// }
// }
//
// // robot will travel straight towards heading achieved by turnTo method
// leftMotor.setSpeed(Constants.MOTOR_STRAIGHT);
// rightMotor.setSpeed(Constants.MOTOR_STRAIGHT);
// leftMotor.forward();
// rightMotor.forward();
//
// }

/*
 * Go foward a set distance in cm
 */
// public void goForward(double distance) {
// this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance,
// Math.cos(Math.toRadians(this.odometer.getAng())) * distance);
//
// }