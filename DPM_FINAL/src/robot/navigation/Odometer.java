/*
 * File: Odometer.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Class which controls the odometer for the robot
 * 
 * Odometer defines cooridinate system as such...
 * 
 * 					90Deg:pos y-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 180Deg:neg x-axis------------------0Deg:pos x-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 					270Deg:neg y-axis
 * 
 * The odometer is initalized to 90 degrees, assuming the robot is facing up the positive y-axis
 * 
 */
package robot.navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import robot.constants.Constants;
import robot.constants.Position;

/**
 * This class represents the odometer of our robot, will always run as a thread as long as the robot is playing the game
 */
public class Odometer implements TimerListener {

    /**
     * This is a singleton class
     */
    private static final Odometer ourInstance = new Odometer(30, true);
    public static Odometer getInstance() {
        return ourInstance;
    }


    private Timer timer;
    private EV3LargeRegulatedMotor leftMotor, rightMotor, clawMotor;
    private EV3MediumRegulatedMotor armMotor;
    private final int DEFAULT_TIMEOUT_PERIOD = 120;
    private double leftRadius, rightRadius, TRACK;
    private double x, y, theta;
    private double[] oldDH, dDH;
    private Position position;

    /**
     * The constructor for the odometer
     * @param INTERVAL the interval period for updating the odometer
     * @param autostart whether the thread starts from the moment this class is instantiated
     */
    private Odometer(int INTERVAL, boolean autostart) {
        Motors mtrs = Motors.getInstance();
        this.leftMotor = mtrs.getLeftMotor();
        this.rightMotor = mtrs.getRightMotor();
        this.armMotor = mtrs.getArmMotor();
        this.clawMotor = mtrs.getClawMotor();

        // default values, modify for your robot
        this.rightRadius = Constants.WHEEL_RADIUS;
        this.leftRadius = Constants.WHEEL_RADIUS;
        this.TRACK = Constants.TRACK;

        this.x = 0.0;
        this.y = 0.0;
        this.theta = 90.0;				//changed this back to 90 !
        this.oldDH = new double[2];
        this.dDH = new double[2];
        this.position=new Position(0,0,90);
        
        if (autostart) {
            // if the timeout interval is given as <= 0, default to 20ms timeout
            this.timer = new Timer((INTERVAL <= 0) ? INTERVAL : DEFAULT_TIMEOUT_PERIOD, this);
            this.timer.start();
        } else
            this.timer = null;
    }

    /**
     * functions to start/stop the timerlistener
     */
    public void stop() {
        if (this.timer != null)
            this.timer.stop();
    }

    public void start() {
        if (this.timer != null)
            this.timer.start();
    }

    /**
     * Calculates displacement and heading as title suggests
     */
    private void getDisplacementAndHeading(double[] data) {
        int leftTacho, rightTacho;
        leftTacho = leftMotor.getTachoCount();
        rightTacho = rightMotor.getTachoCount();

        data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI / 360.0;
        data[1] = (rightTacho * rightRadius - leftTacho * leftRadius) / TRACK;
    }

    /**
     * Recompute the odometer values using the displacement and heading changes
     */
    public void timedOut() {
        
        // update the position in a critical region
        synchronized (this) {
        	this.getDisplacementAndHeading(dDH);
            dDH[0] -= oldDH[0];
            dDH[1] -= oldDH[1];
        	
            theta += dDH[1];
            theta = fixDegAngle(theta);

            x += dDH[0] * Math.cos(Math.toRadians(theta));
            y += dDH[0] * Math.sin(Math.toRadians(theta));
            position.setX(x);
            position.setY(y);
            position.setTheta(theta);
            
            oldDH[0] += dDH[0];
            oldDH[1] += dDH[1];
        }
    }

    // return X value
    public double getX() {
        synchronized (this) {
            return x;
        }
    }

    // return Y value
    public double getY() {
        synchronized (this) {
            return y;
        }
    }

    // return theta value
    public double getTheta() {
        synchronized (this) {
            return theta;
        }
    }

    public void setPosition(double[] position, boolean[] update) {
        synchronized (this) {
            if (update[0])
                x = position[0];
            if (update[1])
                y = position[1];
            if (update[2])
                theta = position[2];
        }
    }

    public synchronized void setPosition(Position p){
        x = p.getX();
        y = p.getY();
        theta = p.getTheta();
    }

    // UPDATED.. INDIVIDUAL SETTERS
    public void setX(double x) {
        synchronized (this) {
            this.x = x;
        }
    }

    public void setY(double y) {
        synchronized (this) {
            this.y = y;
        }
    }
    public void setTheta(double theta) {
        synchronized (this) {
            this.theta = theta;
        }
    }


    // return x,y,theta
    public void getPosition(double[] position) {
        synchronized (this) {
            position[0] = x;
            position[1] = y;
            position[2] = theta;
        }
    }

    public synchronized Position getPosition() {
        synchronized (this) {
            return this.position;
        }
    }

    // accessors to motors
    public EV3LargeRegulatedMotor[] getMotors() {
        return new EV3LargeRegulatedMotor[]{this.leftMotor, this.rightMotor};
    }

    public EV3MediumRegulatedMotor getArm() {
        return this.armMotor;
    }
    
    public EV3LargeRegulatedMotor getClawMotor() {
        return this.clawMotor;
    }

    public EV3LargeRegulatedMotor getLeftMotor() {
        return this.leftMotor;
    }

    public EV3LargeRegulatedMotor getRightMotor() {
        return this.rightMotor;
    }

    /**
     *  This method adds the USlocalizer's deltatheta to the current odometer theta to correct it
     * @param theta theta that we want to correct
     */
    public void correctTheta(final double theta) {
        synchronized (this) {
            this.theta += theta;

            while (this.theta < 0)
                this.theta += 360;                                // ensures WRAPROUND (angle does not go out of bounds)
            while (this.theta > 360) this.theta -= 360;
        }
    }

    // static 'helper' methods
    public static double fixDegAngle(double angle) {
        if (angle < 0.0)
            angle = 360.0 + (angle % 360.0);

        return angle % 360.0;
    }

    public static double minimumAngleFromTo(double a, double b) {
        double d = fixDegAngle(b - a);

        if (d < 180.0)
            return d;
        else
            return d - 360.0;
    }

}

