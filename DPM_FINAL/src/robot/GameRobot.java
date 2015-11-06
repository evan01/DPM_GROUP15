package robot;

/*
 * Created by evanknox on 2015-10-27.
 */

import java.util.ArrayList;

import robot.constants.Color;
import robot.constants.Position;
import robot.navigation.Capturer;
import robot.sensors.ColorSensor;
import robot.sensors.USSensor;

/**
 * This class represents our actual implementation of the robot class, we may
 * have multiple implementations if a specific implementation fails to pass all
 * of our tests later on. For now, all the methods in this class aren't
 * synchronized although this may change...
 */
public class GameRobot implements Robot {

	public void moveTo(double x, double y) {

	}

	public void turnTo(int angle) {

	}

	public int usScan() {
		return 0;
	}

	public int colorScan() {
		// object for synchronized lock
		Object lock = new Object();
		ColorSensor cs = ColorSensor.getInstance();
		new Thread(cs).start();
		Color color = cs.getColor();
		int counterScan = 0;
		ArrayList<Color> colorPings = new ArrayList<Color>();
		synchronized (lock) {
			// continuously get colour values
			while (counterScan != 10) {
				color = cs.getColor();
				colorPings.add(color);
				counterScan++;
			}
		}
		// get average of each value
		double R = 0;
		double G = 0;
		double B = 0;
		for (Color i : colorPings) {
			R += i.getR();
			G += i.getG();
			B += i.getB();
		}
		R /= 10.0;
		G /= 10.0;
		B /= 10.0;
		color.setR(R);
		color.setG(G);
		color.setB(B);

		if (color.isSampleBlue()) {
			return 0;
		} else if (color.isSampleYellow()) {
			return 1;
		} else if (color.isSampleWhite()) {
			return 2;
		} else if (color.isSampleRed()) {
			return 3;
		} else if (color.isSampleLightBlue()) {
			return 4;
		} else {
			// bad sample was taken
			return -1;
		}
	}

	public void capture() {
		Capturer.capture();
	}

	public void localize() {

	}

	public Position getOdometerReading() {
		return null;
	}

}
