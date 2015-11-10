package game.stage2;

import robot.Robot;
import robot.constants.*;

public class Stage2 {
	private Robot robot;

	public Stage2(Robot robot) {
		this.robot = robot;
	}

	public void start() {
		// assume coordinate of top right corner of
		// opponent base is (x1,y1), will be set up with wifi
		// integer representing color block will be int colorBlock
		// bottom right corner x2,y2
		// bottom left corner x3,y3
		// top left corner x4,y4

		double colorBlock = 2;
		double x[] = new double[] { 0, 0, 0, 0 };
		double y[] = new double[] { 0, 0, 0, 0 };
		// every time the robot moves 1 styrofoam block width
		// to get to next position block could be
		double blockCount = 0;
		
		
		/* To find color blocks scan facing the opponents zone and
		 * follow outline moving one styrofoam block width every iteration
		 * the following 3 while loops follow the outline, as one side will
		 * be on side of wall
		 */
		
		
		// go to top right corner of base, leaving room to scan blocks
		// and not hit wall
		robot.travelTo(x[0] + Constants.ROBOT_LENTGH * 1.5, y[0]
				- Constants.STYRO_BLOCK);

		while (robot.getOdometerReading().getY() > y[1]) {
			// face potential block
			robot.turnTo(180);
			if (robot.usScan() < Constants.ROBOT_LENTGH + 10) {
				if (robot.colorScan() == colorBlock) {
					robot.capture();
					break;
				}
				// keep looking for block
			} else {
				robot.travelTo(x[0] + Constants.ROBOT_LENTGH * 1.5, y[0]
						- Constants.STYRO_BLOCK * blockCount);
				blockCount++;
			}
		}
		blockCount=2;
		robot.travelTo(x[0] + Constants.ROBOT_LENTGH*1.5, y[1]-Constants.STYRO_BLOCK);
		
		while (robot.getOdometerReading().getX() > x[2]) {
			// face potential block
			robot.turnTo(90);
			if (robot.usScan() < Constants.ROBOT_LENTGH + 10) {
				if (robot.colorScan() == colorBlock) {
					robot.capture();
					break;
				}
				// keep looking for block
			} else {
				robot.travelTo(x[0] - Constants.STYRO_BLOCK * blockCount, y[0]
						- Constants.STYRO_BLOCK);
				blockCount++;
			}
		}
		blockCount=2;
		robot.travelTo(x[2] - Constants.ROBOT_LENTGH*1.5, y[2]-Constants.STYRO_BLOCK);
		
		while (robot.getOdometerReading().getY() < y[3]-Constants.STYRO_BLOCK) {
			// face potential block
			robot.turnTo(0);
			if (robot.usScan() < Constants.ROBOT_LENTGH + 10) {
				if (robot.colorScan() == colorBlock) {
					robot.capture();
					break;
				}
				// keep looking for block
			} else {
				robot.travelTo(x[2] - Constants.ROBOT_LENTGH * 1.5, y[2]
						+ Constants.STYRO_BLOCK * blockCount);
				blockCount++;
			}
		}
		//robot should have styrofoam block at this point
	}
}
