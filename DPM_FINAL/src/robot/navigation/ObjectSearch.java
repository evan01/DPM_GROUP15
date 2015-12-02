package robot.navigation;

import java.util.ArrayList;


import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Delay;
import robot.GameRobot;
import robot.constants.Position;
//import robot.display.LCDDisplay;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.sensors.ColorSensor;
import robot.sensors.USSensor;
import robot.navigation.USLocalizer;
import robot.constants.Vector;
import robot.display.Display;

/**
 * Our robot needs to perform a sequence of operations in order to complete the
 * lab Robot starts on lower left corner of the floor and started by button
 * press 
 * 	1. Localization, moves to 0,0 on the grid 
 * 	2. Search, searches for objects on the floor, 
 *	3. Capture, push styrofoam block to upper right corner of floor section
 */

public class ObjectSearch {

	private Odometer odo;
	private Navigation nav;
	private USSensor us;
	private GameRobot gameRobot =new GameRobot();
	private int expectedColorID;
	private ColorSensor cs;
	Vector position;
	TextLCD lcd = LocalEV3.get().getTextLCD();

	/*//This is a singleton class
	private static ObjectSearch instance = new ObjectSearch(Navigation.getInstance(),Odometer.getInstance());
	public static synchronized ObjectSearch getInstance(){
		return instance;
	}
	 */

	public ObjectSearch(Navigation nav, Odometer odo,int expectedColorID){
		//USSensor.getInstance();
		this.us = USSensor.getInstance();
		new Thread(this.us).start();//Start the ultrasonic sensor
		us.setThreadRunning(true);

		this.cs = ColorSensor.getInstance();
		cs.setThreadRunning(true);
		new Thread(cs).start();
		this.nav = nav;
		this.odo = odo;
		this.expectedColorID=expectedColorID;
		position = new Vector();


		(new Thread() {
			public void run() {
				while (true){
					lcd.drawString("CS "+gameRobot.colorScan(), 0, 5);
					lcd.drawString("Dist "+position.getDistance(), 0, 6);
					lcd.drawString("Ang "+position.getAngle(), 0, 7);
				}
			}
		}).start();
	}

	public void begin() {
		//do the three actions that our robot needs to do
		searchRight(); //Searches the first thing we find and visits it, will leave us in front of first block
		if(scan()==true){
			capture();
		}else{
			redo();
		}
	}

	public void redo(){
		nav.goBackward(position.getDistance()-10);
		double currentAng = odo.getTheta();
		double newAng = currentAng +20;
		nav.turnTo(newAng, true);
		//Delay.msDelay(3000);
		
		begin();
	}


	/*
	 * SEARCH ROUTINE
	 */
	public void searchRight(){
		int distance;
		int numDetections = 0;
		nav.search(false); //Now we are rotating left
		lcd.clear();

		//lcd.drawString("Odometer Angle: "+odo.getTheta(),0,0);

		while((odo.getTheta() < 300 && numDetections<1) || (odo.getTheta() > 170 && numDetections<1)){
			distance = us.scan();
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
			
			//lcd.drawString("US Reading: "+distance,0,0);
			//lcd.drawString("Detections: "+numDetections,0,7);
			if (distance < 80){
				//Then we have a detection! Add vector to our array to return
				Button.LEDPattern(5);
				position.setAngle(odo.getTheta());
				position.setDistance(distance);
				//lcd.drawString("Dist "+position.getDistance(), 0, 6);
				//lcd.drawString("Ang "+position.getAngle(), 0, 7);
				numDetections++;
				Sound.beep();
				nav.stopMoving();

			}
		}

		visit(position);
	}


	public void visit(Vector position){
		//TextLCD lcd = LocalEV3.get().getTextLCD();
		lcd.clear();
		nav.turnTo(position.getAngle(), true);
		nav.goForward(position.getDistance());
		Delay.msDelay(2000);
		

	}

	public boolean scan(){

		//return true if the object is what we want


		/*if(cs.getColor().isBadSample()){
			//will probably need to come a bit more closer to the object, i.e go forward a bit

			//Sound.buzz();
			//return scan();
			return false;
		}
		else{
		 */	

		if (gameRobot.colorScan()==expectedColorID){
			Sound.twoBeeps();
			//cs.setThreadRunning(false);
			return true;
		}else{
			Sound.beepSequence();
			//cs.setThreadRunning(false);
			return false;
		}
		//}
	}

	/*
	 * CAPTURE ROUTINES 
	 */
	public void capture() {
		// Assuming that we know where the block is, and that we can capture the block
		//Find a path to the top corner of the field
		
		nav.grab();
		nav.clawUp();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.goBackward(27);
		nav.clawDown();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.armOpen();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.goForward(14);
		nav.grab();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.clawUp();
		nav.stopMoving();

		//First start the 

	}


}
