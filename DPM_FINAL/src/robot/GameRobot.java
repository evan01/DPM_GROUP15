package robot;

/*
 * Created by evanknox on 2015-10-27.
 */

import java.util.ArrayList;

import robot.constants.Color;
import robot.constants.Constants;
import robot.constants.Position;
import robot.navigation.Capturer;
import robot.navigation.LightLocalizerTwo;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.Traveler;
import robot.navigation.USLocalizer;
import robot.navigation.USLocalizer.LocalizationType;
import robot.sensors.ColorSensor;
import robot.sensors.USSensor;
import robot.navigation.ObjectSearch;

/**
 * This class represents our actual implementation of the robot class, we may
 * have multiple implementations if a specific implementation fails to pass all
 * of our tests later on. For now, all the methods in this class aren't
 * synchronized although this may change...
 */
public class GameRobot implements Robot {

	//traveler class is used, incorporates obstacle avoidance
	public void goTo(int x, int y) {
		Traveler travel = new Traveler();
		travel.goTo(x, y);
	}
	//standard travel to method, not from center to center of square
	public void travelTo(double x, double y){
		
	}

	public void turnTo(int angle) {
		Navigation.getInstance().turnTo(angle, true);
	}

	public int usScan() {
		return 0;
	}
	
	public void ObjectSearch(){
		ObjectSearch objSearch = new ObjectSearch(Navigation.getInstance(), Odometer.getInstance(), Constants.flagType);
	}

	public int colorScan() {
	
		ColorSensor cs = ColorSensor.getInstance();
		Color color = new Color();
		int counterScan = 0;
		ArrayList<Color> colorPings = new ArrayList<Color>();
		
			// continuously get colour values
			while (counterScan != 10) {
				color = cs.scan();
				colorPings.add(color);
				counterScan++;
			
		}
		
		return color.colorValue(colorPings);
		
	}

	public void capture() {
		//Capturer.capture();
	}

	public void localize() {
		USLocalizer usLoc = new USLocalizer(Odometer.getInstance(), LocalizationType.FALLING_EDGE);
		usLoc.doLocalization();
		turnTo(0);
		LightLocalizerTwo lightLoc = new LightLocalizerTwo();
		lightLoc.lightLocalize();
	}

	public Position getOdometerReading() {
		return null;
	}

}
