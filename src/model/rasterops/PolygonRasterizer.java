package model.rasterops;

import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;

public class PolygonRasterizer extends ShapeRasterizer {

    public PolygonRasterizer(LineFiller filler, Point2D start, Point2D end) {
        super(filler, start, end);
    }
    public PolygonRasterizer() {
        super(null, null, null);
    }
    @Override
    public ShapeType getShapeType() {
        return ShapeType.Mnohouhelnik;
    }

    @Override
    public void setPoints(Point2D start, Point2D end) {
        if (points.size() == 0) {
            points.add(start);
            points.add(end);
        }
        else {
            points.add(end);
        }
    }
    public int Size() {
        return points.size();
    }

    private ArrayList<Point2D> points = new ArrayList<>();

    @Override
    public void draw(Raster raster) {

        if (points.size() == 0)
            return;

        LineRasterizerBresenham line = new LineRasterizerBresenham();
        line.setFiller(filler);

        for(int i = 0; i < points.size() - 1; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i + 1);

            line.setPoints(p1, p2);
            line.draw(raster);
        }

        Point2D p1 = points.get(points.size() - 1);
        Point2D p2 = points.get(0);

        line.setPoints(p1, p2);
        line.draw(raster);
    }

}
