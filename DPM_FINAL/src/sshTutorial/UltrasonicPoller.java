package sshTutorial;
import java.io.PrintWriter;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class UltrasonicPoller extends Thread{	
	private PrintWriter writer;
	private EV3UltrasonicSensor sensor;
	private SampleProvider us;
	//private UltrasonicController cont;
	private float[] usData;
	
	public UltrasonicPoller(SampleProvider us,EV3UltrasonicSensor sensor,PrintWriter writer){	//, float[] usData , UltrasonicController cont) {
		this.us = us;
		//this.cont = cont;
		//this.usData = usData;
		usData =new float[us.sampleSize()];
		this.sensor=sensor;
		this.writer=writer;
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	
	public void run() {
		try{
			for(int i=0 ;i<100 ;i ++){
				int distance = getDistance( );
				System.out.print(String.format("%d: %d%n",System.currentTimeMillis(), distance));
				writer.write(distance + "\n");
				try {Thread.sleep(100);} catch (InterruptedException e) {}
			}
		}finally{
			writer.close();
			sensor.close(); 
		}
	}
	
	public int getDistance(){
		int distance;
		//while (true) {
			us.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int
			if (distance>255){
				distance=255;
			}
			try { Thread.sleep(30); } catch(Exception e){}		// Poor man's timed sampling
			return distance;
		//}
	}
	
}
