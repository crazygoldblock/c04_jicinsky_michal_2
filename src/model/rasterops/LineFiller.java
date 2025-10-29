package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

public interface LineFiller {
    public Color getColor(float t, int count, Point2D p, Raster raster);
    public void setColors(Color primary, Color secondary);
} 
