package test;/*
 * Created by evanknox on 2015-11-05.
 */

import lejos.hardware.Button;
import robot.navigation.LightLocalizer;

public class LightLocalizationTest {
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
    	LightLocalizer ll = new LightLocalizer();
        ll.lightLocalize();
    }
}
