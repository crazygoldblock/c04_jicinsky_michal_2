package model.rasterops;

import java.awt.Color;
import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;
import model.rasterops.Filler.FillerType;

public class ScanLine extends ShapeRasterizer {

    private ArrayList<Point2D> points;

    public ScanLine(ArrayList<Point2D> points, FillerType type, Color primary, Color secondary) {
        this.points = points;
        primaryColor = primary.getRGB();
        secondaryColor = secondary.getRGB();
        fillerType = type;
    }

    public void fillPolygonScanline(Raster raster, Polygon polygon) {
        
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        int count = 0;

        for (Point2D p : polygon.getPoints()) {
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
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

                    float t1 = (maxX - minX) / (x - minX);
                    float t2 = (maxY - minY) / (scanY - minY);
                    float t = (t1 + t2) / 2;

                    Color color = Filler.getColor(fillerType, new Point2D(x, scanY), raster, count++, new Color(primaryColor), new Color(secondaryColor), t);
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
