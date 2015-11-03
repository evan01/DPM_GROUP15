package robot.sensors;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import robot.constants.Constants;

/**
 * This class represents the Ultrasonic sensor, you can order the sensor to continuously sense things or only scan at
 * certain moments.
 */
public class USSensor implements Runnable{

    //This is a singleton class, get the
    private static final USSensor ourInstance = new USSensor();

    public static USSensor getInstance() {
        return ourInstance;
    }

    private static EV3UltrasonicSensor usSensor;
    private static SampleProvider us;
    private static float[] usData;
    private int distance;
    int collisionsDetected;

    private boolean threadRunning;

    private USSensor() {
    	Port port = Constants.usPort;
        usSensor = new EV3UltrasonicSensor(port);
        us = usSensor.getDistanceMode();
        usData = new float[usSensor.sampleSize()];
    }

    /**
     * When you want to run the sensor as a thread you have the option, but you aren't limited to doing so
     */
    @Override
    public void run() {
        threadRunning = true;
        while (threadRunning) {
            fetchSample();
            try {
                Thread.sleep(40);
            } catch (Exception e) {
            }
        }
    }

    public synchronized void fetchSample(){
        us.fetchSample(usData, 0); // acquire data
        distance = (int) (usData[0] * 100.0);
    }

    /**
     * This method allows for single scans of the sensor
     * @return an integer representing the distance returned from a scan
     */
    public synchronized int scan(){
        us.fetchSample(usData, 0); // acquire data
        return (int) (usData[0] * 100.0);
    }

    /**
     *
     * @param sample a sample from the ultrasonic sensor
     * @return the filtered sample, cuts off sample at 30 cm if the sample is greater than 30
     */
    public static float filterSample(float sample){
        if (sample > 30) {                                                                        // filter out large
            // values
            sample = 30;
        }// thus makes
        // anything above the threshold distance irrelevant

        return sample;
    }

    public synchronized boolean isThreadRunning() {
        return threadRunning;
    }

    public synchronized void setThreadRunning(boolean threadRunning) {
        this.threadRunning = threadRunning;
    }

    public synchronized int getDistance() {
        return distance;
    }

    public synchronized void setDistance(int distance) {
        this.distance = distance;
    }

}
// Setup ultrasonic sensor, there are 4 steps involved:
// 1. Create a port object attached to a physical port (done already above)
// 2. Create a sensor instance and attach to port
// 3. Create a sample provider instance for the above and initialize operating mode
// 4. Create a buffer for the sensor data