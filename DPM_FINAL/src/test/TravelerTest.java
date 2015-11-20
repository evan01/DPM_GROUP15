package test;/*
 * Created by evanknox on 2015-11-10.
 */

import lejos.hardware.Button;
import robot.display.Display;
import robot.navigation.LightLocalizerTwo;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.Traveler;
import robot.navigation.USLocalizer;
import robot.navigation.USLocalizer.LocalizationType;

public class TravelerTest {
	
	
    public static void main(String[] args){
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
    	
   
    	USLocalizer ul = new USLocalizer(Odometer.getInstance(),LocalizationType.FALLING_EDGE);
    	ul.doLocalization();
    	
    	Navigation.getInstance().turnTo(0, true);
    	
    	LightLocalizerTwo ll = new LightLocalizerTwo();
    	ll.lightLocalize();
    	
    	Traveler trav = new Traveler();
        trav.goTo(7, 7);
//    	
//    	Display.getInstance().start();
//    	Navigation.getInstance().travelTo(16, 16);
//    	Navigation.getInstance().turnTo(0, true);

            
    }

}
