package test;

import lejos.hardware.Button;
import robot.navigation.Navigation;
import robot.navigation.Odometer;

public class ClawTest {
	
	/**
	 * This class will make sure that the robot can pick up a styrofoam block correctly
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
    	
		testClaw();	
	}
	
	public static void testClaw(){
		Odometer odo = Odometer.getInstance();
		Navigation nav = Navigation.getInstance();
		nav.grab();
		nav.clawUp();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.goBackward(10);
		nav.clawDown();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.armOpen();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.goForward(10);
		nav.grab();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.clawUp();
		}
	

}
