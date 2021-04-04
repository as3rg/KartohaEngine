package geometry.objects3D;

import com.sun.istack.internal.Nullable;
import java.util.Optional;
import utils.Pair;
import utils.throwables.ImpossiblePlaneException;
import utils.throwables.ImpossiblePolygonException;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Polygon3D implements Object3D {
    public final Point3D a1, a2, a3;
    public final Color color;

    /**
     * Конструктор полигона по 3 точкам и цвету
     * @param a1
     * @param a2
     * @param a3
     * @param color
     */
    public Polygon3D(Point3D a1, Point3D a2, Point3D a3, Color color) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.color = color;
        try {
            getPlane();
        } catch (ImpossiblePlaneException e) {
            throw new ImpossiblePolygonException();
        }
        if (a1.equals(a2) || a1.equals(a3) || a2.equals(a3))
            throw new ImpossiblePolygonException();
    }

    /**
     * @param p Точка
     * @return Полигон в общей системе координат, полученный из текущего полигона в системе координат с нулем в данной точке
     */
    public Polygon3D from(Point3D p) {
        return new Polygon3D(a1.from(p), a2.from(p), a3.from(p), color);
    }

    /**
     * @return Плоскость полигона
     */
    public Plane3D getPlane() {
        return new Plane3D(a1, a2, a3);
    }

    /**
     * @param v Вектор оси поворота
     * @param p Точка, лежащая на оси
     * @return Полигон, повернутый вдоль оси поворота на угол, равный модулю вектора оси поворота
     */
    public Polygon3D rotate(Vector3D v, Point3D p) {
        return new Polygon3D(a1.rotate(v, p), a2.rotate(v, p), a3.rotate(v, p), color);
    }

    @Override
    public Region3D getRegion() {
        return new Region3D(
                new Point3D(min(min(a1.x, a2.x), a3.x), min(min(a1.y, a2.y), a3.y), min(min(a1.z, a2.z), a3.z)),
                new Point3D(max(max(a1.x, a2.x), a3.x), max(max(a1.y, a2.y), a3.y), max(max(a1.z, a2.z), a3.z)));
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", a1, a2, a3);
    }

    /**
     * @param color Цвето полученных полигонов
     * @param a Первая точка
     * @param b Вторая точка
     * @param c Третья точка
     * @param d Четвертая точка
     * @return Возможная пара непересекающихся полигонов, образованных данными двумя точками
     */
    public static Optional<Pair<Polygon3D, Polygon3D>> getPolygons(Color color, Point3D a, Point3D b, Point3D c, Point3D d) {
        Set<Point3D> point3DS = new HashSet<>();
        point3DS.add(a);
        point3DS.add(b);
        point3DS.add(c);
        point3DS.add(d);
        for (Point3D p1 : point3DS) {
            for (Point3D p2 : point3DS) {
                if (p1 == p2)
                    continue;
                ArrayList<Point3D> v = new ArrayList<>(point3DS);
                v.remove(p1);
                v.remove(p2);
                Point3D p3 = v.get(0), p4 = v.get(1);

                Vector3D p13 = new Vector3D(p1, p3),
                        p14 = new Vector3D(p1, p4),
                        p12 = new Vector3D(p1, p2);
                if (p12.vectorProduct(p14).scalarProduct(p12.vectorProduct(p13)) < 0) {
                    return Optional.of(new Pair<>(new Polygon3D(p1, p2, p3, color), new Polygon3D(p1, p2, p4, color)));
                }
            }
        }
        return Optional.empty();
    }
}
