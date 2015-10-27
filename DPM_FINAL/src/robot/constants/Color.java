package robot.constants;/*
 * Created by evanknox on 2015-10-18.
 */
//Simple data class to represent colours
public class Color {
    private double R;
    private double G;
    private double B;

    public boolean isSampleBlue(){
        if(this.R<this.B && this.G <this.B){
            return true;
        }else{
            return false;
        }
    }
    
    public boolean isBadSample(){
    	if (this.R<2 && this.B<2 && this.G<2){
    		return true;
    	}else{
    		return false;
    	}
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
