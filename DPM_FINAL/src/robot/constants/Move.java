package robot.constants;/*
 * Created by evanknox on 2015-11-10.
 */

public class Move {
    public static enum Direction{left,right,up,down}
    public Direction direction;

    public Move(Direction dir){
        this.direction = dir;
    }
}
