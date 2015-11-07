package sshTutorial ;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class SshDataLogging{
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 15.6;
	
	private static final int ROTATE_SPEED = 30;

	public static void main (String [ ] args) throws InterruptedException,
	FileNotFoundException,UnsupportedEncodingException{
		PrintWriter writer= null;
		writer= new PrintWriter("data.csv","UTF8");

		EV3UltrasonicSensor sensor= new EV3UltrasonicSensor(SensorPort.S1);

		UltrasonicPoller usPoller= new UltrasonicPoller(sensor.getDistanceMode( ),sensor,writer);
		usPoller.start();

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// turn 90 degrees counterclockwise
		leftMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, 90.0), true);
		rightMotor.rotate(+convertAngle(WHEEL_RADIUS, TRACK, 90.0), false);

	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}



