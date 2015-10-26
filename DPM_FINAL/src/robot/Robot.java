package robot;

public interface Robot {
	public void moveTo();
	
	public void turnTo(int angle);
	
	public int usScan();
	
	public int colorScan();
	
	public void capture();
	
	public void localize();
	
}
