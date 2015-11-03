package test;

import robot.constants.Color;
import robot.sensors.ColorSensor;
import robot.sensors.USSensor;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorRecognitionTest {

	private static TextLCD LCD = LocalEV3.get().getTextLCD();

	public static void main(String[] args) {
		Color color;
		double dist;
		ColorSensor cs = ColorSensor.getInstance();
		new Thread(cs).start();
		USSensor us = USSensor.getInstance();
		new Thread(us).start();
		
		//continuously get colour values and print out colour
		while (true) {
		  color = cs.getColor();
		  dist = us.getDistance();
		  LCD.clear();
		  if(dist<5){
			if (color.isSampleBlue()) {
				LCD.drawString("Blue", 0, 0);
			} else if (color.isSampleYellow()) {
				LCD.drawString("Yellow", 0, 0);
			} else if (color.isSampleWhite()) {
				LCD.drawString("White", 0, 0);
			} else if (color.isSampleRed()) {
				LCD.drawString("Red", 0, 0);
			} else if(color.isSampleLightBlue()){
				LCD.drawString("light", 0, 0);
			} else{
				LCD.drawString("No Block", 0, 0);
			}
		  }
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
	}

}
