package robot;
/*
 * Created by evanknox on 2015-10-27.
 */

import robot.constants.Color;
import robot.constants.Position;
import robot.navigation.Capturer;

/**
 * This class represents our actual implementation of the robot class, we may have multiple implementations if
 * a specific implementation fails to pass all of our tests later on. For now, all the methods in this class aren't synchronized
 * although this may change...
 */
public class GameRobot implements Robot {

    public void moveTo(double x, double y) {

    }

    public void turnTo(int angle) {

    	
    }

    public int usScan() {
        return 0;
    }

    public Color colorScan() {
        return null;
    }

    public void capture() {
//        Capturer.capture();
    }

    public void localize() {

    }

	public Position getOdometerReading() {
		return null;
	}

}
