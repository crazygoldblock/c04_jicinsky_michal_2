package model.rasterops;

import java.util.ArrayList;

import model.objectdata.Point2D;
import model.objectdata.Polygon;

public class Suther {

    public static Polygon clipPolygon(Polygon subjectPolygon, Polygon clipPolygon) {
        ArrayList<Point2D> outputList = new ArrayList<>(subjectPolygon.getPoints());

        for (int i = 0; i < clipPolygon.size(); i++) {
            Point2D A = clipPolygon.getPoint(i);
            Point2D B = clipPolygon.getPoint((i + 1) % clipPolygon.size());

            ArrayList<Point2D> inputList = outputList;
            outputList = new ArrayList<>();

            if (inputList.isEmpty()) break;

            Point2D S = inputList.get(inputList.size() - 1);

            for (Point2D P : inputList) {
                boolean P_inside = isInside(A, B, P);
                boolean S_inside = isInside(A, B, S);

                if (S_inside && P_inside) {
                  
                    outputList.add(P);
                } else if (S_inside && !P_inside) {
                  
                    outputList.add(intersection(S, P, A, B));
                } else if (!S_inside && P_inside) {
                  
                    outputList.add(intersection(S, P, A, B));
                    outputList.add(P);
                }
                S = P;
            }
        }
        return new Polygon(outputList);
    }
    private static boolean isInside(Point2D A, Point2D B, Point2D P) {
        return cross(B, A, P) < 0;  
    }
    private static double cross(Point2D A, Point2D B, Point2D P) {
        return (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
    }
    private static Point2D intersection(Point2D S, Point2D P, Point2D A, Point2D B) {
         double dx1 = P.x - S.x;
        double dy1 = P.y - S.y;
        double dx2 = B.x - A.x;
        double dy2 = B.y - A.y;

        double denominator = dx1 * dy2 - dy1 * dx2;

        if (Math.abs(denominator) < 1e-10) {
            return new Point2D(S.x, S.y);
        }

        double t = ((A.x - S.x) * dy2 - (A.y - S.y) * dx2) / denominator;

        return new Point2D(
            (int)(S.x + t * dx1),
            (int)(S.y + t * dy1)
        );
    }
}
