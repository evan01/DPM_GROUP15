//The only purpose of this class is to contain constants
//We could have put these in the main class, figured may want to consolodate them here
package robot.constants;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;

public class Constants {

	//Ports
		// Motors
		public static final Port leftMotorPort = LocalEV3.get().getPort("A");
		public static final Port rightMotorPort = LocalEV3.get().getPort("D");
		public static final Port armPort = LocalEV3.get().getPort("C");
		public static final Port clawMotorPort = LocalEV3.get().getPort("B");
		//Ultrasonic Sensor
		public static final Port usPort = LocalEV3.get().getPort("S4");
		//Color Sensor
		public static final Port colorPort = LocalEV3.get().getPort("S2");
		//Light Sensors
		public static final Port rightLightPort = LocalEV3.get().getPort("S1");
		public static final Port leftLightPort = LocalEV3.get().getPort("S3");

	// Constants for Odometer and Robot
		// Motors
		public static final double WHEEL_RADIUS = 2.2;
		public static final double TRACK = 15.1;
		public static final float LIGHT_LOCALIZATION_FORWARD_SPEED=60;
		public static final int forwardSpeed = 150;
		public static final int ROTATE_SPEED = 50;
		public final static int FAST = 200;
		public final static int SLOW = 100;
		public final static int ACCELERATION = 4000;
		public final static double DEG_ERR = 3.0;
		public final static double CM_ERR = 1.0;
		public final static float motorStraight = 150;
		public static final double LRS_TO_AXIS_DISTANCE = 6.6;	//distance from the left/right sensors to the center of the wheel axis

		// US SENSOR
		public static final int SCANPERIOD = 80;
		public static final int OBJECT_DETECTION_DIST = 15;
		public static final int MOTOR_STRAIGHT = 200;

		// LIGHT SENSOR
		public static final int LIGHT_SENSOR_UPDATE_TIME = 200;
		public static final int LIGHT_THRESHOLD = 25; 
		public static final double LIGHT_SENS_OFFSET = 5.4;
		
		public static final double SQUARE_WIDTH = 30.48;

		// Odometer
		public static final int ODOMETER_UPDATE_INTERVAL = 20;
		public static final double THRESHOLD_ERROR = Math.PI / 128; // acceptable angle error
		public static final double THRESHOLD_DISTANCE_ERROR = 1.25;

		// LCD DISPLAY
		public static final int LCD_REFRESH = 50;

		// LightSensor
	

	// Constants for the environment
	public static final double GRID_LINE_SPACING = 31.2;
	//needs to be measured
	public static final double STYRO_BLOCK=12;
	
	//Robot physical constants, will measure soon
	public static final double ROBOT_WIDTH=12;
	public static final double ROBOT_LENTGH=12;
	public static final double COLOR_TO_CENTER=12;
	public static final double US_TO_CENTER=12;


}
