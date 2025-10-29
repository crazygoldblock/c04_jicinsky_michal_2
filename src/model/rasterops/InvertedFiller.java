package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

public class InvertedFiller implements ShapeFiller {

    @Override
    public Color getColor(Point2D p, Raster raster) {
        int c = raster.getPixel(p.x, p.y);
        int inv = 0xffffff - c;
        return new Color(inv);
    }

    @Override
    public void setColors(Color primary, Color secondary) {
        // nic
    }
    
}
