package game.stage2;

import robot.Robot;
import robot.constants.*;

public class Stage2 {
	private Robot robot;

	public Stage2(Robot robot) {
		this.robot = robot;
	}

	public void start() {
		robot.ObjectSearch();
	}
}
