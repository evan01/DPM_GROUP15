package game;

import robot.GameRobot;
import robot.Robot;
import game.stage1.Stage1;

/**
 * This class is the main entry point into our game, everything originates from here
 */
public class Main {

	public static void main(String[] args) {
		Loader loader = new Loader();
		loader.load();

		//Start the game!
		Robot robot = new GameRobot();
		Stage1 s1 = new Stage1(robot);
		s1.start();
//		Stage2 s2 = new Stage2(robot);
//		Stage3 s3 = new Stage3(robot);

	}

}
