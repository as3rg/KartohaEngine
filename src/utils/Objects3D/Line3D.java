package utils.Objects3D;

import graph.Camera;
import utils.Drawable;
import utils.Objects2D.Point2D;
import utils.Throwables.ImpossibleLineException;

import java.awt.*;
import java.util.Optional;

public class Line3D implements Drawable {

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
        Vector3D numerator = new Vector3D(point, l.point),
                denominator = vector.subtract(l.vector);

        Double t1 = denominator.x != 0 ? numerator.x / denominator.x : null,
                t2 = denominator.y != 0 ? numerator.y / denominator.y : null,
                t3 = denominator.z != 0 ? numerator.z / denominator.z : null;
        boolean possible = t1 == null || t2 == null || utils.Math.destroyMinusZeros(t2 - t1) == 0;
        possible = possible && (t1 == null || t3 == null || utils.Math.destroyMinusZeros(t3-t1) == 0);
        possible = possible && (t3 == null || t2 == null || utils.Math.destroyMinusZeros(t2-t3) == 0);
        if (possible && (t1 != null || t2 != null || t3 != null)){
            return Optional.of(vector.multiply(t1 != null ? t1 : (t2 != null ? t2 : t3)).addToPoint(point));
        }
        return Optional.empty();
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        Optional<Point2D> a2 = camera.project(vector.multiply(-10).addToPoint(point)), b2 = camera.project(vector.multiply(10).addToPoint(point));
        if(a2.isPresent() && b2.isPresent()) {
            Point2D a = a2.get(), b = b2.get();
            g.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
        }
    }
}
