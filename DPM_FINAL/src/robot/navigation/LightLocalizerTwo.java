package robot.navigation;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

import robot.constants.Constants;
import robot.display.Display;
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
	private Display display;


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
		//display.start();

		(new Thread() {
			public void run() {
				TextLCD LCD = LocalEV3.get().getTextLCD();
				while (true){
					//Sound.beep();
					LCD.drawString("X "+odometer.getX(), 0, 0);
					LCD.drawString("Y "+odometer.getY(), 0, 1);
					LCD.drawString("Theta "+odometer.getTheta(), 0, 2);

					LCD.drawString("LLS "+LeftLightSensor.getInstance().scan(), 0, 5);
					LCD.drawString("RLS "+RightLightSensor.getInstance().scan(), 0, 6);
				}
			}
		}).start();
		
		performBlackLineDetection();

		//correct odometer x
		odometer.setX(Constants.LRS_TO_AXIS_DISTANCE);
		//correct odometer angle
		odometer.setTheta(0);

		double newPos1[] = {Constants.LRS_TO_AXIS_DISTANCE,0,0};
		boolean newUpdates1[] = {true,false,true};
		odometer.setPosition(newPos1,newUpdates1);

		//Delay.msDelay(5000);


		if(odometer.getTheta()==0 && odometer.getX()==Constants.LRS_TO_AXIS_DISTANCE){
			Sound.beepSequence();
		}

		//go backwards 10 cm to avoid detecting the y-axis while correcting odometer y later
		navigation.goBackward(Constants.LRS_TO_AXIS_DISTANCE);

		//turn 90 degrees counterclockwise
		leftMotor.setSpeed(Constants.ROTATE_SPEED);
		rightMotor.setSpeed(Constants.ROTATE_SPEED);

		leftMotor.rotate(-navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), true);	//use new "TRACK" not the one in "Constants" class
		rightMotor.rotate(+navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), false);	//use new "TRACK" not the one in "Constants" class

		performBlackLineDetection();

		//correct theta again
		odometer.setTheta(90);
		//correct odometer y
		odometer.setY(Constants.LRS_TO_AXIS_DISTANCE);
		//Delay.msDelay(5000);

		double newPos2[] = {0,Constants.LRS_TO_AXIS_DISTANCE,90};
		boolean newUpdates2[] = {false,true,true};
		odometer.setPosition(newPos2,newUpdates2);

		if(odometer.getTheta()==0 && odometer.getY()==Constants.LRS_TO_AXIS_DISTANCE){
			Sound.beepSequence();
		}
		navigation.goBackward(Constants.LRS_TO_AXIS_DISTANCE);

		//turn 90 degrees clockwise
		leftMotor.setSpeed(Constants.ROTATE_SPEED);
		rightMotor.setSpeed(Constants.ROTATE_SPEED);

		leftMotor.rotate(navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), true);	//use new "TRACK" not the one in "Constants" class
		rightMotor.rotate(-navigation.convertAngle(Constants.WHEEL_RADIUS, Constants.TRACK, 90.0), false);	//use new "TRACK" not the one in "Constants" class

		//navigation.travelTo(0, 0);
		//navigation.turnTo(0, true);
		//display.stop();
		 	
		}
	
	
	
	
	
	

	public void performBlackLineDetection(){
		while(isBlackLineDetected==false){
			navigation.setSpeeds(Constants.LIGHT_LOCALIZATION_FORWARD_SPEED,Constants.LIGHT_LOCALIZATION_FORWARD_SPEED);

			scanRight=scanRight(rightLightSensor);
			scanLeft=scanLeft(leftLightSensor);
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
				scanLeft=scanLeft(leftLightSensor);
			}
			Sound.beep();
			navigation.setSpeeds(0, 0);
		}

		else if(scanLeft==true){
			//turn counterclockwise rightMotor only
			while(scanRight==false){
				navigation.setSpeeds(0,Constants.ROTATE_SPEED);
				scanRight=scanRight(rightLightSensor);
			}
			Sound.beep();
			navigation.setSpeeds(0, 0);
		}
		
		scanRight=false;
		scanLeft=false;
		isBlackLineDetected=false;

	}




	/**
	 * Check if the rightLightSensor detected a black line
	 * @return
	 */
	private boolean scanRight(RightLightSensor rs){
		return isBlackLineDetected(rs.scan());
	}

	/**
	 * Check if the leftLightSensor detected a black line
	 * @return
	 */
	private boolean scanLeft(LeftLightSensor ls){
		return isBlackLineDetected(ls.scan());
	}

	private boolean isBlackLineDetected(double val){
		if(val<45)
			return true;
		else
			return false;
	}

}

