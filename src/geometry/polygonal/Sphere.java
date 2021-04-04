package geometry.polygonal;

import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Sphere extends Polyhedron {

    /**
     * @return Получить центр сферы(совпадает с нулевой точкой)
     */
    public Point3D getCenter() {
        return zero;
    }

    public void setCenter(Point3D center) {
        this.zero = center;
    }

    public Sphere(Point3D center, double R, int step, Color c) {
        super(center);
        Set<Polygon3D> drawableSet = new HashSet<>();
        java.util.List<java.util.List<Point3D>> C = new ArrayList<>();
        for (int j = 0; j <= 360; j += step) {
            java.util.List<Point3D> B = new ArrayList<>();
            for (int i = 0; i <= 360; i += step) {
                B.add(new Point3D(R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), R * utils.Math.roundNearZero(Math.cos(j * Math.PI / 180))));
            }
            C.add(B);
        }
        for (int j = 0; j <= 180 / step - 1; j++) {
            List<Point3D> A = C.get(j), B = C.get(j + 1);
            for (int i = 0; i <= 360 / step; i += 1) {
                try {
                    drawableSet.add(new Polygon3D(A.get(i), A.get((i + 1) % (360 / step)), B.get((i + 1) % (360 / step)), c));
                } catch (utils.throwables.ImpossiblePolygonException ignored) {
                }
                try {
                    drawableSet.add(new Polygon3D(A.get(i), B.get(i), B.get((i + 1) % (360 / step)), c));
                } catch (utils.throwables.ImpossiblePolygonException ignored) {}
            }
        }
        polygons.clear();
        polygons.addAll(drawableSet);
    }
}
