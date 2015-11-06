package test;

import robot.display.Display;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.USLocalizer;
import robot.sensors.USSensor;

public class UsLocalizerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display lcd=Display.getInstance();
		lcd.start();
		Odometer odo = Odometer.getInstance();
		USSensor us = USSensor.getInstance();
		new Thread(us).start();
		Navigation nav = Navigation.getInstance();
		
		USLocalizer loc = new USLocalizer(odo,USLocalizer.LocalizationType.FALLING_EDGE);
		loc.doLocalization();
		nav.travelTo(0, 0);
		nav.turnTo(90, true);

		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
		}
	}

}
