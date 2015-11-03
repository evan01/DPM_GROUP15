package robot.constants;/*
 * Created by evanknox on 2015-10-18.
 */
/**
 * 
 * 
 *
 */
public class Color {
    private double R;
    private double G;
    private double B;

    public boolean isSampleRed(){
        return this.R > this.G && this.R > this.B;
    }
    //if sample is not red or blue, it compares the ratio of each
    //value to 90% as white colour has similar RGB values
    public boolean isSampleWhite(){
    	if(!isSampleBlue() && !isSampleRed()){
            return (this.R / this.B) * 100 >= 90 && (this.R / this.G) * 100 >= 90;
    	} else{
    		return false;
    	}
    }
    
    //if sample is not blue, it compares the ratio of red and green
    //if green is not less than 30% of red value it is yellow and
    //red is bigger than green and blue
    public boolean isSampleYellow(){
    	if(!isSampleBlue()){
            return this.R > this.B && this.R > this.G && !isSampleWhite()
                    && !((this.G / this.R) * 100 <= 30);
    	} else{
    		return false;
    	}
    }
    public boolean isSampleBlue(){
        return this.R < this.B && this.G < this.B;
    }
    
    public boolean isBadSample(){
        return this.R < 2 && this.B < 2 && this.G < 2;
    }

    public double getB() {
        return B;
    }

    public void setB(double b) {
        B = b;
    }

    public double getR() {
        return R;
    }

    public void setR(double r) {
        R = r;
    }

    public double getG() {
        return G;
    }

    public void setG(double g) {
        G = g;
    }
}
