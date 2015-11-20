package test;

import lejos.hardware.Button;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.USLocalizer;
import robot.sensors.USSensor;

public class NavigationTest {
	
	/**
	 * This class will make sure that the robot can navigate correctly
	 * @param args
	 */
	public static void main(String[] args) {
		//thread to allow exit at any time;
    	(new Thread() {
			public void run() {
				int buttonPressed=Button.waitForAnyPress();
				while (buttonPressed != Button.ID_ESCAPE){
					buttonPressed=Button.waitForAnyPress();
				}
				System.exit(0);
			}
		}).start();
		
		//testLocalization();
		testNavigation();	
	}
	
	public static void testNavigation(){
		Odometer odo = Odometer.getInstance();
		Navigation nav = Navigation.getInstance();
		nav.goForward(15);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.turnTo(45, false);
	}
	
	public static void testLocalization(){
		Odometer odo = Odometer.getInstance();
		USSensor us = USSensor.getInstance();
		new Thread(us).start();
		Navigation nav = Navigation.getInstance();
		
		nav.clawUp();
		nav.grab();
		USLocalizer loc = new USLocalizer(odo,USLocalizer.LocalizationType.FALLING_EDGE);
		loc.doLocalization();
		nav.travelTo(0, 0);
		nav.turnTo(0, true);

	}

}
