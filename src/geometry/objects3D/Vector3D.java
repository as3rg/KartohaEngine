package geometry.objects3D;

import utils.throwables.ImpossibleVectorException;

public class Vector3D {
    public final double x,y,z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Point3D p1, Point3D p2) {
        this.x = p2.x-p1.x;
        this.y = p2.y-p1.y;
        this.z = p2.z-p1.z;
        if(getLength() == 0){
            throw new ImpossibleVectorException();
        }
    }

    public Point3D addToPoint(Point3D a){
        return new Point3D(a.x+x, a.y+y, a.z+z);
    }

    public Vector3D multiply(double d){
        return new Vector3D(x*d, y*d, z*d);
    }

    public Vector3D add(Vector3D v){
        return new Vector3D(x+v.x, y+v.y,z+v.z);
    }

    public Vector3D subtract(Vector3D v){
        return new Vector3D(x-v.x, y-v.y,z-v.z);
    }

    public double getLength(){
        return Math.sqrt(x*x+y*y+z*z);
    }

    public double scalarProduct(Vector3D v){
        return x*v.x+y*v.y+z*v.z;
    }
}
