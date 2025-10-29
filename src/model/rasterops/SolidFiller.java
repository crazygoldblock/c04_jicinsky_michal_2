package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

public class SolidFiller implements LineFiller {

    private Color color;

    public SolidFiller(Color color) {
        this.color = color;
    }
    public SolidFiller() {}
    @Override
    public void setColors(Color primary, Color secondary) {
        color = primary;
    }
    @Override
    public Color getColor(float t, int count, Point2D p, Raster raster) {
        return color;
    }
}
