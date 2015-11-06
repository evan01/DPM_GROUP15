package test;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import robot.display.Display;
import robot.navigation.Navigation;
import robot.navigation.Odometer;

public class OdometerTest {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Navigation nav = Navigation.getInstance();
		//create display instance
		Display lcd = Display.getInstance();
		lcd.start();
		//cart moves along gridline accomplishing 1x1 square
		
		nav.travelTo(0, 30.48);
		nav.travelTo(30.48, 30.48);
		nav.travelTo(30.48, 0);
		nav.travelTo(0, 0);
		nav.turnTo(90, true);
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
		}
		System.exit(0);
	}
	
}
