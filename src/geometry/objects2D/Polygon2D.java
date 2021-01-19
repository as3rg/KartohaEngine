package geometry.objects2D;

import java.awt.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Polygon2D implements Object2D {
    public final Point2D a1, a2, a3;
    public final Color color;

    public Polygon2D(Point2D a1, Point2D a2, Point2D a3, Color color) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.color = color;
    }

    @Override
    public Region2D getRegion() {
        return new Region2D(
                new Point2D(min(min(a1.x, a2.x), a3.x), min(min(a1.y, a2.y), a3.y)),
                new Point2D(max(max(a1.x, a2.x), a3.x), max(max(a1.y, a2.y), a3.y)));
    }
}
