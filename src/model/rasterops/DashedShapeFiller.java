package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

public class DashedShapeFiller implements ShapeFiller {

    private Color primary;
    private Color secundary;

    @Override
    public Color getColor(Point2D p, Raster raster) {
        if (p.x % 20 < 10) {
            if (p.y % 20 < 10)
                return primary;
            else
                return secundary;
        }
        else {
            if (p.y % 20 < 10)
                return secundary;
            else
                return primary;
        }
    }

    @Override
    public void setColors(Color primary, Color secondary) {
        this.primary = primary;
        this.secundary = secondary;
    }
    
}
