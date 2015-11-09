package test;

import lejos.hardware.Button;
import robot.GameRobot;
import robot.Robot;

public class colorScanTest {

	/**
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
		
		// TODO Auto-generated method stub
		Robot robot = new GameRobot();
		System.out.println(robot.colorScan());
		
	}

}
