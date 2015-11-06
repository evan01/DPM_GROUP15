package game;

import robot.GameRobot;
import robot.Robot;
import game.stage1.Stage1;
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
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3MediumRegulatedMotor armMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	public static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	public static void main(String[] args) {
//		Loader loader = new Loader();
//		loader.load();

		Odometer odo = Odometer.getInstance();
		Navigation nav = new Navigation();
		
	//	odo.setPosition(new double[]{0, 0,0}, new boolean[] {true,true,true});
		
		
		//Start the game!
//		Robot robot = new GameRobot();
//		Stage1 s1 = new Stage1(robot);
//		s1.start();
//		Stage2 s2 = new Stage2(robot);
//		Stage3 s3 = new Stage3(robot);

	}

}
