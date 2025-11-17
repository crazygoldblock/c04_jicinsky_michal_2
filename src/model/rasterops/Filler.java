package model.rasterops;

import java.awt.Color;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

public class Filler {
    public enum FillerType {
        None,

        Solid,
        Inverted,
        Transition,
        Dashed,
    }
    public static Color getColor(
        FillerType type, 
        Point2D p, 
        Raster raster, 
        int count, 
        Color primary, 
        Color secondary, 
        float t
    ) {
        
        switch (type) {
            case Solid:
                return new Color(primary.getRGB());
            case Inverted:
                return new Color(0xffffff - raster.getPixel(p.x, p.y));
            case Transition:
                float r = primary.getRed() + (float)(secondary.getRed() - primary.getRed()) * t;
                float g = primary.getGreen() + (float)(secondary.getGreen() - primary.getGreen()) * t;
                float b = primary.getBlue() + (float)(secondary.getBlue() - primary.getBlue()) * t;
                return new Color((int)r, (int)g, (int)b);
            case Dashed:
                if (p.x % 15 < 8) {
                    if (p.y % 15 < 8)
                        return new Color(primary.getRGB());
                    else
                        return new Color(secondary.getRGB());
                }
                else {
                    if (p.y % 15 < 8)
                        return new Color(secondary.getRGB());
                    else
                        return new Color(primary.getRGB());
                }
            default:
                throw new RuntimeException("unknown filler");
        }
    }
}
