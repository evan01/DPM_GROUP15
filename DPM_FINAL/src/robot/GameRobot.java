package robot;
/*
 * Created by evanknox on 2015-10-27.
 */

import robot.constants.NavInstruction;
import robot.constants.Position;
import robot.navigation.Capturer;
import robot.navigation.Navigator;

/**
 * This class represents our actual implementation of the robot class, we may have multiple implementations if
 * a specific implementation fails to pass all of our tests later on.
 */
public class GameRobot extends Robot {
	
    @Override
    public void moveTo(double x, double y) {
<<<<<<< Updated upstream
        //In theory the navigator thread should have already been started!!
        Navigator nav = Navigator.getInstance();
        NavInstruction xDistance = new NavInstruction();
        NavInstruction yDistance = new NavInstruction();

        nav.addInstructions(xDistance);
        nav.addInstructions(yDistance);
=======
    		
>>>>>>> Stashed changes
    }

    @Override
    public void turnTo(int angle) {
    	Navigator nav = Navigator.getInstance();
    	nav.turnTo(angle);
    }

    @Override
    public int usScan() {
        return 0;
    }

    @Override
    public int colorScan() {
        return 0;
    }

    @Override
    public void capture() {
        Capturer.capture();
    }

    @Override
    public void localize() {

    }

<<<<<<< Updated upstream
	@Override
	public Position getOdometerReading() {
		// TODO Auto-generated method stub
		return null;
	}
=======
   
>>>>>>> Stashed changes
}
