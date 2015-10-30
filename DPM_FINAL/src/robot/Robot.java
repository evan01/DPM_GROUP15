package robot;

/**
 * This abstract class will allow us to define the methods that our robot needs to be able to do
 */
public abstract class Robot {
	public abstract void moveTo();
	
	/**
	 * This will rotate the robot to whatever angle is specified
	 * @param angle
	 */
	public abstract void turnTo(int angle);
	
	public abstract int usScan();
	
	public abstract int colorScan();
	
	public abstract void capture();
	
	public abstract void localize();
	
}
