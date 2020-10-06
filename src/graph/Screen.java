package graph;

import geometry.objects3D.Plane3D;
import geometry.objects3D.Point3D;
import geometry.objects3D.Vector3D;

public class Screen extends Plane3D {

    public final Point3D focus;

    public Screen(Vector3D vector, Point3D point) {
        super(vector, vector.addToPoint(point));
        focus = point;
    }
}
