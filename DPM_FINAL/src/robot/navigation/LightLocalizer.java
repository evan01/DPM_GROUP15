package robot.navigation;
/*
 * Created by evanknox on 2015-11-02.
 */

import javafx.geometry.Pos;
import robot.constants.Constants;
import robot.constants.Position;
import robot.sensors.LightSensor;

/**
 * this class assumes that the robot is already in the correct spot, and is ready
 * to rotate in order to perform the light localization
 *
 * Assumes that the light sensor thread has already been started and that
 * our robot is facing 30 degrees from x axis
 */
public class LightLocalizer {


    /**
     * Forces the robot to begin the light localization routine
     */
    public void lightLocalize(){
        LightSensor ls = LightSensor.getInstance();
        int intersectionCount = 0;
        double intersectionAngles[] = new double[4];
        boolean lineDetected;

        //Start turning left
        Navigation.getInstance().setSpeeds(-50,50);

        while(intersectionCount<4){
            //check if light sensor detects anything
            if (isBlackLineDetected()){
                intersectionAngles[intersectionCount] = Odometer.getInstance().getAng();
                intersectionCount++;
            }
        }

        //Stop the motors
        Navigation.getInstance().stopMoving();

        //Do the calculations and update the position
        updatePosition(intersectionAngles);

        travelToStartPosition();
    }


    /**
     * Updates the odometer using the values that we measured
     * @param Angles the array of angles noted when we crossed each black line
     */
    private void updatePosition(double[] Angles) {
        double newX = findXYDistance(Angles[0],Angles[2]);
        double newY = findXYDistance(Angles[1],Angles[3]);
        double newTheta = findThetaCorrection(Angles);

        Position position = new Position(newX,newY,newTheta);
        Odometer.getInstance().setPosition(position);

    }

    /**
     * Given two angles, find the distance from either x or y axis
     * @param theta1 first angle recorded
     * @param theta2 second angle
     * @return a double representing the distance from axis
     */
    private double findXYDistance(double theta1, double theta2){
        return Constants.DIST_TO_LIGHT_SENSOR*Math.cos((theta2-theta1)/2);
    }

    /**
     * This is the ammount that we need to correct
     * @param angles
     * @return
     */
    private double findThetaCorrection(double angles[]){
        return 0.0;
    }

    private void travelToStartPosition() {
        //Our odometer should now be recording the 'perfect' values
        Navigation.getInstance().travelTo(0,0);
        Navigation.getInstance().turnTo(0,true);
    }

    /**
     * Check if the lightSensor detected a black line
     * @return
     */
    private boolean isBlackLineDetected(){
        return false;
    }



}
