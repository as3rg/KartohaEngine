package geometry.objects3D;

import utils.throwables.ImpossibleVectorException;

import java.util.Objects;

public class Vector3D {
    public final double x,y,z;

    public Vector3D(double x, double y, double z) {
        this.x = utils.Math.roundNearZero(x);
        this.y = utils.Math.roundNearZero(y);
        this.z = utils.Math.roundNearZero(z);
    }

    public Vector3D(Point3D p1, Point3D p2) {
        this.x = utils.Math.roundNearZero(p2.x-p1.x);
        this.y = utils.Math.roundNearZero(p2.y-p1.y);
        this.z = utils.Math.roundNearZero(p2.z-p1.z);
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
        return utils.Math.roundNearZero(Math.sqrt(x*x+y*y+z*z));
    }

    public double scalarProduct(Vector3D v){
        return utils.Math.roundNearZero(x*v.x+y*v.y+z*v.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3D vector3D = (Vector3D) o;
        return utils.Math.roundNearZero(vector3D.x-x) == 0 &&
                utils.Math.roundNearZero(vector3D.y-y) == 0 &&
                utils.Math.roundNearZero(vector3D.z-z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

