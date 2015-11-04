package robot.sensors;
import 	lejos.hardware.Sound;
import 	lejos.hardware.lcd.LCD;
import 	lejos.hardware.port.Port;
import 	lejos.hardware.sensor.EV3ColorSensor;
import 	lejos.robotics.SampleProvider;
import robot.constants.Color;
import 	robot.constants.Constants;

public class LeftLightSensor implements Runnable{

	
	private static LeftLightSensor ourInstance = new LeftLightSensor();
    public synchronized static LeftLightSensor getInstance(){
        return ourInstance;
    }

    private static EV3ColorSensor sensorLeft;
    private static SampleProvider lightSensorL;
   
    private float[] sampleLeft;


    //This will help manage the threads execution loop
    private boolean threadRunning;
    
    //Constructor
    private LeftLightSensor(){
        //First setup the sensor
        Port port1 = Constants.rightLightPort;
        sensorLeft = new EV3ColorSensor(port1);
        lightSensorL = sensorLeft.getRedMode();     
        sampleLeft = new float[lightSensorL.sampleSize()];
        sensorLeft.setFloodlight(true);
    }
	
	@Override
	public void run() {
		 threadRunning = true;
        
		 while (threadRunning){
			 lightSensorL.fetchSample(sampleLeft, 0);
			 	 
		 }
		
	}

}