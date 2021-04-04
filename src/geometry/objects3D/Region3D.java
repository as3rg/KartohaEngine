package geometry.objects3D;

import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Region3D implements Object3D {
    public final Point3D low, high;

    public Region3D(Point3D a, Point3D b) {
        low = new Point3D(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z));
        high = new Point3D(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z));
    }

    @Override
    public Region3D getRegion() {
        return this;
    }

    public boolean contains(Object3D o) {
        Point3D oLow = o.getRegion().low, oHigh = o.getRegion().high;
        return contains(oLow) && contains(oHigh);
    }

    public boolean contains(Point3D o) {
        return low.x <= o.x && low.y <= o.y && low.z <= o.z && high.x >= o.x && high.y >= o.y && high.z >= o.z;
    }

    public boolean crosses(Object3D o) {
        Point3D oLow = o.getRegion().low, oHigh = o.getRegion().high;
        return low.x <= oHigh.x && low.y <= oHigh.y && low.z <= oHigh.z && high.x >= oLow.x && high.y >= oLow.y && high.z >= oLow.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region3D region3D = (Region3D) o;
        return Objects.equals(low, region3D.low) &&
                Objects.equals(high, region3D.high);
    }

    @Override
    public int hashCode() {
        return Objects.hash(low, high);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", low.toString(), high.toString());
    }
}
