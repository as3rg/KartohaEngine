package geometry.polygonal;


import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Polyhedron implements Polygonal {

    public Polyhedron(Point3D zero, Collection<Polygon3D> polygons) {
        this.zero = zero;
        this.polygons = polygons;
    }

    public Polyhedron(Point3D zero) {
        this.zero = zero;
        this.polygons = new HashSet<>();
    }

    @Override
    public Collection<Polygon3D> getPolygons() {
        Set<Polygon3D> polygon3DS = new HashSet<>();
        for(Polygon3D p : polygons){
            polygon3DS.add(p.from(zero));
        }
        return polygon3DS;
    }

    protected Point3D zero;

    public Collection<Polygon3D> getOriginalPolygons(){
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
