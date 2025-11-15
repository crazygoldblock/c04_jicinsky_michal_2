package model.objectdata;

import java.util.ArrayList;

public class Polygon {
    
    private ArrayList<Point2D> points = new ArrayList<>();

    public Polygon() {}
    public Polygon(ArrayList<Point2D> points) {
        this.points = points;
    }
    public void addPoint(Point2D p) {
        points.add(p);
    }
    public int size() {
        return points.size();
    }
    public Point2D getPoint(int index) {
        return points.get(index % size());
    }
    public ArrayList<Point2D> getPoints() {
        return points;
    }
}
