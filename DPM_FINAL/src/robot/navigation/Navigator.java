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
 * The navigator class is in charge of the motors of the robot. ALL the motors, including the arms to capture blocks,
 * arguably, this is the single most important class in the entire project, all commands sent to the navigator will
 * be executed
 */
public class Navigator implements Runnable {

    //this is a singleton class
    private static Navigator ourInstance = new Navigator();
    public static Navigator getInstance(){return ourInstance;}


    // Motors
    private static EV3LargeRegulatedMotor rightMotor = Motors.getInstance().getRightMotor();
    private static EV3LargeRegulatedMotor leftMotor = Motors.getInstance().getLeftMotor();

    // class variables
    private static Odometer odometer = Odometer.getInstance();
    private static USSensor us = USSensor.getInstance();
    private static boolean collisionDetectionEnabled = false;
    private static boolean isNavigating = true; //Always true to start
    private static boolean movingInX;
    private static boolean movingInY;

    // lock object
    private Object lock = new Object();

    /**
     * Queue that holds the instructions to execute! ie: the locations that we
     * need to navigate to (it's a blocking queue so we can update it from
     *outside classes)
     */
    private static BlockingQueue<NavInstruction> instructions = new LinkedBlockingQueue<NavInstruction>();

    // Double array that holds the current goal ie: the position that we want to
    private static NavInstruction goal;

    // NavVector that holds our current position
    private static Position currentPosition;

    /**
     * This is the main navigation loop for the robot, as it moves, the loop will run always!
     * If there are no places to go to, then the thread will wait until something notifies it to continue (assuming
     * that whatever notifies it adds more information to the blocking queue before resuming the thread)
     * Another thread MUST call the notify() function for this thread to be activated
     */
    public void run(){

        while (isNavigating) {
            currentPosition = odometer.getPosition();

            //First check for a collision avoidance routine
            if (isUpcomingColision()) {
                stopMoving();
                avoidCollision();
            }

            // If we haven't reached the goal yet, then continue to 'travelto' the goal
            if (isRobotAtDestination(currentPosition)) {
                // Stop the motors, we're at the location!
                stopMoving();

                //Check to see if there is another instruction, calling fetch will take instruction from queue
                if (fetchNextInstruction()==false){
                    //Then we are done traveling all together, wait until next instruction given
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                travelTo(goal,currentPosition);
            }

            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method will determine whether or not there is an object along the robots path
     * @return must return whether we are about to collide with an object or not based on the us sensor
     */
    private boolean isUpcomingColision() {



        return false;
    }

    /**
     *
     * @return a boolean indicating if there is an instruction to fetch or not
     */
    private boolean fetchNextInstruction(){
        try {
            synchronized (this) {
                if (instructions.size() > 1) {
                    this.goal = instructions.take();
                    return true;
                } else {
                    return false;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false; //Just in case, return false
    }

    /**
     *
     * @param currentPosition pass in our current position
     * @return whether or not we are there yet
     */
    private boolean isRobotAtDestination(Position currentPosition){
        // calculate distance from the goal
        double error;
        if(goal.movingInX){
            error = goal.coordinate - currentPosition.getX();
        }else{
            error = goal.coordinate - currentPosition.getY();
        }

        if (Math.abs(error) > Constants.THRESHOLD_DISTANCE_ERROR) {
            return false;
        }else{
            return true;
        }
    }

    public void stopMoving() {																	 		// This method will just stop the motors

        rightMotor.setSpeed(0);
        leftMotor.setSpeed(0);
        leftMotor.forward();
        rightMotor.forward();
    }

    /**
     * This method will be called when the robot needs to avoid a collision
     */
    public void avoidCollision() {

        Sound.twoBeeps(); // To show we are running this specific method
    }

    private int convertDistance(double radius, double distance) {
        // tells robot how much to move forward
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }

    private int convertAngle(double radius, double width, double angle) {
        // tells robot how much it should turn in degrees.
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }

    /**
     * This method will move our robot in either the x or y direction, but not both,
     * @param goal , the goal that we need to travel towards
     * As the robot is travelling, odometry correction should be working.
     */
    public void travelTo(NavInstruction goal,Position currentPosition) {

        if (goal.movingInX){
            this.movingInX = true;
            if(currentPosition.getX()<goal.coordinate){
                turnTo(0);
            }else{
                turnTo(180);
            }
        }else{
            this.movingInX = true;
            if (currentPosition.getY()<goal.coordinate){
                turnTo(90);
            }else{
                turnTo(270);
            }
        }

        // robot will travel straight towards heading achieved by turnTo method
        leftMotor.setSpeed(Constants.MOTOR_STRAIGHT);
        rightMotor.setSpeed(Constants.MOTOR_STRAIGHT);
        leftMotor.forward();
        rightMotor.forward();

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

    public void turnTo(double theta) {


        rightMotor.setSpeed(Constants.ROTATE_SPEED);												// prepare to rotate
        leftMotor.setSpeed(Constants.ROTATE_SPEED);

        double changeInT = changeInTheta(theta);													// determine wheel directions for rotations based on angle

        if ((changeInT > 0 && changeInT < Math.PI) || (changeInT < 0 && changeInT < -Math.PI)) {	// Only head to theta E [-PI,PI]
            leftMotor.forward();
            rightMotor.backward();
        } else {
            leftMotor.backward();
            rightMotor.forward();
        }
        // if theta was not within angle error threshold
        while (Math.abs(changeInT) > Constants.THRESHOLD_ERROR) {									// Redo the calculation (always head with correct theta)
            Button.LEDPattern(3); 																	// debugging tool..
            changeInT = changeInTheta(theta); 														// REDO
        }
    }
    //This method returns the minimal desired angle to heading
    public double changeInTheta(double angle) {
        double minAngle = angle - odometer.getAng();

        if (minAngle < Math.PI && minAngle > -Math.PI)												// the statements make sure the calculated angle is between PI and -PI
            minAngle = minAngle;																	// so that the robot takes ONLY the MINIMAL angle
        else if (minAngle > Math.PI) {
            minAngle = minAngle - 2 * Math.PI;
        } else if (minAngle < -Math.PI) {
            minAngle = minAngle + 2 * Math.PI;
        }

        return minAngle;
    }

    //Getters and setters for the positions

    public synchronized void addInstructions(NavInstruction instruction){
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