package geometry.polygonal;

import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;

import java.util.Collection;

public interface Polygonal {

    /**
     * @return Список полигонов
     */
    Collection<Polygon3D> getPolygons();

    /**
     * Поворачивает текущий объект вдоль оси на угол, равный модулю вектора оси
     * @param v Вектор оси
     * @param p Точка, лежащая на оси
     */
    void rotate(Vector3D v, Point3D p);
}
