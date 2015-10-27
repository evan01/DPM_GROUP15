package robot.display;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import robot.constants.Constants;
import robot.constants.Position;
import robot.navigation.Odometer;


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
		this.mode = Mode.Standard;//Always the standard mode by default
		this.stage = "Stage 1";
		this.CS_ON = false;
		this.US_ON = false;
	}

	/**
	 * Mode setting for the class, changes what the LCD actually displays
	 */
	public static enum Mode {
		Standard, //Will display the usual screen
		Debug //Will allow for a different display
	}
	//Useful info/class vars we will display
	private static Mode mode;
	private static String stage;
	private static boolean US_ON;
	private static boolean CS_ON;

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
		Position p = odo.getPosition();
		LCD.clear();
		LCD.drawString(stage,0,0);
		LCD.drawString(stage,0,1);
		LCD.drawString(stage,0,2);
		LCD.drawString("---------------",0,3);
		LCD.drawString(p.toString(), 0, 4);
		if(US_ON){
			//If the ultrasonic sensor is on
			LCD.drawString("USSensor: ", 0, 5);
		}
		if(CS_ON){
			//If the color sensor is on
			LCD.drawString("ColorSensor: ", 0, 6);
		}

	}

	public void displayDebug(){

	}

	//Will return the raw TextLCD class in the case that we want to use it.
	public synchronized TextLCD getTextLCD() {
		return LCD;
	}

}
