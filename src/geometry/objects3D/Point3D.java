package geometry.objects3D;

import java.util.Objects;

public class Point3D{
    public final double x, y, z;

    public Point3D(double x, double y, double z){
        this.x = utils.Math.roundNearZero(x);
        this.y = utils.Math.roundNearZero(y);
        this.z = utils.Math.roundNearZero(z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3D point3D = (Point3D) o;
        return utils.Math.roundNearZero(point3D.x-x) == 0 &&
                utils.Math.roundNearZero(point3D.y-y) == 0 &&
                utils.Math.roundNearZero(point3D.z-z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
