package game;

import game.stage1.Stage1;
import robot.GameRobot;
import robot.Robot;

/**
 * This class is the main entry point into our game, everything originates from here
 */
public class Main {

	public static void main(String[] args) {
		//TODO Final Game code starts here
		Loader loader = new Loader();
		loader.load();

		//Start the game!
		Robot robot = new GameRobot();
		//Stage1 s1 = new Stage1(robot);	//adjust the constructor
		//s1.start();
		
//		Stage2 s2 = new Stage2(robot);
//		Stage3 s3 = new Stage3(robot);



		//testing git in main.java

	}

}
