package utils;

public class Line {

    public final Point3D point;
    public final Vector3D vector;

    public Line(Point3D p, Vector3D vector) {
        this.point = p;
        this.vector = vector;
    }

    public Line(Point3D p1, Point3D p2){
        this.point = p1;
        this.vector = new Vector3D(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
    }
}
