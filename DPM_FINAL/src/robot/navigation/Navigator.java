package robot.navigation;

/**
 * The navigator class is in charge of the motors of the robot. ALL the motors, including the arms to capture blocks,
 * arguably, this is the single most important class in the entire project, all commands sent to the navigator will
 * be executed
 */
/*
 * This class will actually be controlling the navigation, odometer and collision detection of the robot
 *
 * We have to be able to interupt the navigator if say there's an object in the way
 *
 */




import java.util.Timer;
import java.util.concurrent.BlockingQueue;

import robot.constants.Constants;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import robot.sensors.USSensor;

public class Navigator implements Runnable {
    // Motors
    private static EV3LargeRegulatedMotor rightMotor = Motors.getInstance().getRightMotor();
    private static EV3LargeRegulatedMotor leftMotor = Motors.getInstance().getLeftMotor();

    // class variables
    private static boolean isNavigating = true;
    private static Odometer odometer = new Odometer();
    private static USSensor us;
    private static boolean collisionDetected = false;
    private static boolean collisionDetectionEnabled = false;
    private static int numIterations = 0;
    private static final TextLCD t = LocalEV3.get().getTextLCD();
    // lock object
    private Object lock = new Object();

    // Queue that holds the instructions to execute! ie: the locations that we
    // need to navigate to (it's a blocking queue so we can update it from
    // outside classes)
    private static BlockingQueue<NavVector> instructions;

    // Double array that holds the current goal ie: the position that we want to
    // go to
    private static NavVector goal;

    // NavVector that holds our current position
    private static NavVector currentPosition;

    // Constructor for the object
    public Navigator(BlockingQueue<NavVector> q) {
        instructions = q;// This takes the instructions given when the
        try {
            // Take the first task and assign it as the goal to accomplish
            goal = instructions.take(); // Convert first instruction to the first goal!
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // This is the launch method of the navigator thread
    public void run() {
        int distFront;

        // first start odometer thread, and collisionDetection thread
        startOdometer();
        startCollisionDetection();

        while (isNavigating) {

            // Get information to act on! Odometer, + US
            distFront = getUSReading();											// Will check for imminent collisions
            currentPosition = odometer.getReading();

            // Display the readings for debugging purposes
            displayNavData(currentPosition, goal, distFront);

            if (collisionDetected) { 											// Only ever (could be) true if collision detection
                stopMoving();
                avoidCollision();												// Once is avoided, robot will now head to it's desired goal normally (waypoints)
            }

            // calculate distance from the goal
            double errorX = goal.getX() - odometer.getX();
            double errorY = goal.getY() - odometer.getY();

            // If we haven't reached the goal yet, then continue to 'travelto' the goal
            if (Math.abs(errorX) > Constants.THRESHOLD_DISTANCE_ERROR
                    || Math.abs(errorY) > Constants.THRESHOLD_DISTANCE_ERROR) {
                this.travelTo(goal.getX(), goal.getY());
            } else {
                // Stop the motors, we're at the location!
                stopMoving();
                // Time for the next instruction! (not for purposes of this Lab! We're looking ahead here)
                try {
                    if (instructions.size() < 1) {
                        Button.LEDPattern(2);									// Then we have no instructions left!
                        isNavigating = false;									// i.e. No longer Navigating
                    } else {
                        // if we were to take another instruction , Take the instruction and set it as our goal
                        this.goal = instructions.take();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
            numIterations++;													//debugging tool
        }
    }

    public void startOdometer() {
        new Thread(odometer).start();
    }

    public void startCollisionDetection() {
        // If collision detection is enabled, then start that thread as well
        if (collisionDetectionEnabled) {
            colDetect = new CollisionDetector(Constants.getUsdistance(), Constants.getUsdata());
            new Thread(colDetect).start();
        }
    }

    public void stopMoving() {																	 		// This method will just stop the motors

        rightMotor.setSpeed(0);
        leftMotor.setSpeed(0);
        leftMotor.forward();
        rightMotor.forward();
    }

    // This method gets called when the US senses an imminent collision.
    public void avoidCollision() {																		// The method will allow the robot to avoid an object

        Sound.twoBeeps(); // To show we are running this specific method
        Button.LEDPattern(7);
        while(getUSReading()<Constants.USWARNINGDISTANCE){

          
        }
        //We should now be good to rotate back towards our initial heading
        //turnTo(goal.getTheta());
        //travelTo(goal.getX(),goal.getY());

        // At the end of this method, object has been dealt with
        collisionDetected = false;																    	// Continue with navigation normally !
    }

    private int convertDistance(double radius, double distance) {
        // tells robot how much to move forward
        return (int) ((180.0 * distance) / (Math.PI * radius));
    }

    private int convertAngle(double radius, double width, double angle) {
        // tells robot how much it should turn in degrees.
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }

    /* This method will move our robot to the x and y position */
    public void travelTo(double x, double y) {
        double DTheta = calcAngle(x, y); // pass x and y values to calculate the
        // angle

        turnTo(DTheta); // pass angle calculated to turnTo --> robot rotates to
        // correct heading

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

    public boolean isNavigating() {
        return isNavigating;
    }

    public int getUSReading() {
        // This method gets the ultrasonic sensor reading
        if (this.isCollisionDetectionEnabled()) {
            int distance = colDetect.getDistance();

            if (colDetect.getNumCollisionDetected() > Constants.MAX_ALARM_READINGS) {
                // After so many alarming readings, we really need to invoke the
                // collision avoidance routine
                this.setCollisionDetected(true);
                // Reset the collisionDetection mechanism
                colDetect.setNumCollisionDetected(0);
            } else
                this.setCollisionDetected(false);

            return distance;
        } else {
            return Constants.STANDARDULTRASONICDIST;
        }

    }

    // Getter and Setter Methods
    public static boolean isCollisionDetected() {
        return collisionDetected;
    }

    public static void setCollisionDetected(boolean collisionDetected) {
        Navigator.collisionDetected = collisionDetected;
    }

    public static boolean isCollisionDetectionEnabled() {
        return collisionDetectionEnabled;
    }

    public static void setCollisionDetectionEnabled(boolean collisionDetectionEnabled) {
        Navigator.collisionDetectionEnabled = collisionDetectionEnabled;
    }

    public static void setNavigating(boolean isNavigating) {
        Navigator.isNavigating = isNavigating;
    }

}