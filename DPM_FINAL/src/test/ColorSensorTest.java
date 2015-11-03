package test;/*
 * Created by evanknox on 2015-11-02.
 */

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import robot.constants.Color;
import robot.sensors.ColorSensor;

public class ColorSensorTest {
    private static TextLCD LCD = LocalEV3.get().getTextLCD();

    public static void main(String[] args) {
        Color color;
        ColorSensor cs = ColorSensor.getInstance();
        new Thread(cs).start();

        //continuously get colour values and print out colour
        while (true) {
            color = cs.getColor();
            if (color.isSampleBlue()) {
                LCD.drawString("Blue", 0, 0);
            } else if (color.isSampleYellow()) {
                LCD.drawString("Yellow", 0, 0);
            } else if (color.isSampleWhite()) {
                LCD.drawString("White", 0, 0);
            } else if (color.isSampleRed()) {
                LCD.drawString("Red", 0, 0);
            } else{
                LCD.drawString("gros", 0, 0);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
            }
        }
    }
}