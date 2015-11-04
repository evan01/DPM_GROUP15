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
   
    private float[] sampleRight;


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
    }
	
	@Override
	public void run() {
		 threadRunning = true;
        
		 while (threadRunning){
			 lightSensorR.fetchSample(sampleRight, 0);
			 	 
		 }
		
	}

}