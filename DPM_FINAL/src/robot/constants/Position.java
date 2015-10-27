package robot.constants;/*
 * Created by evanknox on 2015-10-10.
 */

public class Position {
    private double x;
    private double y;
    private double theta;

    public Position(double x, double y, double theta){
        this.setX(x);
        this.setY(y);
        this.setTheta(theta);
    }

    public Position(){
        this.setTheta(0);
        this.setY(0);
        this.setX(0);
    }

    @Override
    public String toString(){
        String str = "[X:"+x+"][Y:"+y+"][Th:"+theta+"]";
        return str;
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getTheta() {
        return theta;
    }
    public void setTheta(double theta) {
        this.theta = theta;
    }
}
