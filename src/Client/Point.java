package Client;

import java.io.Serializable;

public class Point implements Serializable {

    private double pointX;
    private double pointY;

    public Point(double pointX, double pointY){
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public void setPoint(double pointX, double pointY){
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public double getPointX() {
        return pointX;
    }

    public double getPointY(){
        return pointY;
    }
}
