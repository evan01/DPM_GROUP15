package test;

import lejos.hardware.Button;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;
import robot.constants.Constants;
import robot.navigation.LightLocalizerTwo;
import robot.navigation.Navigation;

public class LightLocalizationTwoTest {
	
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
    	
    	LightLocalizerTwo lightLocalizaerTwo = new LightLocalizerTwo();
        lightLocalizaerTwo.lightLocalize();
    }
}
