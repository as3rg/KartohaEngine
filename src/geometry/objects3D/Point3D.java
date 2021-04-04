package geometry.objects3D;

import java.util.Objects;

public class Point3D {

    /**
     * Нуль-вектор
     */
    public static final Point3D ZERO = new Point3D(0, 0, 0);
    public final double x, y, z;

    /**
     * Конструктор точки по 3 координатам
     * @param x x-координата
     * @param y y-координата
     * @param z z-координата
     */
    public Point3D(double x, double y, double z) {
        this.x = utils.Math.roundNearZero(x);
        this.y = utils.Math.roundNearZero(y);
        this.z = utils.Math.roundNearZero(z);
    }

    /**
     * @param p Точка
     * @return Точка в общей системе координат, полученная из текущей точки в системе координат с нулем в данной точке
     */
    public Point3D from(Point3D p) {
        return new Point3D(x + p.x, y + p.y, z + p.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3D point3D = (Point3D) o;
        return utils.Math.roundNearZero(point3D.x - x) == 0 &&
                utils.Math.roundNearZero(point3D.y - y) == 0 &&
                utils.Math.roundNearZero(point3D.z - z) == 0;
    }

    /**
     * @param v Вектор оси поворота
     * @param p Точка, лежащая на оси
     * @return Точка, повернутая вдоль оси поворота на угол, равный модулю вектора оси поворота
     */
    public Point3D rotate(Vector3D v, Point3D p) {
        Vector3D pv = new Vector3D(p, this);
        pv = pv.rotate(v);
        return pv.addToPoint(p);
    }

    /**
     * @param l Прямая
     * @return Расстояние от текущей точки до прямой
     */
    public double distance(Line3D l) {
        return l.distance(this);
    }

    /**
     * @param p Плоскость
     * @return Расстояние от точки до данной плоскости
     */
    public double distance(Plane3D p) {
        return p.distance(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
