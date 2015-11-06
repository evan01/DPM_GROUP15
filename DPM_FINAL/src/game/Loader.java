package game;/*
 * Created by evanknox on 2015-10-27.
 */

import robot.display.Display;
import robot.navigation.Motors;
import robot.navigation.Odometer;
import robot.sensors.ColorSensor;
import robot.sensors.USSensor;

/**
 * The purpose of this class is to start up all the threads and make sure that the robot can execute all the functions
 * without invalid sensor mode errors and instantiation errors
 */
public class Loader {
    /**
     * this method loads up all the classes when the game begins!
     */
    public void load() {
        try {
//          final Class<Odometer> aClass = Class.forName(Odometer);
        	Display.getInstance();
            Odometer.getInstance();
            USSensor.getInstance();
            ColorSensor.getInstance();
            Display.getInstance();
         //   Motors.getInstance();
        }catch (Exception e){

        }

    }
}
