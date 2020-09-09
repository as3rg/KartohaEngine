package utils;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Region implements Object3D {
    private final Point low, high;

    public Region(Point a, Point b) {
        low = new Point(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z));
        high = new Point(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z));
    }

    @Override
    public Point getLowPoint() {
        return low;
    }

    @Override
    public Point getHighPoint() {
        return high;
    }

    public boolean contains(Object3D o){
        Point oLow = o.getLowPoint(), oHigh = o.getHighPoint();
        return low.x <= oLow.x && low.y <= oLow.y && low.z <= oLow.z && high.x >= oHigh.x && high.y >= oHigh.y && high.z >= oHigh.z;
    }

    public boolean partiallyContains(Object3D o){
        Point oLow = o.getLowPoint(), oHigh = o.getHighPoint();
        return low.x <= oHigh.x && low.y <= oHigh.y && low.z <= oHigh.z && high.x >= oLow.x && high.y >= oLow.y && high.z >= oLow.z;
    }
}
