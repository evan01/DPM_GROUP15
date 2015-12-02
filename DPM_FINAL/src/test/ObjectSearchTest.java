package test;

import lejos.hardware.Button;

import lejos.hardware.Sound;
import lejos.utility.Delay;
//import main.Lab5;
//import main.ObjectSearch;
import robot.constants.Position;
import robot.display.Display;
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

		Odometer odo=Odometer.getInstance();
		Navigation nav=Navigation.getInstance();

		//USLocalizer usl = new USLocalizer(odo, USLocalizer.LocalizationType.FALLING_EDGE);
		//usl.doLocalization();

		//LightLocalizerTwo ll = new LightLocalizerTwo();
		//ll.lightLocalize();


		int expectedColorID=2;
		startSearch(nav,odo,expectedColorID);


		/*
		if (color.isSampleBlue()) {
			return 5;
		} else if (color.isSampleYellow()) {
			return 3;
		} else if (color.isSampleWhite()) {
			return 4;
		} else if (color.isSampleRed()) {
			return 2;
		} else if (color.isSampleLightBlue()) {
			return 1;
		} else {
			// bad sample was taken
			return -1;
		}*/
	}



	//Launches the search routine
	private static void startSearch(Navigation nav,Odometer odo,int expectedColorID ){
		ObjectSearch search = new ObjectSearch(nav,odo,expectedColorID);
		search.begin();
	}

}
