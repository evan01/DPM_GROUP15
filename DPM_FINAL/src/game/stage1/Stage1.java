package game.stage1;

import robot.Robot;

public class Stage1 {
    private Robot robot;
    private int[] wifiValues;
    private robot.navigation.Odometer odo;
    private final double SQUARE_CENTER = 15.24;
    private final double SQUARE_WIDTH = 30.48;

    public Stage1(Robot robot, int[] wifiValues, robot.navigation.Odometer odo){
        this.robot = robot;
        this.wifiValues=wifiValues;
        this.odo=odo;
    }

    /**
	 * Stage 1 consists of localizing and reaching the opponents flag area
	 * 
	 */
    public void start(){
        robot.localize(); //First thing is to localize..
        //test
    	//Stage 1 begins by localizing
        robot.localize();
        
        //go to center of square it is currently in (one of the 4 corners)
        //since it is known robot starts in a corner, can use current odometer values to find
        //distance to smallest 30.48 multiples (width of square) and then add 15.24 to get
        //to center of that tile
        robot.moveTo(odo.getX()-(odo.getX()%SQUARE_WIDTH)+SQUARE_CENTER, odo.getY()-(odo.getY()%SQUARE_WIDTH)+SQUARE_CENTER);
        
    }

}
