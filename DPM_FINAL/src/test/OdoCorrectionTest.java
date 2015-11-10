package test;

import lejos.hardware.Button;
import robot.display.Display;
import robot.navigation.Navigation;
import robot.navigation.Odometer;

public class OdoCorrectionTest {
	
	/**
	 * This class will make sure that the robot can perform odometry correection correctly
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
    	
		testCorrection();	
	}
	
	public static void testCorrection(){
		Odometer odo = Odometer.getInstance();
		Navigation nav = Navigation.getInstance();
		Display display = Display.getInstance();
		
		display.start();
		nav.clawUp();
		nav.grab();
		odo.setPosition(new double[] {0, 0,0,}, new boolean[]{true,true,true});
		nav.travelToWithCorrection(60, 90, odo.getTheta());
		
		
	}
}