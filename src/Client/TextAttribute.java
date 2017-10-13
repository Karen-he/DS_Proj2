package Client;

import java.io.Serializable;

public class TextAttribute implements Serializable{

    private Point point;
    private double lineWidth;
    private String color;
    private String text;

    public TextAttribute(Point point, double lineWidth, String color, String text){
        this.point = point;
        this.lineWidth = lineWidth;
        this.color = color;
        this.text = text;
    }

    public void setPoint(Point point){
        this.point = point;
    }

    public void setLineWidth(double lineWidth){
        this.lineWidth = lineWidth;
    }

    public void setColor(String color){
        this.color = color;
    }

    public void setText(String text){
        this.text = text;
    }

    public Point getPoint(){
        return point;
    }

    public double getLineWidth(){
        return lineWidth;
    }

    public String getColor(){
        return color;
    }

    public String getText(){
        return text;
    }
}
