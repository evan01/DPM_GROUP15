package robot.constants;/*
 * Created by evanknox on 2015-10-30.
 */

/**
 * This class will represent a physical instruction given to the navigator, the purpose of this
 * is to create an instruction that will determine if the robot is moving in x or y and by how much
 */
public class NavInstruction {
    public double coordinate;
    public boolean movingInX;
}
