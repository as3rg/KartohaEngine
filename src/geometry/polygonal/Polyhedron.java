package geometry.polygonal;


import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Polyhedron implements Polygonal {

    /**
     * Конструктор многогранника по нулевой точке и коллекции полигонов
     * @param zero
     * @param polygons
     */
    public Polyhedron(Point3D zero, Collection<Polygon3D> polygons) {
        this.zero = zero;
        this.polygons = polygons;
    }

    /**
     * Конструктор многогранника по нулевой точке
     * @param zero
     */
    public Polyhedron(Point3D zero) {
        this.zero = zero;
        this.polygons = new HashSet<>();
    }

    @Override
    public Collection<Polygon3D> getPolygons() {
        Set<Polygon3D> polygon3DS = new HashSet<>();
        for (Polygon3D p : polygons) {
            polygon3DS.add(p.from(zero));
        }
        return polygon3DS;
    }

    @Override
    public void rotate(Vector3D v, Point3D p) {
        Set<Polygon3D> polygon3DS = new HashSet<>(getPolygons());
        Set<Polygon3D> polygon3DS2 = new HashSet<>();
        for (Polygon3D pol : polygon3DS) {
            polygon3DS2.add(pol.rotate(v, p));
        }
        setZero(zero.rotate(v, p));
        polygons.clear();
        Point3D antizero = new Vector3D(Point3D.ZERO, zero).multiply(-1).addToPoint(Point3D.ZERO);
        for (Polygon3D pol : polygon3DS2) {
            polygons.add(pol.from(antizero));
        }
    }

    /**
     * Точка, относительно которой расположены полигоны в многограннике
     */
    protected Point3D zero;

    /**
     * @return Коллекция полигонов
     */
    public Collection<Polygon3D> getOriginalPolygons() {
        return polygons;
    }

    protected final Collection<Polygon3D> polygons;

    public Point3D getZero() {
        return zero;
    }

    public void setZero(Point3D zero) {
        this.zero = zero;
    }

}
