package robot.navigation;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import robot.sensors.USSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }

	public static float ROTATION_SPEED = 70;
	

	private Odometer odo;
	private Navigation navigate;
	private USSensor us;
	private LocalizationType locType;

	public USLocalizer(Odometer odo, LocalizationType locType) {
		this.odo = odo;
		this.us = USSensor.getInstance();
		this.locType = locType;
		this.navigate = Navigation.getInstance();
	}

	private float getFilteredData() {
		int dist;
		dist = us.getDistance();
														// thus makes anything above the threshold distance irrelevant
		return dist;
	}

	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB , deltaTheta ;

		double USDISTANCE_THRESHOLD = 35;
		boolean isMeasured = false;
		if (locType == LocalizationType.FALLING_EDGE)
		{
			getFilteredData();

			navigate.setRotationSpeed(ROTATION_SPEED);											// start turning

			while(getFilteredData() < USDISTANCE_THRESHOLD) {
			}
			try { Thread.sleep(1000); } catch (InterruptedException e) {}

			while(getFilteredData() > USDISTANCE_THRESHOLD) {
			}
			// Wall is seen !!
			angleA = odo.getTheta();															  	// latch angle A
			Sound.buzz();

			navigate.setRotationSpeed(-ROTATION_SPEED); 									 	 // switch rotation direction


			while(getFilteredData() < USDISTANCE_THRESHOLD) {				    // (we don't want the same wall to be detected)
			}

			try { Thread.sleep(1000); } catch (InterruptedException e) {}


			while(getFilteredData() > USDISTANCE_THRESHOLD) {					// wait until you see 2nd wall !
			}
			// 2nd wall seen !!
			angleB = odo.getTheta();									        // latch angle B 																		   //2nd wall detected
			Sound.buzz();

			//use formulas
			if(angleA > angleB)
				angleB+=360;
			deltaTheta = 42-((angleA + angleB)/2);
			Sound.beep();
			odo.correctTheta(deltaTheta);										// adds the error dtheta onto the odomerters theta

			// now we MEASURE
			// measuring x and y can be found belowwww
			if(isMeasured)
			{																					// x, y already measured with respect to new (0,0)!
				navigate.setRotationSpeed(10); 												    // rotate slowly
				for( ; ; ) 																		// orienting robot
				{																				// Exit only if robot is with
					double theta = odo.getTheta();
					if(theta > 180)
						theta -= 360;
					if(theta >= 0 && theta < 20)
						break;
					try { Thread.sleep(100); } catch (InterruptedException e) {}
				}
				navigate.stopMoving();
				Sound.twoBeeps();
			}


		} else
		{
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			getFilteredData();

			navigate.setRotationSpeed(ROTATION_SPEED);											// start turning

			while(getFilteredData() == USDISTANCE_THRESHOLD) {
			}
			try { Thread.sleep(1000); } catch (InterruptedException e) {}

			while(getFilteredData() < USDISTANCE_THRESHOLD) {
			}
			// Wall is cleared !!
			angleA = odo.getTheta();															  // latch angle A
			Sound.buzz();

			navigate.setRotationSpeed(-ROTATION_SPEED); 										// switch rotation direction


			while(getFilteredData() < USDISTANCE_THRESHOLD) {					// (we don't want the same wall to be detected)
			}

			try { Thread.sleep(1000); } catch (InterruptedException e) {}


			while(getFilteredData() == USDISTANCE_THRESHOLD) {				   // wait to clear off first wall!
			}
			// 2nd wall cleared !!
			angleB = odo.getTheta();															   // latch angle B 																		   //2nd wall detected
			Sound.buzz();

			// use formulas
			if(angleA < angleB)
			{
				angleB+=360;
			}
			deltaTheta = 225-((angleA + angleB)/2);
			odo.correctTheta(deltaTheta); 															// ensure theta doesnt go out of bounds
			Button.LEDPattern(3);


			try { Thread.sleep(2000); } catch (InterruptedException e) {}
			// now we MEASURE
			if(isMeasured) {																		// x, y already measured !
				navigate.setRotationSpeed(-10); 													// rotate slowly
				for( ; ; ) 																			// orienting robot
				{																					// exit
					double theta = odo.getTheta();
					if(theta > 180)
						theta -= 360;
					if(theta <= 0 && theta > -20)
						break;
					try { Thread.sleep(100); } catch (InterruptedException e) {}
				}
				navigate.stopMoving();
				Sound.twoBeeps();
			}

		}
		if(!isMeasured)
		{
			// we now need to correctly measure (0,0)
			// we'll do that by measuring the position of the robot with respect to the corner of the arena
			// then we'll relocate the origin from corner of arena to the intersection of the 2 grid lines (using the tile domension)
			//UPDATE = WE'LL BE MOVING ORIGIN TO GRIDLINES IN LIGHTLOCALIZER INSTEAD (A LOT MORE ACCURATE)
			double error, theta;
			for( ; ; ) 																				//Start with x
			{
				theta = odo.getTheta();
				if(theta <= 45)
					theta = theta + 360;
				error = odo.getTheta() - 180;														// we want angle to be 180 to corrcelty calculate US X distance
				if(error < 0.5) 																	// ERROR SHOULD BE <0.5 to break and measure
					break;
				navigate.setRotationSpeed((float) (-error - 20));									// Robot going clockwise; accelerated by random constant
				try { Thread.sleep(50); } catch (InterruptedException e) {}							// rotate until error reaches threshold
			}
			navigate.stopMoving();																	// robot facing x axis wall
			double xPrime = getFilteredData();											// latch US x reading

			for( ; ; ) 																				// now get y
			{
				theta = odo.getTheta();
				error = odo.getTheta() - 270;															// we want angle to be 270 to corrcelty calculate US Y distance
				if(error < 0.5) 																	// ERROR SHOULD BE <0.5 to break and measure
					break;
				navigate.setRotationSpeed((float) (-error - 20));									// Robot going clockwise; accelerated by random constant
				try { Thread.sleep(50); } catch (InterruptedException e) {}							// rotate until error reaches threshold
			}
			navigate.stopMoving();																	// robot now facing Y axis wall
			double yPrime = getFilteredData();											// latch US y reading
			// since x and y are from US and US is not center of rotation
			// we have to account for distance from US to center == 1
			double xCoord = xPrime - 1	;									// now we know where bot is with respect to arena corner
			double yCoord = yPrime - 1  ;

			odo.setX(-30 + xCoord);												// now we move (0,0) to arena corner
			odo.setY(-30+yCoord);

			xCoord = odo.getX();											// get NEW X and Y with respect to NEW (0,0)
			yCoord = odo.getY();
			double T = odo.getTheta();

		}
	}

}
