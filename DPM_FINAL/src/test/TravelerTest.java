package test;/*
 * Created by evanknox on 2015-11-10.
 */

import lejos.hardware.Button;
import robot.display.Display;
import robot.navigation.LightLocalizerTwo;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.Traveler;

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
 
        //Using the travler should be as easy as this!
    	Odometer.getInstance();
    	//Display.getInstance().start();
    	LightLocalizerTwo ll = new LightLocalizerTwo();
    	ll.lightLocalize();
    	Navigation.getInstance().travelTo(-15, -15);
    	Navigation.getInstance().turnTo(0, true);
        Traveler trav = new Traveler();
        trav.goTo(2,2);
        //trav.goTo(2, 0);
       // trav.goTo(0,0);
        //trav.goTo(1,3);
    }

}
