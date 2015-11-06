package robot.sensors;
import 	lejos.hardware.Sound;
import 	lejos.hardware.lcd.LCD;
import 	lejos.hardware.port.Port;
import 	lejos.hardware.sensor.EV3ColorSensor;
import 	lejos.robotics.SampleProvider;
import  robot.constants.Color;
import 	robot.constants.Constants;

public class LeftLightSensor implements Runnable{

	
	private static LeftLightSensor ourInstance = new LeftLightSensor();
    public synchronized static LeftLightSensor getInstance(){
        return ourInstance;
    }

    private static EV3ColorSensor sensorLeft;
    private static SampleProvider lightSensorL;
    private float lightIntensity;
    private float[] sampleLeft;


    //This will help manage the threads execution loop
    private boolean threadRunning;
    
    //Constructor
    private LeftLightSensor(){
        //First setup the sensor
        Port port1 = Constants.leftLightPort;
        sensorLeft = new EV3ColorSensor(port1);
        lightSensorL = sensorLeft.getRedMode();     
        sampleLeft = new float[lightSensorL.sampleSize()];
        sensorLeft.setFloodlight(true);
    }
	
	@Override
	public void run() {
		 threadRunning = true;
        
		 while (threadRunning){
			 fetchSample();
			 try {
	              Thread.sleep(40);
	            } catch (Exception e) {
	         }
		 }
		
	}
	
    public synchronized void fetchSample(){
    	lightSensorL.fetchSample(sampleLeft, 0); // acquire data
    	lightIntensity = sampleLeft[0]*100;  
    }
    public synchronized double scan(){
    	lightSensorL.fetchSample(sampleLeft, 0); // acquire data
        return (sampleLeft[0] * 100.0);
    }
    
    public synchronized float getIntensity() {
        return lightIntensity;
    }
    
    public synchronized boolean isThreadRunning() {
        return threadRunning;
    }

    public synchronized void setThreadRunning(boolean threadRunning) {
        this.threadRunning = threadRunning;
    }
	
	
	
	
	

}