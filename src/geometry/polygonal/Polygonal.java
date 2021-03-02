package geometry.polygonal;

import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;

import java.util.Collection;

public interface Polygonal {
    Collection<Polygon3D> getPolygons();
    void rotate(Vector3D v, Point3D p);
}
