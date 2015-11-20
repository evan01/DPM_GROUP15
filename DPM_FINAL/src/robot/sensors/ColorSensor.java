package robot.sensors;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import robot.constants.Color;
import robot.constants.Constants;

/**
 * This class represents the color sensor of our robot, it can continuously run or take specific samples
 */
public class ColorSensor implements Runnable{

    //This is a singleton class
    private static ColorSensor ourInstance = new ColorSensor();
    public synchronized static ColorSensor getInstance(){
        return ourInstance;
    }

    //Class variables
    private static EV3ColorSensor sensor;
    private static SampleProvider colorRGBSensor;
    private static Color color; //Represents the colour that was sensed

    private float[] sample;

    //This will help manage the threads execution loop
    private boolean threadRunning;


    //Constructor
    private ColorSensor(){
        //First setup the sensor
        Port port = Constants.colorPort;
        sensor = new EV3ColorSensor(port);
        colorRGBSensor = sensor.getRGBMode();
        sample = new float[colorRGBSensor.sampleSize()];
        sensor.setFloodlight(true);
        color = new Color();
    }

    //This ColourSensor can run as a thread when we need to use it to poll results
    @Override
    public void run() {
        threadRunning = true;
        //Color clr = new Color();

        while (threadRunning){

            colorRGBSensor.fetchSample(sample, 0);
            color.setR(sample[0]*1024);
            color.setG(sample[1]*1024);
            color.setB(sample[2]*1024);

            synchronized (this){
                setColor(color);
            }

            try {
                Thread.sleep(Constants.LIGHT_SENSOR_UPDATE_TIME);
            } catch (Exception e) {
            }
        }
    }

    public synchronized boolean isThreadRunning() {
        return threadRunning;
    }

    public synchronized void setThreadRunning(boolean threadRunning) {
        this.threadRunning = threadRunning;
    }

    public synchronized Color getColor() {
        return color;
    }

    public synchronized void setColor(Color color) {
        ColorSensor.color = color;
    }
    public synchronized Color scan(){
    	//Color clr = new Color();
    	colorRGBSensor.fetchSample(sample, 0);
        color.setR(sample[0]*1024);
        color.setG(sample[1]*1024);
        color.setB(sample[2]*1024);
        return color;
    }
}

// Setup color sensor
// 1. Create a port object attached to a physical port (done above)
// 2. Create a sensor instance and attach to port
// 3. Create a sample provider instance for the above and initialize operating mode
// public . Create a buffer for the sensor data