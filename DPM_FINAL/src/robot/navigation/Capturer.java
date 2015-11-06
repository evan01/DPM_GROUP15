package robot.navigation;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

/**
 * This class will take care of capturing a single block off the ground.
 */
public class Capturer {

//    EV3MediumRegulatedMotor armMotor = Motors.getInstance().getArmMotor();
//    EV3LargeRegulatedMotor clawMotor = Motors.getInstance().getClawMotor();
    boolean hasStyro;

    public static void capture(){

    }

    /**
     * Controls the arm motor to force robot to grab the actual block
     */
    
//    public void grab() {
//        Navigation.getInstance().goForward(3);
//        armMotor.backward();
//        armMotor.setSpeed(150);
//        armMotor.rotate(180);
//        Delay.msDelay(250);
//        armMotor.stop();
//        this.hasStyro = true;
//    }

    public boolean hasStyro() {
        boolean foo;
        synchronized (this) {
            foo = hasStyro;
        }
        return foo;
    }
}
