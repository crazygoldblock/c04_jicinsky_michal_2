package model.rasterops;

import java.awt.Color;
import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;
import model.rasterops.Filler.FillerType;

public class FloodFill extends ShapeRasterizer {

    private Point2D point;

    public FloodFill(Point2D point, Color primary, Color secondary, FillerType type) {
        this.point = point;
        primaryColor = primary.getRGB();
        secondaryColor = secondary.getRGB();
        fillerType = type;
    }
    @Override
    public ShapeType getShapeType() {
        return ShapeType.Fill;
    }
    @Override
    public void draw(Raster raster) {
        int start = raster.getPixel(point.x, point.y);

        ArrayList<Point2D> list = new ArrayList<>();
        list.add(point);
        int count = 0;

        while (!list.isEmpty()) {
            Point2D current = list.remove(list.size() - 1);

            int x = current.x;
            int y = current.y;

            if (x < 0 || x > raster.getWidth() || y < 0 || y > raster.getHeight())
                continue;

            if (raster.getPixel(x, y) == start) {
                Color color = Filler.getColor(fillerType, current, raster, count++, new Color(primaryColor), new Color(secondaryColor), 0);
                raster.setPixel(x, y, color.getRGB());

                list.add(new Point2D(x + 1, y));
                list.add(new Point2D(x - 1, y));
                list.add(new Point2D(x, y + 1));
                list.add(new Point2D(x, y - 1));
            }
        }
    }
}
