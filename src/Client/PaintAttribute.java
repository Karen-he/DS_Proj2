package Client;

import java.io.Serializable;
import java.util.ArrayList;

public class PaintAttribute implements Serializable {

    private ArrayList<Point> pointList;
    private double lineWidth;
    private double[] color;
    private String text;

    public PaintAttribute(ArrayList<Point> pointList, double lineWidth, double[] color, String text){
        this.pointList = pointList;
        this.lineWidth = lineWidth;
        this.color = color;
        this.text = text;
    }

    public void setPointList(ArrayList<Point> pointList){
        this.pointList = pointList;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setColor(double[] color) {
        this.color = color;
    }

    public void setText(String text) {this.text = text;}

    public ArrayList<Point> getPointList(){
        return pointList;
    }

    public double getLineWidth(){
        return lineWidth;
    }

    public double[] getColor(){
        return color;
    }

    public String getText(){return text;}
}
