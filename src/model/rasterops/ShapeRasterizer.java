package model.rasterops;

import java.awt.Color;

import model.objectdata.ShapeType;
import model.rasterdata.Raster;
import model.rasterops.Filler.FillerType;

public abstract class ShapeRasterizer {

    protected int primaryColor = 0;
    protected int secondaryColor = 0;
    protected FillerType fillerType = FillerType.None;

    public void SetValues(Color primary, Color secondary, FillerType fillerType) {
        this.primaryColor = primary.getRGB();
        this.primaryColor = secondary.getRGB();
        this.fillerType = fillerType;
    }

    public abstract ShapeType getShapeType();

    public abstract void draw(Raster raster);
}
