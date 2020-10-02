package utils.Objects3D;

import com.sun.istack.internal.Nullable;

public class Line3D {

    public final Point3D point;
    public final Vector3D vector;

    public Line3D(Point3D p, Vector3D vector) {
        this.point = p;
        this.vector = vector;
    }

    public Line3D(Point3D p1, Point3D p2){
        this.point = p1;
        this.vector = new Vector3D(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
    }

    @Nullable
    public Point3D getIntersection(Plane3D p) {
        return p.getIntersection(this);
    }
}
