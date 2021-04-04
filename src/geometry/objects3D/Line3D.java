package geometry.objects3D;

import java.util.Optional;

public class Line3D {

    /**
     * Точка, лежащая на прямой и задающая ее
     */
    public final Point3D point;

    /**
     * Вектор, задающий направление прямой
     */
    public final Vector3D vector;

    /**
     * Конструктор прямой по точке и вектору
     * @param p Точка
     * @param vector Вектор
     */
    public Line3D(Point3D p, Vector3D vector) {
        this.point = p;
        this.vector = vector.normalize();
    }

    /**
     * Конструктор прямой по 2 точкам
     * @param p1 Первая точка
     * @param p2 Вторая точка
     */
    public Line3D(Point3D p1, Point3D p2) {
        this.point = p1;
        this.vector = new Vector3D(p1, p2).normalize();
    }

    /**
     * @param p Точка
     * @return Расстояние от прямой до точки
     */
    public double distance(Point3D p) {
        return Math.abs(vector.vectorProduct(new Vector3D(p, point)).getLength()) / vector.getLength();
    }

    /**
     * @param p Плоскость
     * @return Возможная точка пересечения данной прямой и плоскости
     */
    public Optional<Point3D> getIntersection(Plane3D p) {
        return p.getIntersection(this);
    }

    /**
     * @param l Прямая
     * @return Возможная точка пересечения данной прямой и другой прямой
     */
    public Optional<Point3D> getIntersection(Line3D l) {

        double t2;
        if (vector.y * l.vector.x - vector.x * l.vector.y != 0) {
            t2 = (vector.x * (l.point.y - point.y) - vector.y * (l.point.x - point.x)) / (vector.y * l.vector.x - vector.x * l.vector.y);
        } else if (vector.z * l.vector.x - vector.x * l.vector.z != 0) {
            t2 = (vector.x * (l.point.z - point.z) - vector.z * (l.point.x - point.x)) / (vector.z * l.vector.x - vector.x * l.vector.z);
        } else if (vector.y * l.vector.z - vector.z * l.vector.y != 0) {
            t2 = (vector.z * (l.point.y - point.y) - vector.y * (l.point.z - point.z)) / (vector.y * l.vector.z - vector.z * l.vector.y);
        } else {
            return Optional.empty();
        }
        return Optional.of(l.vector.multiply(t2).addToPoint(l.point));
    }

    @Override
    public String toString() {
        return "Line3D{" +
                "point=" + point +
                ", vector=" + vector +
                '}';
    }
}
