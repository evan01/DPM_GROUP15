package test;/*
 * Created by evanknox on 2015-11-10.
 */

import lejos.hardware.Button;
import robot.display.Display;
import robot.navigation.LightLocalizerTwo;
import robot.navigation.Navigation;
import robot.navigation.ObjectSearch;
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
    	
   
		Display display=Display.getInstance();
		display.start();
		display.setUS_ON(true);
		//display.setCS_ON(true);

		Odometer odo=Odometer.getInstance();
		Navigation nav=Navigation.getInstance();
		
		
    	USLocalizer ul = new USLocalizer(Odometer.getInstance(),LocalizationType.FALLING_EDGE);
    	ul.doLocalization();
    	
    	nav.turnTo(0, true);
    	
    	LightLocalizerTwo ll = new LightLocalizerTwo();
    	ll.lightLocalize();
    	
    	Traveler trav = new Traveler();
        trav.goTo(7, 7);

        nav.turnTo(180, true);
    	int expectedColorID=1;
        startSearch(nav,odo,expectedColorID);

            
    }
	
	//Launches the search routine
	private static void startSearch(Navigation nav,Odometer odo,int expectedColorID ){
		ObjectSearch search = new ObjectSearch(nav,odo,expectedColorID);
		search.begin();
	}


}
