package geometry.objects3D;

import utils.throwables.ImpossiblePlaneException;

import java.util.Optional;

public class Plane3D {
    public final Vector3D vector;
    public final Point3D point;

    public Plane3D(Vector3D vector, Point3D point) {
        this.vector = vector;
        this.point = point;
        if(vector.getLength() == 0)
            throw new ImpossiblePlaneException();
    }

    public Plane3D(Point3D a, Point3D b, Point3D c) {
        double A = (b.y-a.y)*(c.z - a.z)-(c.y-a.y)*(b.z-a.z),
                B = (c.x-a.x)*(b.z-a.z)-(c.z-a.z)*(b.x-a.x),
                C = (b.x-a.x)*(c.y-a.y)-(c.x-a.x)*(b.y-a.y);
        this.vector = new Vector3D(A,B,C);
        this.point = a;
        if (vector.getLength() == 0)
            throw new ImpossiblePlaneException();
    }

    public double getD(){
        return utils.Math.roundNearZero(-(vector.x * point.x + vector.y * point.y + vector.z * point.z));
    }

    public Optional<Point3D> getIntersection(Line3D l){
        if (vector.scalarProduct(l.vector) == 0) return Optional.empty();
        double t = -(getD() + vector.x*l.point.x + vector.y*l.point.y+vector.z*l.point.z)/vector.scalarProduct(l.vector);
        return Optional.of(l.vector.multiply(t).addToPoint(l.point));
    }

    public Optional<Line3D> getIntersection(Plane3D p){
        double kY = 0, y0 = 0, kZ = 0, z0 = 0, kX = 0, x0 = 0;
        if(vector.x != 0 && p.vector.x != 0){
            if(vector.x * p.vector.y != vector.y * p.vector.x){
                kY = (vector.x*p.vector.z-p.vector.x*vector.z)/(p.vector.x*vector.y-vector.x*p.vector.y);
                y0 = (vector.x*p.getD()-p.vector.x*getD())/(p.vector.x*vector.y-vector.x*p.vector.y);
                kZ = 1;
                z0 = 0;
                kX = -vector.y*kY/vector.x - vector.z/vector.x;
                x0 = -vector.y*y0/vector.x - getD()/vector.x;
            }else if(vector.x * p.vector.z != vector.z * p.vector.x){
                kY = 1;
                y0 = 0;
                kZ = 0;
                z0 = (p.vector.x*getD()-vector.x*p.getD())/(vector.x*p.vector.z-p.vector.x*vector.z);
                kX = -vector.y/vector.x;
                x0 = -vector.z*z0/vector.x - vector.y/vector.x;
            }
        }else if(vector.y != 0 && p.vector.y != 0){
            if(vector.y * p.vector.x != vector.x * p.vector.y){
                kX = (vector.y*p.vector.z-p.vector.y*vector.z)/(p.vector.y*vector.x-vector.y*p.vector.x);
                x0 = (vector.y*p.getD()-p.vector.y*getD())/(p.vector.y*vector.x-vector.y*p.vector.x);
                kZ = 1;
                z0 = 0;
                kY = -vector.x*kX/vector.y - vector.z/vector.y;
                y0 = -vector.x*x0/vector.y - getD()/vector.y;
            }else if(vector.y * p.vector.x != vector.z * p.vector.y){
                kX = 1;
                x0 = 0;
                kZ = 0;
                z0 = (p.vector.y*getD()-vector.y*p.getD())/(vector.y*p.vector.z-p.vector.y*vector.z);
                kY = -vector.x/vector.y;
                y0 = -vector.z*z0/vector.y - vector.x/vector.y;
            }
        }else if(vector.z != 0 && p.vector.z != 0){
            if(vector.z * p.vector.y != vector.y * p.vector.z){
                kY = (vector.z*p.vector.x-p.vector.z*vector.x)/(p.vector.z*vector.y-vector.z*p.vector.y);
                y0 = (vector.z*p.getD()-p.vector.z*getD())/(p.vector.z*vector.y-vector.z*p.vector.y);
                kX = 1;
                x0 = 0;
                kZ = -vector.y*kY/vector.z - vector.x/vector.z;
                z0 = -vector.y*y0/vector.z - getD()/vector.z;
            }else if(vector.z * p.vector.x != vector.x * p.vector.z){
                kY = 1;
                y0 = 0;
                kX = 0;
                x0 = (p.vector.z*getD()-vector.z*p.getD())/(vector.z*p.vector.x-p.vector.z*vector.x);
                kZ = -vector.y/vector.z;
                z0 = -vector.x*x0/vector.z - vector.y/vector.z;
            }
        }else if(vector.x != 0 && p.vector.z != 0){ //p.vector.y?
            z0 = -p.getD()/p.vector.z;
            kX = 0;
            kY = 1;
            x0 = -getD()/vector.x;
            y0 = 0;
            kZ = -p.vector.y/p.vector.z;
        }else if(vector.y != 0 && p.vector.x != 0){  //p.vector.z?
            z0 = 0;
            kX = -p.vector.z/p.vector.x;
            kY = 0;
            x0 = -p.getD()/p.vector.x;
            y0 = -getD()/vector.y;
            kZ = 1;
        }else if(vector.z != 0 && p.vector.y != 0){  //p.vector.x?
            z0 = -getD()/vector.z;
            kX = 1;
            kY = -p.vector.x/p.vector.y;
            x0 = 0;
            y0 = -p.getD()/p.vector.y;
            kZ = 0;
        }else if(p.vector.x != 0 && vector.z != 0){  //vector.y?
            z0 = -getD()/vector.z;
            kX = 0;
            kY = 1;
            x0 = -p.getD()/p.vector.x;
            y0 = 0;
            kZ = -vector.y/vector.z;
        }else if(vector.x != 0 && p.vector.y != 0){  //vector.z?
            z0 = 0;
            kX = -vector.z/vector.x;
            kY = 0;
            x0 = -getD()/vector.x;
            y0 = -p.getD()/p.vector.y;
            kZ = 1;
        }else if(vector.y != 0 && p.vector.z != 0){  //vector.x?
            z0 = -p.getD()/p.vector.z;
            kX = 1;
            kY = -vector.x/vector.y;
            x0 = 0;
            y0 = -getD()/vector.y;
            kZ = 0;
        }
        if(utils.Math.roundNearZero(z0) == 0
                && utils.Math.roundNearZero(y0) == 0
                && utils.Math.roundNearZero(x0) == 0
                && utils.Math.roundNearZero(kX) == 0
                && utils.Math.roundNearZero(kY) == 0
                && utils.Math.roundNearZero(kZ) == 0){
            return Optional.empty();
        }
        return Optional.of(new Line3D(new Point3D(x0, y0, z0), new Vector3D(kX, kY, kZ)));
    }
}
