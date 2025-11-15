package controller;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.objectdata.ShapeType;
import model.rasterops.LineFiller;
import model.rasterops.LineRasterizerBresenham;
import model.rasterops.PolygonRasterizer;
import model.rasterops.ScanLine;
import model.rasterops.ShapeFiller;
import model.rasterops.ShapeRasterizer;
import model.rasterops.SolidFiller;
import model.rasterops.SolidShapeFiller;
import model.rasterops.Suther;
import model.rasterops.DashedFiller;
import model.rasterops.DashedShapeFiller;
import model.rasterops.FloodFill;
import model.rasterops.TransitionFiller;
import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;

    private Point2D start;
    private Point2D end;
    private boolean shift = false;
    private boolean control = false;
    private boolean fill = false;
    private boolean lastFill = true;
    private boolean lastRect = false;

    private int primaryColorIndex = 6;
    private Color primaryColor = Color.ORANGE;

    private int secondaryColorIndex = 2;
    private Color secondaryColor = Color.GREEN;

    private int shapeTypeIndex = 0;
    
    private LineRasterizerBresenham line = new LineRasterizerBresenham();

    private PolygonRasterizer polygon = new PolygonRasterizer();

    private int fillerIndex = 2;
    private LineFiller filler = new SolidFiller(primaryColor);

    private ArrayList<ShapeRasterizer> shapes = new ArrayList<>();

    private PolygonRasterizer rectangle;

    
    public Controller2D(Panel panel) {
        this.panel = panel;

        initObjects();
        initListeners(panel);
    }
    @Override
    public void initObjects() {
        panel.clear();
        panel.repaint();
    }
    private void useSuther() {
        if (shapes.size() < 2)
            return;

        ShapeRasterizer sr1 = shapes.get(shapes.size() - 2);
        PolygonRasterizer p1;

        if (sr1.getShapeType() == ShapeType.Mnohouhelnik)
            p1 = (PolygonRasterizer)sr1;
        else
            return;

        ShapeRasterizer sr2 = shapes.get(shapes.size() - 1);
        PolygonRasterizer p2;

        if (sr2.getShapeType() == ShapeType.Mnohouhelnik)
            p2 = (PolygonRasterizer)sr2;
        else
            return;

        shapes.remove(shapes.size() - 1);
        shapes.remove(shapes.size() - 1);

        Polygon cut = Suther.clipPolygon(new Polygon(p1.getPoints()), new Polygon(p2.getPoints()));

        PolygonRasterizer novy = new PolygonRasterizer(cut.getPoints());
        novy.setFiller(filler);
        
        switchFiller(fillerIndex - 1);

        shapes.add(novy);
    }
    private Point2D EditEndOnShift(Point2D start, Point2D end, boolean shift, boolean straight) {
        if (shift) {
            int lenX = Math.abs(start.x - end.x);
            int lenY = Math.abs(start.y - end.y);
            int minLen = Math.min(lenX, lenY);
            int maxLen = Math.max(lenX, lenY);
            int signX = (int)Math.signum((float)end.x - start.x);
            int signY = (int)Math.signum((float)end.y - start.y);
            int notStraight = (straight && minLen < maxLen / 2) ? 0 : 1;

            if (lenX > lenY) {
                int y = start.y + lenX * signY * notStraight;
                return new Point2D(end.x, y);
            }  
            else {
                int x = start.x + lenY * signX * notStraight;
                return new Point2D(x, end.y);
            }
        }
        return new Point2D(end.x, end.y);
    }
    private void reDraw() {

        // překreslení plátna při změně

        panel.clear();

        // vykreslení všech nakreslených útvarů
        for (ShapeRasterizer shape : shapes) {
            shape.draw(panel.getRaster());
        }

        filler.setColors(primaryColor, secondaryColor);

        // vykreslení úsečky která se právě vytváří
        if (start != null) {
            Point2D newEnd = EditEndOnShift(start, end, shift, true);
            
            line.setPoints(start, newEnd);
            line.setFiller(filler);

            line.draw(panel.getRaster());
        }

        if (rectangle != null ) {
            rectangle.setFiller(filler);
            rectangle.draw(panel.getRaster());
        }

        // vykreslení polygonu který se právě vytváří
        polygon.setFiller(filler);
        polygon.draw(panel.getRaster());

        panel.repaint();
    }
    private int switchFiller(int index) {

        int len = 3;

        if (index < 0) 
            index += len;

        switch (index) {
            case 0:
                filler = new TransitionFiller();
                break;
            case 1:
                filler = new SolidFiller();
                break;
            case 2:
                filler = new DashedFiller();
                break;
            default:
                throw new RuntimeException("barva");
        }
        return (index + 1) % len;
    }
    private int switchShapeType(int index) {

        int len = 3;

        if (index < 0) 
            index += len;

        switch (index) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                throw new RuntimeException("shape type");
        }
        return (index + 1) % len;
    }
    private Color switchColor(int index) {
        switch (index) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.RED;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.ORANGE;
            case 6:
                return Color.CYAN;
            case 7:
                return Color.MAGENTA;
        }
        throw new RuntimeException("barva");
    }
    @Override
    public void initListeners(Panel panel) {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // shift
                if (e.getKeyCode() == 16) {
                    shift = true;
                } 
                // ctrl
                if (e.getKeyCode() == 17) {
                    control = true;
                } 
            }
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println(e.getKeyCode());
                // enter - dokončení polygonu
                if (e.getKeyCode() == 10) {
                    shapes.add(polygon);
                    polygon = new PolygonRasterizer();
                    switchFiller(fillerIndex - 1);
                } 
                // shift
                if (e.getKeyCode() == 16) {
                    shift = false;
                } 
                // ctrl
                if (e.getKeyCode() == 17) {
                    control = false;
                } 
                // levá šipka - změna primární barvy
                if (e.getKeyCode() == 37) {
                    primaryColor = switchColor(primaryColorIndex++);
                    primaryColorIndex = primaryColorIndex % 8;
                }
                // pravá šipka - změna sekundární barvy
                if (e.getKeyCode() == 39) {
                    secondaryColor = switchColor(secondaryColorIndex++);
                    secondaryColorIndex = secondaryColorIndex % 8;
                }
                // dolů šipka - změna útvaru
                if (e.getKeyCode() == 40) {
                    if (polygon.Size() > 0) {
                        shapes.add(polygon);
                        polygon = new PolygonRasterizer();
                    }
                    if (rectangle != null) {
                        rectangle = null;
                    }
                    shapeTypeIndex = switchShapeType(shapeTypeIndex);
                }
                // nahoru šipka - změna stylu vykreslení
                if (e.getKeyCode() == 38) {
                    fillerIndex = switchFiller(fillerIndex);
                }
                // C - smazání plátna
                if (e.getKeyCode() == 67) {
                    shapes.clear();
                }
                // Z - smazání posledního útvaru
                if (e.getKeyCode() == 90) {
                    if (control && shapes.size() > 0) 
                        shapes.remove(shapes.size() - 1);
                }
                // f - fill
                if (e.getKeyCode() == 70) {
                    fill = !fill;
                } 
                // s - vyplnění posledního polygonu 
                if (e.getKeyCode() == 83) {
                    if (shapes.size() != 0 && shapes.get(shapes.size() - 1).getShapeType() == ShapeType.Mnohouhelnik) {
                        PolygonRasterizer pr = (PolygonRasterizer)shapes.get(shapes.size() - 1);
                        ShapeFiller sf = new DashedShapeFiller();
                        sf.setColors(primaryColor, secondaryColor); 
                        shapes.add(new ScanLine(pr.getPoints(), sf));
                    }
                } 
                // q - ořezání polygonu
                if (e.getKeyCode() == 81) {
                    useSuther();
                } 
                reDraw();
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (fill && !lastFill) {
                    ShapeFiller shapeFiller = new SolidShapeFiller(); 
                    shapeFiller.setColors(primaryColor, secondaryColor);
                    FloodFill f = new FloodFill(shapeFiller, new Point2D(e.getX(), e.getY()));
                    shapes.add(f);
                    reDraw();
                    lastFill = true;
                    return;
                }
                if (shapeTypeIndex == 2 && rectangle != null && rectangle.Size() == 2) {
                    int sx = rectangle.getPoints().get(1).x;
                    rectangle.setPoints(null, new Point2D(sx, e.getY()));

                    int sx2 = rectangle.getPoints().get(0).x;
                    rectangle.setPoints(null, new Point2D(sx2, e.getY()));

                    shapes.add(rectangle);
                    rectangle = null;
                    reDraw();
                    lastRect = true;
                    return;
                }
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                lastFill = false;
                lastRect = false;

                // pokud start není null tak tohle první zavolání této funkkce po dokončení vytváření úsečky
                if (start != null) {

                    System.out.println(e.getX() + ", " + e.getY());

                    if (shapeTypeIndex != 2) {
                        end = new Point2D(e.getX(), e.getY());
                    }
                    else {
                        end = new Point2D(e.getX(), start.y);
                    }

                    // pokud kreslíme úsečku - přidat do listu
                    if (shapeTypeIndex == 0) {
                        shapes.add(line);
                        line = new LineRasterizerBresenham();
                    }
                    else if (shapeTypeIndex == 2) {
                        rectangle = new PolygonRasterizer();
                        rectangle.setPoints(start, end);
                    }
                    // pokud kreslíme polygon - přidat další bod
                    else {
                        end = EditEndOnShift(start, end, shift, true);
                        polygon.setPoints(start, end);
                    }

                    switchShapeType(shapeTypeIndex - 1);
                    primaryColor = new Color(primaryColor.getRGB());
                    secondaryColor = new Color(secondaryColor.getRGB());
                    switchFiller(fillerIndex - 1);

                    start = null;              
                    
                    reDraw();
                }
            }
            public void mouseDragged(MouseEvent e) {

                if (fill && !lastFill) {
                    return;
                }
                if (lastRect) {
                    return;
                }

                // pokud je start null = tohle je začátek vytváření pového útvaru
                if (start == null) {
                    // pokud kreslíme polygon start musí být konec poslední úsečky
                    if (shapeTypeIndex == 0 || shapeTypeIndex == 2 || polygon.Size() == 0) {
                        start = new Point2D(e.getX(), e.getY());
                    }
                    else {
                        start = new Point2D(end.x, end.y);
                    }
                }
                    
                if (shapeTypeIndex != 2) {
                    end = new Point2D(e.getX(), e.getY());
                }
                else {
                    end = new Point2D(e.getX(), start.y);
                }
               
                reDraw();
            };
        });
    }
}
