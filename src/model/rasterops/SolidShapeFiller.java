package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

public class SolidShapeFiller implements ShapeFiller {

    Color color;

    @Override
    public Color getColor(Point2D p, Raster raster) {
        return color;
    }

    @Override
    public void setColors(Color primary, Color secondary) {
        color = primary;
    }
    
}
