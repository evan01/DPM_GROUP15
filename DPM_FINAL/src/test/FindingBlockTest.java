package test;

import game.stage2.Stage2;
import robot.GameRobot;
import robot.Robot;

public class FindingBlockTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Robot robot = new GameRobot();
		Stage2 stage = new Stage2(robot);
		stage.start();

	}

}
