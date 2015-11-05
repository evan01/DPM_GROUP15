package robot.navigation;
/*
 * Created by evanknox on 2015-11-02.
 */

import robot.constants.Constants;
import robot.sensors.LeftLightSensor;
import robot.sensors.RightLightSensor;

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
        RightLightSensor rs = RightLightSensor.getInstance();
        LeftLightSensor ls = LeftLightSensor.getInstance();
        int intersectionCount = 0;
        int leftCount =0;
        int rightCount = 0;
        boolean scanR,scanL;
        double rightIntersectionAngles[] = new double[4];
        double leftIntersectionAngles[] = new double[4];

        //Start turning left
        Navigation.getInstance().setSpeeds(-50,50);

        //Make sure we get 8 intersections as we rotate!
        while(intersectionCount<8) {
            //check if light sensor detects anything
            scanR = scanRight(rs);
            scanL = scanLeft(ls);
            if (scanR || scanL) {
                if (scanRight(rs)) {
                    rightIntersectionAngles[rightCount] = Odometer.getInstance().getAng();
                    rightCount++;
                    intersectionCount++;
                }
                if (scanLeft(ls)) {
                    leftIntersectionAngles[leftCount] = Odometer.getInstance().getAng();
                    leftCount++;
                    intersectionCount++;
                }
            }
        }
        //Stop the motors
        Navigation.getInstance().stopMoving();
        
        //Get actual angles, just the difference between both arrays.
        double angles[] = getActualAngles(leftIntersectionAngles,rightIntersectionAngles);
        //Do the calculations and update the position
        updateXY(angles);
        updateTheta(angles);

        //Robot now knows it's x and y position and is facing at a calculated 0 degrees
        travelToStartPosition();
    }


    /**
     * This method simply takes the average of the angles measured by the left and right sensors and finds the angle between them,
     * Which is the actual angle with which we want to calculate the odometer values.
     * @param leftIntersectionAngles array of angles stored when left light sensor crossed a black line
     * @param rightIntersectionAngles array of angles stored when the right sensor crossed the black line
     * @return a new array with the averaged angles.
     */
    private double[] getActualAngles(double[] leftIntersectionAngles, double[] rightIntersectionAngles) {
        double angles[] = new double[4];
        for (int i=0;i<leftIntersectionAngles.length;i++){
            angles[i] = (rightIntersectionAngles[i]+leftIntersectionAngles[i])/2;
        }
        return angles;
    }


    /**
     * Updates the x and y coordinates of the odometer using the values that we measured
     * @param Angles the array of angles noted when we crossed each black line
     */
    private void updateXY(double[] Angles) {
        //Don't forget that these distances are negative in respect to the origin
        double newX = -1*findXYDistance(Angles[0],Angles[2]);
        double newY = -1*findXYDistance(Angles[1],Angles[3]);

        Odometer.getInstance().setX(newX);
        Odometer.getInstance().setY(newY);
    }

    /**
     * Given two angles, find the distance from either x or y axis
     * @param theta1 first angle recorded
     * @param theta2 second angle
     * @return a double representing the distance from axis
     */

    private double findXYDistance(double theta1, double theta2){
        return Constants.LIGHT_SENS_OFFSET*Math.cos((theta2-theta1)/2);
    }

    /**
     * Rotates the robot to calculated 0, and updates the odometer accordingly
     * @param angles an array of angles containing the black lines that we need to cross
     */
    private void updateTheta(double[] angles) {
        //At thi point in time, robot has left sensor 'over' the black line
        Navigation.getInstance().turnTo(angles[2],true);
        Navigation.getInstance().turnTo((angles[0]-angles[2]/2),true);

        //Robot should be facing 0,0
        Odometer.getInstance().setTheta(0);
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
    private boolean scanRight(RightLightSensor rs){
        return isBlackIntensity(rs.scan());
    }

    /**
     * Check if the lightSensor detected a black line
     * @return
     */
    private boolean scanLeft(LeftLightSensor ls){

        return isBlackIntensity(ls.scan());
    }

    private boolean isBlackIntensity(double val){
        if(val>25)
            return true;
        else
            return false;
    }


}
