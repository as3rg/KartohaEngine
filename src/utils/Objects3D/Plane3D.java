package utils.Objects3D;

import com.sun.istack.internal.Nullable;

public class Plane3D {
    public final Vector3D vector;
    public final Point3D point;

    public Plane3D(Vector3D vector, Point3D point) {
        this.vector = vector;
        this.point = point;
    }

    public Plane3D(Point3D a, Point3D b, Point3D c) {
        double A = (b.y-a.y)*(c.z - a.z)-(c.y-a.y)*(b.z-a.z),
                B = (c.x-a.x)*(b.z-a.z)-(c.z-a.z)*(b.x-a.x),
                C = (b.x-a.x)*(c.y-a.y)-(c.x-a.x)*(b.y-a.y);
        this.vector = new Vector3D(A,B,C);
        this.point = a;
    }

    public double getD(){
        return -(vector.x * point.x + vector.y * point.y + vector.z * point.z);
    }
    
    @Nullable
    public Point3D getIntersection(Line3D l){
        if (vector.x*l.vector.x + vector.y*l.vector.y + vector.z*l.vector.z == 0) return null;
        double t = (vector.x*vector.x + vector.y*vector.y + vector.z*vector.z)/(vector.x*l.vector.x + vector.y*l.vector.y + vector.z*l.vector.z);
        return new Point3D(l.vector.x*t+l.point.x, l.vector.y*t+l.point.y, l.vector.z*t+l.point.z);
    }

//    @Nullable
//    public Line getIntersectionLine(Plane p){
//        if(vector.x != 0 && p.vector.x != 0){
//            if(vector.x * p.vector.y != vector.y * p.vector.x){
//                double kY = (vector.x*p.vector.z-p.vector.x*vector.z)/(p.vector.x*vector.y-vector.x*p.vector.y),
//                        y0 = (vector.x*p.getD()-p.vector.x*getD())/(p.vector.x*vector.y-vector.x*p.vector.y),
//                        kZ = 1,
//                        z0 = 0,
//                        kX = -vector.y*kY/vector.x - vector.z/vector.x,
//                        x0 = -vector.y*y0/vector.x - getD()/vector.x;
//                return new Line(new Point3D(x0, y0, z0), new Vector3D(kX, kY, kZ));
//            }else if(vector.x * p.vector.z != vector.z * p.vector.x){
//                double kY = 1,
//                        y0 = 0,
//                        kZ = 0,
//                        z0 = (p.vector.x*getD()-vector.x*p.getD())/(vector.x*p.vector.z-p.vector.x*vector.z),
//                        kX = -vector.y/vector.x,
//                        x0 = -vector.z*z0/vector.x - vector.y/vector.x;
//                return new Line(new Point3D(x0, y0, z0), new Vector3D(kX, kY, kZ));
//            }else return null;
//        }
//    }
}
