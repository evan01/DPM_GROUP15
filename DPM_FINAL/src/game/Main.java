package game;

import robot.GameRobot;
import robot.Robot;
import game.stage1.Stage1;
import game.stage2.Stage2;
import game.stage3.Stage3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import robot.constants.Constants;
import robot.constants.Vector;
import robot.navigation.Navigation;
import robot.sensors.ColorSensor;
import robot.sensors.USSensor;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import robot.navigation.Odometer;


/**
 * This class is the main entry point into our game, everything originates from here
 */
public class Main {
	
	

	public static void main(String[] args) {
		Loader loader = new Loader();
		loader.load();

		//start the game logic
		Robot robot = new GameRobot();
		Stage1 s1 = new Stage1(robot);
		Stage2 s2 = new Stage2(robot);
		Stage3 s3 = new Stage3(robot);
		s1.start();
		s2.start();
		s3.start();
		
		

	}

}
