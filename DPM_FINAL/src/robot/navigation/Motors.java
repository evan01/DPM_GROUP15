package robot.navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import robot.constants.Constants;

/**
 * The purpose of this class is to provide a single access point for the motors
 */
public class Motors {
    private EV3LargeRegulatedMotor leftMotor;
    private EV3LargeRegulatedMotor rightMotor;
    private EV3LargeRegulatedMotor clawMotor;
    private EV3MediumRegulatedMotor armMotor;

    private static Motors ourInstance = new Motors();
    public static Motors getInstance(){
        return ourInstance;
    }

    private Motors(){
        try{
            leftMotor = new EV3LargeRegulatedMotor(Constants.leftMotorPort);
            rightMotor = new EV3LargeRegulatedMotor(Constants.rightMotorPort);
            armMotor =new EV3MediumRegulatedMotor(Constants.armPort);
            clawMotor = new EV3LargeRegulatedMotor(Constants.clawMotorPort);
        }catch(Exception e){
            //Sometimes something else may have control over motors
        }
    }

    public EV3LargeRegulatedMotor getLeftMotor() {
        return leftMotor;
    }

    public void setLeftMotor(EV3LargeRegulatedMotor leftMotor) {
        this.leftMotor = leftMotor;
    }

    public EV3LargeRegulatedMotor getRightMotor() {
        return rightMotor;
    }

    public void setRightMotor(EV3LargeRegulatedMotor rightMotor) {
        this.rightMotor = rightMotor;
    }
    public EV3LargeRegulatedMotor getClawMotor() {
        return clawMotor;
    }

    public void setClawMotor(EV3LargeRegulatedMotor clawMotor) {
        this.clawMotor = clawMotor;
    }

    public EV3MediumRegulatedMotor getArmMotor() {
        return armMotor;
    }

    public void setArmMotor(EV3MediumRegulatedMotor armMotor) {
        this.armMotor = armMotor;
    }
}
