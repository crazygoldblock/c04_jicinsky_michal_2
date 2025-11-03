package model.rasterops;

import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;

public class FloodFill extends ShapeRasterizer {

    private ShapeFiller filler;
    private Point2D point;

    public FloodFill(ShapeFiller filler, Point2D point) {
        super(null, null, null);
        this.filler = filler;
        this.point = point;
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

        while (!list.isEmpty()) {
            Point2D current = list.remove(list.size() - 1);

            int x = current.x;
            int y = current.y;

            if (x < 0 || x > raster.getWidth() || y < 0 || y > raster.getHeight())
                continue;

            if (raster.getPixel(x, y) == start) {
                raster.setPixel(x, y, filler.getColor(new Point2D(x, y), raster).getRGB());
                //raster.setPixel(x, y, Color.RED.getRGB());

                list.add(new Point2D(x + 1, y));
                list.add(new Point2D(x - 1, y));
                list.add(new Point2D(x, y + 1));
                list.add(new Point2D(x, y - 1));
            }
        }
    }
}
