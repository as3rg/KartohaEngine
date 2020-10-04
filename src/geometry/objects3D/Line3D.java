package geometry.objects3D;

import utils.throwables.ImpossibleLineException;

import java.util.Optional;

public class Line3D{

    public final Point3D point;
    public final Vector3D vector;

    public Line3D(Point3D p, Vector3D vector) {
        this.point = p;
        this.vector = vector;
        if(vector.getLength() == 0)
            throw new ImpossibleLineException();
    }

    public Line3D(Point3D p1, Point3D p2){
        this.point = p1;
        this.vector = new Vector3D(p1, p2);
        if(vector.getLength() == 0)
            throw new ImpossibleLineException();
    }

    public Optional<Point3D> getIntersection(Plane3D p) {
        return p.getIntersection(this);
    }

    public Optional<Point3D> getIntersection(Line3D l) {

        double t2;
        if(vector.y*l.vector.x-vector.x*l.vector.y != 0){
            t2 = (vector.x*(l.point.y-point.y)-vector.y*(l.point.x-point.x))/(vector.y*l.vector.x-vector.x*l.vector.y);
        }else if(vector.z*l.vector.x-vector.x*l.vector.z != 0){
            t2 = (vector.x*(l.point.z-point.z)-vector.z*(l.point.x-point.x))/(vector.z*l.vector.x-vector.x*l.vector.z);
        }else if(vector.y*l.vector.z-vector.z*l.vector.y != 0){
            t2 = (vector.z*(l.point.y-point.y)-vector.y*(l.point.z-point.z))/(vector.y*l.vector.z-vector.z*l.vector.y);
        }else{
            return Optional.empty();
        }
        return Optional.of(l.vector.multiply(t2).addToPoint(l.point));
    }
}
