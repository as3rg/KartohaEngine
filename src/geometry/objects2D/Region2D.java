package geometry.objects2D;

import geometry.objects3D.Point3D;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Region2D implements Object2D {

    /**
     * Нижняя левая точка области
     */
    public final Point2D low;

    /**
     * Вернхяя правая точка области
     */
    public final Point2D high;

    public Region2D(Point2D a, Point2D b) {
        low = new Point2D(min(a.x, b.x), min(a.y, b.y));
        high = new Point2D(max(a.x, b.x), max(a.y, b.y));
    }

    @Override
    public Region2D getRegion() {
        return this;
    }

    /**
     * @param o Объект
     * @return Содержит ли данная область объект
     */
    public boolean contains(Object2D o) {
        Point2D oLow = o.getRegion().low, oHigh = o.getRegion().high;
        return contains(oLow) && contains(oHigh);
    }

    /**
     * @param o Точка
     * @return Содержит ли данная область точку
     */
    public boolean contains(Point2D o) {
        return low.x <= o.x && low.y <= o.y && high.x >= o.x && high.y >= o.y;
    }

    /**
     * @param o Объект
     * @return Содержит ли область часть объекта
     */
    public boolean crosses(Object2D o) {
        Point2D oLow = o.getRegion().low, oHigh = o.getRegion().high;
        return low.x <= oHigh.x && low.y <= oHigh.y && high.x >= oLow.x && high.y >= oLow.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region2D region3D = (Region2D) o;
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
