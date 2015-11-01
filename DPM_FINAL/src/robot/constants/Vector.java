package robot.constants;

/**
 * This class just repreents an angle and a distance, can be used for odometry correction
 */
public class Vector {
	double angle;
	int distance;
	
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
}
