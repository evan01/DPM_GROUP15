package game.stage3;

import robot.Robot;
import robot.constants.Constants;

public class Stage3 {
	private Robot robot;

	public Stage3(Robot robot) {
		this.robot = robot;
	}

	public void start() {
		robot.goTo(Constants.dropZone_X+1, Constants.dropZone_Y+1);
	}
}
