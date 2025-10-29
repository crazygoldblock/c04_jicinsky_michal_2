package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

/*
 * vykreslení přechodu barev úsečky pomocí lineární interpolace
 */
public class TransitionFiller implements LineFiller {
    private Color cStart;
    private Color cEnd;

    public TransitionFiller(Color cStart, Color cEnd) {
        this.cStart = cStart;
        this.cEnd = cEnd;
    }
    public TransitionFiller() {}
    @Override
    public void setColors(Color primary, Color secondary) {
        cStart = primary;
        cEnd = secondary;
    }
    @Override
    public Color getColor(float t, int count, Point2D p, Raster raster) {
        float r = cStart.getRed() + (float)(cEnd.getRed() - cStart.getRed()) * t;
        float g = cStart.getGreen() + (float)(cEnd.getGreen() - cStart.getGreen()) * t;
        float b = cStart.getBlue() + (float)(cEnd.getBlue() - cStart.getBlue()) * t;

        return new Color((int)r, (int)g, (int)b);
    }
}
