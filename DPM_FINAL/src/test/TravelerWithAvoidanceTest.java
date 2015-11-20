package test;

import lejos.hardware.Button;
import robot.display.Display;
import robot.navigation.LightLocalizerTwo;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.Traveler;

public class TravelerWithAvoidanceTest {

	/**
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
    	
    	Display display=Display.getInstance();
    	display.start();
    	display.setUS_ON(true);
    	LightLocalizerTwo ll = new LightLocalizerTwo();
    	ll.lightLocalize();
    	
    	Odometer.getInstance();
    	Navigation.getInstance().travelTo(16, 16);
    	Navigation.getInstance().turnTo(0, true);
        Traveler trav = new Traveler();
        trav.goTo(3,3);
	}

}
