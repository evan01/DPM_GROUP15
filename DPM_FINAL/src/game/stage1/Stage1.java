package game.stage1;

import java.io.IOException;

import robot.Robot;
import robot.constants.Constants;
import wifi.Transmission;
import wifi.WifiConnection;

public class Stage1 {
    private Robot robot;

    public Stage1(Robot robot){
        this.robot = robot;
        
    }

    /**
	 * Stage 1 consists of localizing and reaching the opponents flag area
	 * 
	 */
    public void start(){
    	//receive wifi values and associate to constants
    	WifiConnection conn = null;
		try {
			conn = new WifiConnection(Constants.SERVER_IP, Constants.TEAM_NUMBER);
		} catch (IOException e) {
			//LCD.drawString("Connection failed", 0, 8);
		}
		
		// example usage of Transmission class
		Transmission t = conn.getTransmission();
		if (t == null) {
			//LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			Constants.StartingCorner = t.startingCorner;
			Constants.homeZoneBL_X = t.homeZoneBL_X;
			Constants.homeZoneBL_Y = t.homeZoneBL_Y;
			Constants.opponentHomeZoneBL_X = t.opponentHomeZoneBL_X;
			Constants.opponentHomeZoneBL_Y = t.opponentHomeZoneBL_Y;
			Constants.dropZone_X = t.dropZone_X;
			Constants.dropZone_Y = t.dropZone_Y;
			Constants.flagType = t.flagType;
			Constants.opponentFlagType = t.opponentFlagType;
		}
		
    	//Stage 1 begins by localizing
        robot.localize();

    }

}
