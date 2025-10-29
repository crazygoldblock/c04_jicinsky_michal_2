package model.rasterops;

import model.objectdata.Point2D;
import model.objectdata.ShapeType;
import model.rasterdata.Raster;
import model.rasterdata.RasterBI;

import java.awt.*;

public class LineRasterizerGraphics extends ShapeRasterizer {
    public LineRasterizerGraphics(LineFiller filler, Point2D start, Point2D end) {
        super(filler, start, end);
    }
    @Override
    public ShapeType getShapeType() {
        return ShapeType.Usecka;
    }
    @Override
    public void draw(Raster raster) {
        Graphics g = ((RasterBI) raster).getImg().createGraphics();
        g.setColor(filler.getColor(0, 0, new Point2D(0, 0), raster));
        g.drawLine(start.x, start.y, end.x, end.y);
    }
}
