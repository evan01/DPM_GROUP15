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
import robot.sensors.LeftLightSensor;
import robot.sensors.RightLightSensor;

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
//		display.start();
//		display.setUS_ON(true);
		//display.setCS_ON(true);

		Odometer odo=Odometer.getInstance();
		Navigation nav=Navigation.getInstance();
    	Traveler trav = new Traveler();
		
//    	LightLocalizerTwo ll = new LightLocalizerTwo();
////		ll.computeAverageLightValue(LeftLightSensor.getInstance(),RightLightSensor.getInstance());
//    	ll.lightLocalize();
////        trav.goTo(7, 7);
//    	nav.turnTo(240, true);
//    	nav.travelTo(10 , 15);

    	trav.goTo(5, 5);
//    	trav.goTo(1, 1);
//
//        nav.turnTo(180, true);
//    	int expectedColorID=1;
//        startSearch(nav,odo,expectedColorID);

            
    }
	
//	//Launches the search routine
//	private static void startSearch(Navigation nav,Odometer odo,int expectedColorID ){
//		ObjectSearch search = new ObjectSearch(nav,odo,expectedColorID);
//		search.begin();
//	}


}
