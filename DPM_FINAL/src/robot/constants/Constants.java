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
		//Ultrasonic Sensor
		public static final Port usPort = LocalEV3.get().getPort("S1");
		//Color Sensor
		public static final Port colorPort = LocalEV3.get().getPort("S3");
		//Light Sensors
		public static final Port rightLightPort = LocalEV3.get().getPort("S4");
		public static final Port leftLightPort = LocalEV3.get().getPort("S2");

	// Constants for Odometer and Robot
		// Motors
		public static final double WHEEL_RADIUS = 2.2;
		public static final double TRACK = 10.5;
		public static final int forwardSpeed = 150;
		public static final int ROTATE_SPEED = 30;
		public final static int FAST = 200;
		public final static int SLOW = 100;
		public final static int ACCELERATION = 4000;
		public final static double DEG_ERR = 3.0;
		public final static double CM_ERR = 1.0;
		public final static float motorStraight = 150;

		// US SENSOR
		public static final int SCANPERIOD = 80;
		public static final int OBJECT_DETECTION_DIST = 15;
		public static final int MOTOR_STRAIGHT = 200;

		// LIGHT SENSOR
		public static final int LIGHT_SENSOR_UPDATE_TIME = 200;
		public static final int LIGHT_THRESHOLD = 20; // Should be the difference between black vs everything else

		// Odometer
		public static final int ODOMETER_UPDATE_INTERVAL = 20;
		public static final double THRESHOLD_ERROR = Math.PI / 128; // acceptable angle error
		public static final double THRESHOLD_DISTANCE_ERROR = 1.25;

		// LCD DISPLAY
		public static final int LCD_REFRESH = 20;

		// LightSensor
		public static final double DIST_TO_LIGHT_SENSOR = 10.2;

	// Constants for the environment
	public static final double GRID_LINE_SPACING = 31.2;


}
