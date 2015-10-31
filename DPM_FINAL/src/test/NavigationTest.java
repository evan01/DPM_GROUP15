package test;

import robot.constants.NavInstruction;
import robot.navigation.Localizer;
import robot.navigation.Navigation;
import robot.navigation.Navigator;
import robot.navigation.Odometer;
import robot.navigation.USLocalizer;
import robot.sensors.USSensor;

public class NavigationTest {
	
	/**
	 * This class will make sure that the robot can navigate correctly
	 * @param args
	 */
	public static void main(String[] args) {
		testLocalization();
		testNavigation();	
	}
	
	public static void testMoveTo(double x, double y){
		Navigator nav = Navigator.getInstance();
		
		NavInstruction xDistance = new NavInstruction();
        NavInstruction yDistance = new NavInstruction();
        xDistance.coordinate = x;
        xDistance.movingInX = true;
        yDistance.coordinate = y;
        yDistance.movingInX = false;

        nav.addInstructions(xDistance);
        nav.addInstructions(yDistance);
        
        new Thread(nav).start();

	}
	
	public static void testNavigation(){
		Odometer odo = Odometer.getInstance();
		Navigation nav = Navigation.getInstance();
		nav.travelTo(50, 0);
		nav.travelTo(50, 50);
		nav.travelTo(0, 50);
		nav.travelTo(0, 0);
	}
	
	public static void testLocalization(){
		Odometer odo = Odometer.getInstance();
		USSensor us = USSensor.getInstance();
		new Thread(us).start();
		Navigation nav = Navigation.getInstance();
		
		USLocalizer loc = new USLocalizer(odo,USLocalizer.LocalizationType.FALLING_EDGE);
		loc.doLocalization();
		nav.travelTo(0, 0);
		nav.turnTo(0, true);
	}

}
