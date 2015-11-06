package test;

import robot.navigation.Navigation;
import robot.navigation.Odometer;

public class ClawTest {
	
	/**
	 * This class will make sure that the robot can pick up a styrofoam block correctly
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
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.clawDown();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.armOpen();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.grab();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.clawUp();
		}
	

}
