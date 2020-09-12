package utils;

import java.awt.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Polygon3D implements Object3D {
    public final Point3D a1, a2, a3;
    public final Color color;

    public Polygon3D(Point3D a1, Point3D a2, Point3D a3, Color color) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.color = color;
    }

    @Override
    public Point3D getLowPoint() {
        return new Point3D(min(min(a1.x, a2.x), a3.x), min(min(a1.y, a2.y), a3.y), min(min(a1.z, a2.z), a3.z));
    }

    @Override
    public Point3D getHighPoint() {
        return new Point3D(max(max(a1.x, a2.x), a3.x), max(max(a1.y, a2.y), a3.y), max(max(a1.z, a2.z), a3.z));
    }
}
