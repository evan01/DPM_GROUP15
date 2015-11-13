package test;

import lejos.hardware.Button;
import robot.display.Display;
import robot.navigation.LightLocalizerTwo;
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
    	LightLocalizerTwo lightLocalizaerTwo = new LightLocalizerTwo();
        
		nav.clawUp();
		nav.grab();
    	lightLocalizaerTwo.lightLocalize();
		display.start();

		nav.travelToWithCorrection(0, 30.48, 90);
		nav.travelToWithCorrection(30.48, 30.48, 0);
		nav.travelToWithCorrection(30.48, 0, 270);
		nav.travelToWithCorrection(0, 0, 180);
		nav.turnTo(90,true);
		
		nav.stopMoving();
		
		
	}
}