package utils;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Polygon3D implements Object3D {
    public final Point3D A, B, C;

    public Polygon3D(Point3D a, Point3D b, Point3D c) {
        A = a;
        B = b;
        C = c;
    }

    @Override
    public Point3D getLowPoint() {
        return new Point3D(min(min(A.x, B.x), C.x), min(min(A.y, B.y), C.y), min(min(A.z, B.z), C.z));
    }

    @Override
    public Point3D getHighPoint() {
        return new Point3D(max(max(A.x, B.x), C.x), max(max(A.y, B.y), C.y), max(max(A.z, B.z), C.z));
    }
}
