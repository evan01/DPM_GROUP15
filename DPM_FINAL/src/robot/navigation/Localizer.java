package robot.navigation;
import lejos.hardware.Button;
import lejos.hardware.LED;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import robot.sensors.USSensor;

public class Localizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }

	public static float ROTATION_SPEED = 70;
	

	private Odometer odo;
	private Navigation navigate;
	private USSensor us;
	private LocalizationType locType;
	
	private double usDistance; 
	private final double USDISTANCE_THRESHOLD = 35;
//	private final double TILE_DIMENSION = 30;		not needed anymore
	private static boolean isMeasured  = false;
	
	public Localizer(Odometer odo, LocalizationType locType) {
		this.odo = odo;
		this.us = USSensor.getInstance();
		this.locType = locType;
		this.navigate = Navigation.getInstance();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB , deltaTheta ;
		
		if (locType == LocalizationType.FALLING_EDGE) 
		{
			usDistance = getFilteredData();															// measure USdistance
			
				navigate.setRotationSpeed(ROTATION_SPEED);											// start turning
				
				LCD.drawString("wait to clear of wall ", 0,1);									    // GET CLEAR OF THE WALL
				while((usDistance = getFilteredData()) < USDISTANCE_THRESHOLD) {
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}
				try { Thread.sleep(1000); } catch (InterruptedException e) {}

				LCD.drawString("wait for wall hit   ", 0,1);										// 	Wait for a wall to be seen
				while((usDistance = getFilteredData()) > USDISTANCE_THRESHOLD) {
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}	
																								  	// Wall is seen !!
				angleA = odo.getAng();															  	// latch angle A
				LCD.drawString("angle A: "+angleA, 0,1);										  	// debugging tool
				Sound.buzz();
				
				navigate.setRotationSpeed(-ROTATION_SPEED); 									 	 // switch rotation direction
				
				
				LCD.drawString("wait to clear of wall 1 ", 0,1);								  	// now get clear of that wall again
				while((usDistance = getFilteredData()) < USDISTANCE_THRESHOLD) {				    // (we don't want the same wall to be detected)
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}
					
				try { Thread.sleep(1000); } catch (InterruptedException e) {}			           
				
				
				LCD.drawString("wait for wall hit   ", 0,1);
				while((usDistance = getFilteredData()) > USDISTANCE_THRESHOLD) {					// wait until you see 2nd wall !
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}
																								   // 2nd wall seen !!
				angleB = odo.getAng();															   // latch angle B 																		   //2nd wall detected
				LCD.drawString("angle B: "+angleB, 0,2);										   // debugging tool
				Sound.buzz(); 

				//use formulas																				
				if(angleA > angleB)
					angleB+=360;
				deltaTheta = 42-((angleA + angleB)/2);													
				Sound.beep();
				odo.correctTheta(deltaTheta);														// adds the error dtheta onto the odomerters theta
				LCD.drawString("Theta: "+odo.getAng()+"  ", 0,4);
				
																									// now we MEASURE
																									// measuring x and y can be found belowwww
				if(isMeasured) 
				{																					// x, y already measured with respect to new (0,0)!
					LCD.drawString("Finding 0  ", 0,5);
					navigate.setRotationSpeed(10); 												    // rotate slowly
					for( ; ; ) 																		// orienting robot
					{																				// Exit only if robot is with 
						double theta = odo.getAng();
						if(theta > 180) 
						theta -= 360;
						if(theta >= 0 && theta < 20) 												
						break;
						try { Thread.sleep(100); } catch (InterruptedException e) {}
					}
					navigate.stopMoving();
					LCD.drawString(" DONE.", 9,5);
					Sound.twoBeeps();	
				}
				LCD.drawString("Theta : "+odo.getAng()+"  ", 0,3);		
				

		} else 
		{
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			usDistance = getFilteredData();															// measure USdistance

				navigate.setRotationSpeed(ROTATION_SPEED);											// start turning
				
				LCD.drawString("wait to hit wall ", 0,1);									     	// Wait to hit the wall
				while((usDistance = getFilteredData()) == USDISTANCE_THRESHOLD) {
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
			
				LCD.drawString("wait to clear off wall   ", 0,1);									// 	Now get clear of wall
				while((usDistance = getFilteredData()) < USDISTANCE_THRESHOLD) {
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}	
																								  // Wall is cleared !!
				angleA = odo.getAng();															  // latch angle A
				LCD.drawString("angle A: "+angleA, 0,1);										  // debugging tool
				Sound.buzz();
																	
				navigate.setRotationSpeed(-ROTATION_SPEED); 										// switch rotation direction
				
				
				LCD.drawString("wait to hit wall 2 ", 0,1);										    // now go to other wall
				while((usDistance = getFilteredData()) < USDISTANCE_THRESHOLD) {					// (we don't want the same wall to be detected)
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}
					
				try { Thread.sleep(1000); } catch (InterruptedException e) {}			           
				
				
				LCD.drawString("wait to clear off wall   ", 0,1);
				while((usDistance = getFilteredData()) == USDISTANCE_THRESHOLD) {				   // wait to clear off first wall!
					LCD.drawString("US: "+usDistance+"  ", 0, 0);
				}
																								   // 2nd wall cleared !!
				angleB = odo.getAng();															   // latch angle B 																		   //2nd wall detected
				LCD.drawString("angle B: "+angleB, 0,2);										   // debugging tool
				Sound.buzz(); 

			// use formulas
			if(angleA < angleB)																		
			{  
				angleB+=360;																		
			}
			deltaTheta = 225-((angleA + angleB)/2);		
			odo.correctTheta(deltaTheta); 															// ensure theta doesnt go out of bounds
			Button.LEDPattern(3);													
	
			LCD.drawString("Theta: "+odo.getAng()+"  ", 0,4);
			
			try { Thread.sleep(2000); } catch (InterruptedException e) {}
																									// now we MEASURE
			if(isMeasured) {																		// x, y already measured !
				LCD.drawString("Finding 0  ", 0,5);
				navigate.setRotationSpeed(-10); 													// rotate slowly
				for( ; ; ) 																			// orienting robot
				{																					// exit
					double theta = odo.getAng();
					if(theta > 180) 
						theta -= 360;
					if(theta <= 0 && theta > -20) 
						break;
					try { Thread.sleep(100); } catch (InterruptedException e) {}
				}
				navigate.stopMoving();
				LCD.drawString(" DONE.", 9,5);
				Sound.twoBeeps();
			}
			LCD.drawString("Theta: "+odo.getAng()+"  ", 0,3);
			
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
				theta = odo.getAng();
				if(theta <= 45) 																	
				theta = theta + 360; 																
				error = odo.getAng() - 270;															// we want angle to be 270 to corrcelty calculate US X distance
				if(error < 0.5) 																	// ERROR SHOULD BE <0.5 to break and measure
					break;
				navigate.setRotationSpeed((float) (-error - 20));									// Robot going clockwise; accelerated by random constant
				try { Thread.sleep(50); } catch (InterruptedException e) {}							// rotate until error reaches threshold
			}
			navigate.stopMoving();																	// robot facing x axis wall
			double xPrime = usDistance = getFilteredData();											// latch US x reading
			
			LCD.clear();																			
			LCD.drawString("X :"+usDistance, 0,1);													// Store X
			for( ; ; ) 																				// now get y
			{
				theta = odo.getAng();																
				error = odo.getAng() - 180;															// we want angle to be 180 to corrcelty calculate US Y distance
				if(error < 0.5) 																	// ERROR SHOULD BE <0.5 to break and measure
					break;
				navigate.setRotationSpeed((float) (-error - 20));									// Robot going clockwise; accelerated by random constant
				try { Thread.sleep(50); } catch (InterruptedException e) {}							// rotate until error reaches threshold
			}
			navigate.stopMoving();																	// robot now facing Y axis wall
			double yPrime = usDistance = getFilteredData();											// latch US y reading
			LCD.drawString("Y: "+usDistance, 0,1);
																			// since x and y are from US and US is not center of rotation
																			// we have to account for distance from US to center == 1
			double xCoord = xPrime - 1	;									// now we know where bot is with respect to arena corner
			double yCoord = yPrime - 1  ;
																						
			odo.setX(-30+xCoord);												// now we move (0,0) to arena corner
			odo.setY(-30+yCoord);
		
			xCoord = odo.getX();											// get NEW X and Y with respect to NEW (0,0)
			yCoord = odo.getY();
			double T = odo.getAng();
			
			LCD.drawString("X "+xCoord+"\nY: "+yCoord+"\nT: "+T+" ", 0,3);
		}
	}
	
	
	
	private float getFilteredData() {
		int dist;
		dist = us.getDistance();
														// thus makes anything above the threshold distance irrelevant					
		return dist;
	}

}
