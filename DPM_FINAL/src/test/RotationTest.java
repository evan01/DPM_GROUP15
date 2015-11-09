package test;

import robot.navigation.Odometer;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class RotationTest {

	private static Odometer odometer=Odometer.getInstance();
	private static final EV3LargeRegulatedMotor leftMotor = odometer.getLeftMotor();
	private static final EV3LargeRegulatedMotor rightMotor = odometer.getRightMotor();
	private static double WHEEL_RADIUS=2.2;
	private static double TRACK=13.5;
	private static final int ROTATE_SPEED = 150;
	private static TextLCD LCD = LocalEV3.get().getTextLCD();

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
		
		LCD.drawString("X "+odometer.getX(), 0, 0);
		LCD.drawString("Y "+odometer.getY(), 0, 1);
		LCD.drawString("Theta "+odometer.getTheta(), 0, 2);
		
		// turn 90 degrees clockwise
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, 90.0), true);
		rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, 90.0), false);
	}


	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
