package utils;

public class Vector3D {
    public final double x,y,z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D addToPoint(Point3D a){
        return new Point3D(a.x+x, a.y+y, a.z+z);
    }
}
