package Client;

import java.util.ArrayList;

public class Coordinates {

    private ArrayList<Point> coordinate = new ArrayList<Point>();

    public Coordinates(ArrayList<Point> coordinate){
        this.coordinate = coordinate;
    }

    public void setCoordinate(ArrayList<Point> coordinate){
        this.coordinate = coordinate;
    }

    public ArrayList<Point> getCoordinate(){
        return coordinate;
    }
}
