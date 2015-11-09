package test;

import lejos.hardware.Button;
import game.Loader;

public class LoaderTest {
	/**
	 * This class will test whether all of our threads and objects instantiate correctly
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
    	
		Loader loader = new Loader();
		loader.load();

	}

}
