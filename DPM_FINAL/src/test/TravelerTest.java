package test;/*
 * Created by evanknox on 2015-11-10.
 */

import robot.navigation.LightLocalizerTwo;
import robot.navigation.Odometer;
import robot.navigation.Traveler;

public class TravelerTest {
    public static void main(String args[]){
        //Using the travler should be as easy as this!
    	Odometer.getInstance();
    	LightLocalizerTwo ll = new LightLocalizerTwo();
    	ll.lightLocalize();
        Traveler trav = new Traveler();
        trav.goTo(2,2);
       // trav.goTo(0,0);
        //trav.goTo(1,3);
    }
}
