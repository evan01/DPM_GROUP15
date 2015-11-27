package test;

import robot.display.Display;
import robot.navigation.Navigation;
import robot.sensors.LeftLightSensor;

public class GridLine {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Navigation nav = Navigation.getInstance();
		//Display dis = Display.getInstance();
		//dis.start();
		nav.travelToWithCorrection(0, 400, 90);
	}

}
