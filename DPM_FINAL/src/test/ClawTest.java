package test;

import robot.navigation.Navigation;
import robot.navigation.Odometer;

public class ClawTest {
	
	/**
	 * This class will make sure that the robot can navigate correctly
	 * @param args
	 */
	public static void main(String[] args) {
		testClaw();	
	}
	
	public static void testClaw(){
		Odometer odo = Odometer.getInstance();
		Navigation nav = Navigation.getInstance();
		nav.grab();
		nav.clawUp();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.clawDown();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.armOpen();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.grab();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.clawUp();
		}
	
	
//	public static void testLocalization(){
//		Odometer odo = Odometer.getInstance();
//		USSensor us = USSensor.getInstance();
//		new Thread(us).start();
//		Navigation nav = Navigation.getInstance();
//		
//		USLocalizer loc = new USLocalizer(odo,USLocalizer.LocalizationType.FALLING_EDGE);
//		loc.doLocalization();
//		nav.travelTo(-5, -5);
//		nav.turnTo(30, true);
//	}

}
