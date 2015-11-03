package robot.sensors;
import 	lejos.hardware.Sound;
import 	lejos.hardware.lcd.LCD;
import 	lejos.hardware.port.Port;
import 	lejos.hardware.sensor.EV3ColorSensor;
import 	lejos.robotics.SampleProvider;
import robot.constants.Color;
import 	robot.constants.Constants;

public class LightSensor implements Runnable{

	
	private static LightSensor ourInstance = new LightSensor();
    public synchronized static LightSensor getInstance(){
        return ourInstance;
    }

    private static EV3ColorSensor sensorRight;
    private static EV3ColorSensor sensorLeft;
    private static SampleProvider lightSensorR;
    private static SampleProvider lightSensorL;
    
    private float[] sampleRight;
    private float[] sampleLeft;

    //This will help manage the threads execution loop
    private boolean threadRunning;
    
    //Constructor
    private LightSensor(){
        //First setup the sensor
        Port port1 = Constants.rightLightPort;
        Port port2 = Constants.leftLightPort;
        sensorRight = new EV3ColorSensor(port1);
        sensorLeft = new EV3ColorSensor(port2);
        lightSensorR = sensorRight.getRedMode();
        lightSensorL = sensorLeft.getRedMode();
        
        sampleRight = new float[lightSensorR.sampleSize()];
        sampleLeft  = new float[lightSensorL.sampleSize()];
        sensorRight.setFloodlight(true);
        sensorLeft.setFloodlight(true);
    }

	
	@Override
	public void run() {
		 threadRunning = true;
		 
		 while (threadRunning){
			 lightSensorR.fetchSample(sampleRight, 0);
			 lightSensorL.fetchSample(sampleLeft, 0);
			 
			 
		 }
		
	}

}
