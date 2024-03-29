package robot.display;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import robot.constants.Constants;
import robot.constants.Position;
import robot.navigation.Navigation;
import robot.navigation.Odometer;
import robot.navigation.Traveler;
import robot.sensors.ColorSensor;
import robot.sensors.LeftLightSensor;
import robot.sensors.RightLightSensor;
import robot.sensors.USSensor;


/**
 * The LCD display class will display the readings from the sensors and odometer.
 * This class implements TimerListener which means that it will run at a 'constant' interval
 */
public class Display implements TimerListener{
	
	//This is a singleton class
	private static Display instance = new Display();
	public static synchronized Display getInstance(){
		return instance;
	}

	/**
	 * Constructor
	 */
	private Display() {
		this.odo = Odometer.getInstance();
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		mode = Mode.Standard;//Always the standard mode by default
		message = "No Debug Message";
		CS_ON = false;
		US_ON = false;
		LLS_ON=false;
		RLS_ON=false;
		//NumOf_Detections_ON=false;
		
	}

	/**
	 * Mode setting for the class, changes what the LCD actually displays
	 */
	public enum Mode {
		Standard, //Will display the usual screen
		Debug //Will allow for a different display
	}
	//Useful info/class vars we will display
	private static Mode mode;
	private static String message;
	private static boolean US_ON;
	private static boolean CS_ON;
	private static boolean LLS_ON;
	private static boolean RLS_ON;
	//private static boolean NumOf_Detections_ON;
	
	//The actual lcd display of our robot
	public static TextLCD LCD = LocalEV3.get().getTextLCD();

	public static final int LCD_REFRESH = Constants.LCD_REFRESH;
	private Odometer odo; //Gets the only instance of the odometer
	private Timer lcdTimer;

	// functions to start/stop the timerlistener
	public void stop() {
		if (this.lcdTimer != null)
			this.lcdTimer.stop();
	}
	public void start() {
		if (this.lcdTimer != null)
			this.lcdTimer.start();
	}

	/**
	 * Timed out method, called when the timer times out
	 */
	public void timedOut() {
		if(mode.equals(Mode.Standard)){
			displayStandard();
		}else{
			displayDebug();
		}
	}

	/**
	 * Classic display, useful readings and space for debug messages
	 */
	public void displayStandard(){
		LCD.clear();
		Position p = odo.getPosition();
		LCD.drawString("X: "+p.getX(),0,1);
		LCD.drawString("Y: "+p.getY(),0,2);
		LCD.drawString("Theta: "+p.getTheta(),0,3);
		LCD.drawString("Curr X:"+Traveler.currentX,0,6);
		LCD.drawString("Curr Y:"+Traveler.currentY,0,7);
		
		if(US_ON){
			//If the ultrasonic sensor is on
			LCD.drawString("USSensor: "+USSensor.getInstance().scan(), 0, 4);
		}
		if(CS_ON){
			//If the color sensor is on
			LCD.drawString("ColorSensor: "+ColorSensor.getInstance().scan(), 0, 5);
		}
		
		/*if(LLS_ON){
			//If the left light sensor is on
			LCD.drawString("LeftSensor: "+LeftLightSensor.getInstance().scan(), 0, 5);
		}
		if(RLS_ON){
			//If the right light sensor is on
			LCD.drawString("RightSensor: "+RightLightSensor.getInstance().scan(), 0, 6);
		}
		*/
		
		
		
		/*if(NumOf_Detections_ON){
			//When we are detecting our styrofoam object after getting close to it  
			LCD.drawString("Detections: "+USSensor.getInstance().getDistance(), 0, 7);
		}
		*/
		
		
		//LCD.drawString(message,0,7);
		

	}
	
	//setters method 
	public void setUS_ON(boolean uS_ON) {
		US_ON = uS_ON;
	}

	public void setCS_ON(boolean cS_ON) {
		CS_ON = cS_ON;
	}

	public void setLLS_ON(boolean lLS_ON) {
		LLS_ON = lLS_ON;
	}

	public void setRLS_ON(boolean rLS_ON) {
		RLS_ON = rLS_ON;
	}
	
	/*public void setNumOf_Detections_ON(boolean numOfDetections_ON) {
		NumOf_Detections_ON = numOfDetections_ON;
	}
*/
	
	public void displayDebug(){

	}

	//Will return the raw TextLCD class in the case that we want to use it.
	public synchronized TextLCD getTextLCD() {
		return LCD;
	}

}
