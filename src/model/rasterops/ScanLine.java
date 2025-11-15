package model.rasterops;

import java.awt.Color;
import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;

public class ScanLine extends ShapeRasterizer {

    private ArrayList<Point2D> points;
    private ShapeFiller shapeFiller;

    public ScanLine(LineFiller filler, Point2D start, Point2D end) {
        super(filler, start, end);
    }
    public ScanLine(ArrayList<Point2D> points, ShapeFiller filler) {
        super(null, null, null);
        this.points = points;
        shapeFiller = filler;
    }

    public void fillPolygonScanline(Raster raster, Polygon polygon) {
        
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point2D p : polygon.getPoints()) {
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }

        for (int scanY = minY; scanY <= maxY; scanY++) {
            ArrayList<Integer> intersections = new ArrayList<>();

            for (int i = 0; i < polygon.size(); i++) {
                int x1 = polygon.getPoint(i).x;
                int y1 = polygon.getPoint(i).y;;
                int x2 = polygon.getPoint(i + 1).x;
                int y2 = polygon.getPoint(i + 1).y;

                if (y1 == y2) continue;

                if ((scanY >= Math.min(y1, y2)) && (scanY < Math.max(y1, y2))) {
                    int intersectX = x1 + (scanY - y1) * (x2 - x1) / (y2 - y1);
                    intersections.add(intersectX);
                }
            }

            intersections.sort(Integer::compareTo);

            for (int i = 0; i < intersections.size(); i += 2) {
                int xStart = intersections.get(i);
                int xEnd = intersections.get(i + 1);

                for (int x = xStart; x < xEnd; x++) {
                    Color color = shapeFiller.getColor(new Point2D(x, scanY), raster);
                    raster.setPixel(x, scanY, color.getRGB());
                }
            }
        }
    }

    @Override
    public ShapeType getShapeType() {
        return ShapeType.Fill;
    }

    @Override
    public void draw(Raster raster) {
        fillPolygonScanline(raster, new Polygon(points));
    }
}
