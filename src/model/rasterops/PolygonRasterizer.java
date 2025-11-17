package model.rasterops;

import java.awt.Color;
import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;
import model.rasterops.Filler.FillerType;

public class PolygonRasterizer extends ShapeRasterizer {

    private ArrayList<Point2D> points = new ArrayList<>();

    public PolygonRasterizer(ArrayList<Point2D> points, Color primary, Color secondary, FillerType type) {
        this.points = points;
        primaryColor = primary.getRGB();
        secondaryColor = secondary.getRGB();
        fillerType = type;
    }

    @Override
    public ShapeType getShapeType() {
        return ShapeType.Mnohouhelnik;
    }
    @Override
    public void draw(Raster raster) {

        if (points.size() == 0)
            return;

        

        for(int i = 0; i < points.size() - 1; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i + 1);

            LineRasterizerBresenham line = new LineRasterizerBresenham(p1, p2, new Color(primaryColor), new Color(secondaryColor), fillerType);
            line.draw(raster);
        }

        Point2D p1 = points.get(points.size() - 1);
        Point2D p2 = points.get(0);

        LineRasterizerBresenham line = new LineRasterizerBresenham(p1, p2, new Color(primaryColor), new Color(secondaryColor), fillerType);
        line.draw(raster);
    }
    public ArrayList<Point2D> getPoints() {
        return points;
    }
}
