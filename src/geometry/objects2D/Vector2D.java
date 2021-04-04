package geometry.objects2D;

import geometry.objects3D.Vector3D;
import utils.throwables.ImpossibleVectorException;

import java.util.Objects;

public class Vector2D {
    public final double x, y;

    /**
     * Конструктор вектора по координатам
     * @param x x-координата
     * @param y y-координата
     */
    public Vector2D(double x, double y) {
        this.x = utils.Math.roundNearZero(x);
        this.y = utils.Math.roundNearZero(y);
        if (getLength() == 0) {
            throw new ImpossibleVectorException();
        }
    }

    /**
     * Конструктор вектора от начальной до конечной точки
     * @param p1 начальная точка
     * @param p2 конечная точка
     */
    public Vector2D(Point2D p1, Point2D p2) {
        this.x = utils.Math.roundNearZero(p2.x - p1.x);
        this.y = utils.Math.roundNearZero(p2.y - p1.y);
        if (getLength() == 0) {
            throw new ImpossibleVectorException();
        }
    }

    /**
     * @param a Начальная точка
     * @return Точка, вектор от начальной до которой равен данному вектору
     */
    public Point2D addToPoint(Point2D a) {
        return new Point2D(a.x + x, a.y + y);
    }

    /**
     * @param d Коэффициент
     * @return Вектор, равный данному, умноженному на коэффициент
     */
    public Vector2D multiply(double d) {
        return new Vector2D(x * d, y * d);
    }

    /**
     * @param v Вектор
     * @return Вектор, равный сумме данного и текущего
     */
    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    /**
     * @param v Вектор
     * @return Вектор, равнй разности текущего и данного
     */
    public Vector2D subtract(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    /**
     * @return Длина вектора
     */
    public double getLength() {
        return utils.Math.roundNearZero(Math.sqrt(x * x + y * y));
    }

    /**
     * @param v Вектор
     * @return Скалярное произведение данного и текущего вектора
     */
    public double scalarProduct(Vector2D v) {
        return utils.Math.roundNearZero(x * v.x + y * v.y);
    }

    /**
     * @param v Вектор
     * @return Векторное произведение текущего и данного вектора
     */
    public Vector3D vectorProduct(Vector2D v) {
        double C = x * v.y - v.x * y;

        return new Vector3D(0, 0, C);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2D vector2D = (Vector2D) o;
        return utils.Math.roundNearZero(vector2D.x - x) == 0 &&
                utils.Math.roundNearZero(vector2D.y - y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * @return Единичный вектор того же направления
     */
    public Vector2D normalize() {
        double l = getLength();
        return new Vector2D(x / l, y / l);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}

