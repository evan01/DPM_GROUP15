package robot.navigation;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

import robot.constants.Constants;
import robot.sensors.LeftLightSensor;
import robot.sensors.RightLightSensor;
import robot.sensors.USSensor;

public class LightLocalizerTwo {
	private LeftLightSensor leftLightSensor;
	private RightLightSensor rightLightSensor;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private Navigation navigation;
	private boolean isBlackLineDetected;
	private boolean scanRight;
	private boolean scanLeft;
	public static double RECORDED_LINE_VALUE;
	public static double RECORDED_TILE_VALUE;
	public static double RECORDED_THRESHOLD_VALUE;
	private int LIGHT_SAMPLES = 40;
	public static double LightSensorThreshold;
	//private Display display;


	public LightLocalizerTwo(){
		leftLightSensor=LeftLightSensor.getInstance();
		rightLightSensor=RightLightSensor.getInstance();
		odometer=Odometer.getInstance();
		leftMotor = Motors.getInstance().getLeftMotor();
		rightMotor = Motors.getInstance().getRightMotor();
		navigation = Navigation.getInstance();
		isBlackLineDetected=false;
		scanRight=false;
		scanLeft=false;
		//display=Display.getInstance();

	}

	//*************************
	// Note: Uncomment Sound.beep() from goBackward() in navigation class
	//*************************

	public void lightLocalize(){
		//display.setLLS_ON(true);
		//display.setRLS_ON(true);
		//display.start();s

		performBlackLineDetection();

		
		/*//correct odometer x
		odometer.setX(Constants.LRS_TO_AXIS_DISTANCE + Constants.SQUARE_WIDTH);
		//correct odometer angle
		odometer.setTheta(0);
		 */
		
		double newPos1[] = {Constants.LRS_TO_AXIS_DISTANCE + Constants.SQUARE_WIDTH,0,0};
		boolean newUpdates1[] = {true,false,true};
		odometer.setPosition(newPos1,newUpdates1);

		//Delay.msDelay(5000);


		if(odometer.getTheta()==0 && odometer.getX()==Constants.LRS_TO_AXIS_DISTANCE + Constants.SQUARE_WIDTH){
			Sound.beepSequence();
		}

		//go backwards 10 cm to avoid detecting the y-axis while correcting odometer y later
		navigation.goBackward(Constants.LRS_TO_AXIS_DISTANCE + (Constants.SQUARE_WIDTH/2));

		//turn 90 degrees counterclockwise
		leftMotor.setSpeed(Constants.ROTATE_SPEED);
		rightMotor.setSpeed(Constants.ROTATE_SPEED);

		leftMotor.rotate(-navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), true);	//use new "TRACK" not the one in "Constants" class
		rightMotor.rotate(+navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), false);	//use new "TRACK" not the one in "Constants" class
//
//		performBlackLineDetection();
//
//		/*//correct theta again
//		odometer.setTheta(90);
//		//correct odometer y
//		odometer.setY(Constants.LRS_TO_AXIS_DISTANCE + Constants.SQUARE_WIDTH);
//		//Delay.msDelay(5000);
//		 */
//		
//		double newPos2[] = {0,Constants.LRS_TO_AXIS_DISTANCE + Constants.SQUARE_WIDTH,90};
//		boolean newUpdates2[] = {false,true,true};
//		odometer.setPosition(newPos2,newUpdates2);
//
//		if(odometer.getTheta()==90 && odometer.getY()==Constants.LRS_TO_AXIS_DISTANCE + Constants.SQUARE_WIDTH){
//			Sound.beepSequence();
//		}
//		navigation.goBackward(Constants.LRS_TO_AXIS_DISTANCE );
//
//		//turn 90 degrees clockwise
//		leftMotor.setSpeed(Constants.ROTATE_SPEED);
//		rightMotor.setSpeed(Constants.ROTATE_SPEED);
//
//		leftMotor.rotate(navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), true);	//use new "TRACK" not the one in "Constants" class
//		rightMotor.rotate(-navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), false);	//use new "TRACK" not the one in "Constants" class

		//navigation.travelTo(0, 0);
		//navigation.turnTo(0, true);
		//display.stop();

	}

	public void performBlackLineDetection(){
		double rightReading1,rightReading2,leftReading1,leftReading2;
		while(isBlackLineDetected==false){
			navigation.setSpeeds(Constants.LIGHT_LOCALIZATION_FORWARD_SPEED,Constants.LIGHT_LOCALIZATION_FORWARD_SPEED);

			rightReading1=rightLightSensor.scan();
			Delay.msDelay(50);
			rightReading2=rightLightSensor.scan();

			leftReading1=leftLightSensor.scan();
			Delay.msDelay(50);
			leftReading2=leftLightSensor.scan();

			scanRight=scanWithDiffrentialFilter(rightReading1,rightReading2);
			scanLeft=scanWithDiffrentialFilter(leftReading1,leftReading2);
			isBlackLineDetected=scanRight || scanLeft;	
		}
		Sound.beep();

		if(scanRight==true && scanLeft==true){
			//do nothing
			Sound.beepSequence();
			navigation.setSpeeds(0, 0);
		}

		else if(scanRight==true){
			//turn clockwise leftMotor only
			while(scanLeft==false){
				navigation.setSpeeds(Constants.ROTATE_SPEED,0);
				leftReading1=leftLightSensor.scan();
				Delay.msDelay(50);
				leftReading2=leftLightSensor.scan();
				scanLeft=scanWithDiffrentialFilter(leftReading1,leftReading2);
			}
			Sound.beep();
			navigation.setSpeeds(0, 0);
		}

		else if(scanLeft==true){
			//turn counterclockwise rightMotor only
			while(scanRight==false){
				navigation.setSpeeds(0,Constants.ROTATE_SPEED);
				rightReading1=rightLightSensor.scan();
				Delay.msDelay(50);
				rightReading2=rightLightSensor.scan();
				scanRight=scanWithDiffrentialFilter(rightReading1,rightReading2);
			}
			Sound.beep();
			navigation.setSpeeds(0, 0);
		}

		scanRight=false;
		scanLeft=false;
		isBlackLineDetected=false;

	}

	public boolean scanWithDiffrentialFilter(double average1,double average2){
		if ((average1<Constants.LIGHT_THRESHOLD || average2<Constants.LIGHT_THRESHOLD) ){
//		RECORDED_LINE_VALUE = Math.max(average1, average2);
		return true;
		}
		else{
//		RECORDED_TILE_VALUE = Math.min(average1, average2);
		return false;
		}
	}	
	
	// This will set the minumum threshold value
	public boolean scanDiffrentialFilter(double average1,double average2){
		if ((average1<Constants.LIGHT_THRESHOLD || average2<Constants.LIGHT_THRESHOLD) ){
		RECORDED_LINE_VALUE = Math.max(average1, average2);
		System.out.println("LINE:   " + RECORDED_LINE_VALUE);
		RECORDED_THRESHOLD_VALUE = RECORDED_TILE_VALUE - RECORDED_LINE_VALUE;
		System.out.println("DIFFERENTIAL THRESHOLD:  " + RECORDED_THRESHOLD_VALUE);
	//	Constants.NONABS_LIGHT_THRESHOLD = RECORDED_THRESHOLD_VALUE;
		return true;
		}
		else{
//		RECORDED_TILE_VALUE = Math.min(average1, average2);
		return false;
		}
	}
	
	public void computeAverageLightValue(LeftLightSensor leftLightSensor , RightLightSensor rightLightSensor)
	{
		double[] lightSamples = new double[LIGHT_SAMPLES];
		double[] lightSamples2 = new double[LIGHT_SAMPLES];
		double totalLight = 0;
		double totalLight2 = 0;
		for(int i = 0; i < lightSamples.length; i++)
		{
			lightSamples[i] = leftLightSensor.scan();
			lightSamples2[i]= rightLightSensor.scan();
			totalLight = totalLight + lightSamples[i];
			totalLight2 = totalLight2 + lightSamples2[i];
			Delay.msDelay(10);
		}
		RECORDED_TILE_VALUE = Math.min(totalLight/LIGHT_SAMPLES, totalLight2/LIGHT_SAMPLES);
		System.out.println("TILE:  "+RECORDED_TILE_VALUE);
	}
	
//	public void computeAverageLightValue()
//	{
//		LightSensorThreshold = computeAverageLightValue(leftLightSensor , rightLightSensor) - 50;
//	}
	
	
}

