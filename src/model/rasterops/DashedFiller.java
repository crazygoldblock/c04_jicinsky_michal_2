package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

/*
 * vykreslení přerušované čáry
 */
public class DashedFiller implements LineFiller {
    private Color color;

    public DashedFiller(Color color) {
        this.color = color;
    }
    public DashedFiller() {}
    @Override
    public void setColors(Color primary, Color secondary) {
        color = primary;
    }
    @Override
    public Color getColor(float t, int count, Point2D p, Raster raster) {
        if ((count / 10) % 2 == 1) {
            return new Color(raster.getPixel(p.x, p.y));
        }
        else {
            return color;
        }
    }
}
