package test;
import lejos.ev3.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TextLCD lcd = LocalEV3.get().getTextLCD();
		lcd.drawChar('c', 0, 0);
	}

}
