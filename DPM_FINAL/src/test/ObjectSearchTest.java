package test;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;
//import main.Lab5;
//import main.ObjectSearch;
import robot.display.Display;
import robot.navigation.LightLocalizer;
import robot.navigation.LightLocalizerTwo;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.USLocalizer;
import robot.navigation.ObjectSearch;

public class ObjectSearchTest {
	public static void main(String args[]){

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
		
		//USLocalizer usl = new USLocalizer(odo, USLocalizer.LocalizationType.FALLING_EDGE);
		//usl.doLocalization();
		
		LightLocalizerTwo ll = new LightLocalizerTwo();
        ll.lightLocalize();
		
		//nav.travelTo(15, 15);
		nav.turnTo(0, true);
		
		int expectedColorID=1;
		
		startSearch(nav,odo,expectedColorID);
	}
	
	
	
	//Launches the search routine
	private static void startSearch(Navigation nav,Odometer odo,int expectedColorID ){
		ObjectSearch search = new ObjectSearch(nav,odo,expectedColorID);
		search.begin();
	}

}
