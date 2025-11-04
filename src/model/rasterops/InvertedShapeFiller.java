package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

public class InvertedShapeFiller implements ShapeFiller {


    @Override
    public Color getColor(Point2D p, Raster raster) {
        int color = raster.getPixel(p.x, p.y);
        int inv = 0xFFFFFF - color;
        return new Color(inv);
    }

    @Override
    public void setColors(Color primary, Color secondary) {
        
    }
    
}
