package test;

import java.util.ArrayList;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import robot.GameRobot;
import robot.Robot;

public class LightSensorTest {

	/**
	 * @param args
	 */
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	private static final Port colorPort = LocalEV3.get().getPort("S3");
	public static void main(String[] args) {
		//create sensor
		@SuppressWarnings("resource")
		EV3ColorSensor colorSensor = new EV3ColorSensor(colorPort);
		float[] colorData = new float[colorSensor.getRedMode().sampleSize()];
		//go 96.44 cm forward, cross 3 lines
		leftMotor.setSpeed(100);
		leftMotor.setAcceleration(100);
		rightMotor.setSpeed(100);
		rightMotor.setAcceleration(100);
		leftMotor.rotate(1000, true);
		rightMotor.rotate(1000, true);
		
		ArrayList <Float> colorValues = new ArrayList <Float>();
		while(leftMotor.isMoving()){
			colorSensor.getRedMode().fetchSample(colorData, 0);
			colorValues.add(colorData[0]);
			try {
				//ping every 20ms
				Thread.sleep(20);
			} catch (InterruptedException e) {
				
			}
		}
		//print all values to collect in output file
		for(float i:colorValues){
			System.out.println(i);
		}

	}

}