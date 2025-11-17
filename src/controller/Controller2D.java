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
import model.rasterops.LineRasterizerBresenham;
import model.rasterops.PolygonRasterizer;
import model.rasterops.ScanLine;
import model.rasterops.ShapeRasterizer;
import model.rasterops.Suther;
import model.rasterops.Filler.FillerType;
import model.rasterops.FloodFill;
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

    private ArrayList<Point2D> polygon = new ArrayList<>();

    private int fillerIndex = 0;
    private FillerType fillerType = FillerType.Solid;

    private ArrayList<ShapeRasterizer> shapes = new ArrayList<>();

    private ArrayList<Point2D> rectangle;

    
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

        PolygonRasterizer novy = new PolygonRasterizer(cut.getPoints(), primaryColor, secondaryColor, fillerType);
        
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

        // vykreslení úsečky která se právě vytváří
        if (start != null) {
            Point2D newEnd = EditEndOnShift(start, end, shift, true);
            new LineRasterizerBresenham(start, newEnd, primaryColor, secondaryColor, fillerType).draw(panel.getRaster());
        }

        if (rectangle != null ) {
            new PolygonRasterizer(rectangle, primaryColor, secondaryColor, fillerType).draw(panel.getRaster());
        }

        // vykreslení polygonu který se právě vytváří
        new PolygonRasterizer(polygon, primaryColor, secondaryColor, fillerType).draw(panel.getRaster());

        panel.repaint();
    }
    private int switchFiller(int index) {

        int len = 4;

        if (index < 0) 
            index += len;

        switch (index) {
            case 0:
                fillerType = FillerType.Dashed;
                break;
            case 1:
                fillerType = FillerType.Transition;
                break;
            case 2:
                fillerType = FillerType.Inverted;
                break;
            case 3:
                fillerType = FillerType.Solid;
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
                // enter - dokončení polygonu
                if (e.getKeyCode() == 10) {
                    shapes.add(new PolygonRasterizer(polygon, primaryColor, secondaryColor, fillerType));
                    polygon = new ArrayList<>();
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
                    if (polygon.size() > 0) {
                        shapes.add(new PolygonRasterizer(polygon, primaryColor, secondaryColor, fillerType));
                        polygon = new ArrayList<>();
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
                        shapes.add(new ScanLine(pr.getPoints(), fillerType, primaryColor, secondaryColor));
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
                    FloodFill f = new FloodFill(new Point2D(e.getX(), e.getY()), primaryColor, secondaryColor, fillerType);
                    shapes.add(f);
                    reDraw();
                    lastFill = true;
                    return;
                }
                if (shapeTypeIndex == 2 && rectangle != null && rectangle.size() == 2) {
                    int sx = rectangle.get(1).x;
                    rectangle.add(new Point2D(sx, e.getY()));

                    int sx2 = rectangle.get(0).x;
                    rectangle.add(new Point2D(sx2, e.getY()));

                    shapes.add(new PolygonRasterizer(rectangle, primaryColor, secondaryColor, fillerType));
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

                    if (shapeTypeIndex != 2) {
                        end = new Point2D(e.getX(), e.getY());
                    }
                    else {
                        end = new Point2D(e.getX(), start.y);
                    }

                    // pokud kreslíme úsečku - přidat do listu
                    if (shapeTypeIndex == 0) {
                        shapes.add(new LineRasterizerBresenham(start, end, primaryColor, secondaryColor, fillerType));
                    }
                    else if (shapeTypeIndex == 2) {
                        rectangle = new ArrayList<>();
                        rectangle.add(start);
                        rectangle.add(end);
                    }
                    // pokud kreslíme polygon - přidat další bod
                    else {
                        end = EditEndOnShift(start, end, shift, true);
                        if (polygon.size() == 0)
                            polygon.add(start);

                        polygon.add(end);
                    }

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
                    if (shapeTypeIndex == 0 || shapeTypeIndex == 2 || polygon.size() == 0) {
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
