

package robot.navigation;

import java.util.ArrayList;



import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Delay;
import robot.GameRobot;
import robot.constants.Constants;
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

public class ObjectSearch{

	private Odometer odo;
	private Navigation nav;
	private USSensor us;
	private GameRobot gameRobot =new GameRobot();
	private int expectedColorID;
	private ColorSensor cs;
	private double currentClosestObstacleDistance=50000;
	private double startingXPosition;
	private double startingYPosition;
	private int detectedColorID;
	private int distance;
	private int scanCounter;
	private int checkingDistanceCounter;
	private double distanceTraveled;
	private int searchCounter;
	private double distance1;
	private double distance2;
	private boolean requiredBlockFound;
	Vector position;
	TextLCD lcd = LocalEV3.get().getTextLCD();


	public ObjectSearch(Navigation nav, Odometer odo,int expectedColorID){
		this.us = USSensor.getInstance();
		new Thread(this.us).start();//Start the ultrasonic sensor
		us.setThreadRunning(true);

		this.cs = ColorSensor.getInstance();
		cs.setThreadRunning(true);
		new Thread(cs).start();
		this.odo = odo;
		this.nav = nav;
		this.expectedColorID=expectedColorID;
		position = new Vector();
		startingXPosition=odo.getX();
		startingYPosition=odo.getY();
		nav.setSearchingBlock(true);
		scanCounter=0;
		checkingDistanceCounter=0;
		distanceTraveled=0;
		searchCounter=0;
		distance1=0;
		distance2=0;
		requiredBlockFound=false;

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
		scanCounter=0;
		while(searchCounter<2 && requiredBlockFound==false){
			searchRight(); 
			requiredBlockFound=scan();
			nav.turnToSearch2(odo.getTheta(), 0);
			nav.goBackward(distance1+distance2);
			startingXPosition=odo.getX();
			startingYPosition=odo.getY();
			scanCounter=0;
			distance1=0;
			distance2=0;
			if(searchCounter==2 || requiredBlockFound==true){
				break;
			}
			else{
				nav.turnToSearch2(odo.getTheta(), 270);
				nav.goForward(30);
			}
		}
		Sound.beepSequence();
		Sound.beepSequence();
		Sound.beepSequence();
		Sound.beepSequence();
		Sound.beepSequence();
		nav.setSearchingBlock(false);
	}

	// SEARCH ROUTINE
	public void searchRight(){
		lcd.clear();
		nav.turnToSearch2(odo.getTheta(),0);
		sweep(25);
		nav.turnToSearch2(odo.getTheta(),position.getAngle()+2);

		distance = position.getDistance();
		if(distance<90){
			if(distance>20){
				distanceTraveled = Math.sqrt(Math.pow((startingYPosition - odo.getY()), 2) + Math.pow((startingXPosition - odo.getX()), 2));
				while(distance>20 && distanceTraveled<position.getDistance()-15){
					nav.setSpeeds(Constants.FAST, Constants.FAST);
					distance=us.scan();
					distanceTraveled = Math.sqrt(Math.pow((startingYPosition - odo.getY()), 2) + Math.pow((startingXPosition - odo.getX()), 2));
				}
				distance1=distanceTraveled;
			}
			nav.setIsNavigating(false);
			nav.stopMoving();
			sweep(80);
			visit(position);
		}
		else{
			nav.turnToSearch2(odo.getTheta(), 270);
			nav.goForward(30);
			searchCounter++;
			startingXPosition=odo.getX();
			startingYPosition=odo.getY();
			searchRight();

		}
		nav.stopMoving();
	}

	//if the sweeping angle is for example 80, then the robot will turn from 320 to 40
	public void sweep(double sweepingAngle){
		nav.stopMoving();

		double angleBeforeSweep =odo.getTheta();
		nav.setSweepMode(true);
		nav.turnToSearch1((sweepingAngle/2), true, true);
		us.setSweepMode(true);
		nav.turnToSearch1(sweepingAngle, true, false);
		us.setSweepMode(false);
		nav.setSweepMode(false);
		position=us.getSweepingClosestPosition();

		nav.stopMoving();
	}




	public void visit(Vector position){
		lcd.clear();
		nav.turnToSearch2(odo.getTheta(), position.getAngle()+4);
		distance2=position.getDistance()+2;
		nav.goForward(distance2);
		Delay.msDelay(50);
	}

	public boolean scan(){
		//return true if the object is what we want

		//Bad Sample, don't pick up anything
		detectedColorID=gameRobot.colorScan();
		int detectedColorID2= gameRobot.colorScan();
		if(detectedColorID!=-1 || detectedColorID2!=-1 ){
			if (detectedColorID==expectedColorID){
				Sound.beepSequence();
				searchCounter++;
				capture();
				scanCounter=0;
				return true;
			}else{
				Sound.twoBeeps();
				scanCounter=0;
				searchCounter++;
				if(searchCounter==2){
					capture();
				}
				return false;
			}
		}
		if(scanCounter==0){
			scanCounter++;
			nav.goBackward(18);
			sweep(80);
			visit(position);
			scan();		
		}
		Sound.beep();
		Sound.beep();
		Sound.beep();
		Sound.beep();
		searchCounter++;
		if(searchCounter==2){
			capture();
		}
		return false;
	}


	//CAPTURE ROUTINES 
	public void capture() {
		// Assuming that we know where the block is, and that we can capture the block
		//Find a path to the top corner of the field

		nav.grab();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		nav.clawUp();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
		}
		nav.goBackward(27);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
		}
		nav.clawDown();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
		}
		nav.armOpen();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
		}
		nav.goForward(20);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
		}
		nav.grab();
		//nav.grab();
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
		}
		nav.clawUp();
		Delay.msDelay(300);

	}


}
