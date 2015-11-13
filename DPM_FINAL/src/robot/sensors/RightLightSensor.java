package robot.sensors;
import 	lejos.hardware.Sound;
import 	lejos.hardware.lcd.LCD;
import 	lejos.hardware.port.Port;
import 	lejos.hardware.sensor.EV3ColorSensor;
import 	lejos.robotics.SampleProvider;
import robot.constants.Color;
import 	robot.constants.Constants;

public class RightLightSensor implements Runnable{

	
	private static RightLightSensor ourInstance = new RightLightSensor();
    public synchronized static RightLightSensor getInstance(){
        return ourInstance;
    }

    private static EV3ColorSensor sensorRight;
    private static SampleProvider lightSensorR;
    private float lightIntensity;
    private float[] sampleRight;
    public double TILE_COLOR_VALUE;


    //This will help manage the threads execution loop
    private boolean threadRunning;
    
    //Constructor
    private RightLightSensor(){
        //First setup the sensor
        Port port1 = Constants.rightLightPort;
        sensorRight = new EV3ColorSensor(port1);
        lightSensorR = sensorRight.getRedMode();     
        sampleRight = new float[lightSensorR.sampleSize()];
        sensorRight.setFloodlight(true);
        this.TILE_COLOR_VALUE=scanWithAverageFilter();
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
    	lightSensorR.fetchSample(sampleRight, 0); // acquire data
    	lightIntensity = sampleRight[0]*100;  
    }
    public synchronized double scan(){
    	lightSensorR.fetchSample(sampleRight, 0); // acquire data
        return (sampleRight[0] * 100.0);
    }
    
    public synchronized double scanWithAverageFilter(){
    	lightSensorR.fetchSample(sampleRight, 0); // acquire data
    	double reading1=sampleRight[0] * 100.0;
    	lightSensorR.fetchSample(sampleRight, 0); // acquire data
    	double reading2=sampleRight[0] * 100.0;
    	lightSensorR.fetchSample(sampleRight, 0); // acquire data
    	double reading3=sampleRight[0] * 100.0;
    	lightSensorR.fetchSample(sampleRight, 0); // acquire data
    	double reading4=sampleRight[0] * 100.0;
    	lightSensorR.fetchSample(sampleRight, 0); // acquire data
    	double reading5=sampleRight[0] * 100.0;
        double average=(reading1+reading2+reading3+reading4+reading5);
    	return average;
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