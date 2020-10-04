package geometry.objects3D;

import graph.Camera;
import graph.Drawable;
import geometry.objects2D.Point2D;
import geometry.objects2D.Polygon2D;
import utils.throwables.ImpossiblePlaneException;
import utils.throwables.ImpossiblePolygonException;

import javax.sound.sampled.Line;
import java.awt.*;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Polygon3D implements Object3D, Drawable {
    public final Point3D a1, a2, a3;
    public final Color color;

    public Polygon3D(Point3D a1, Point3D a2, Point3D a3, Color color) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.color = color;
        try{
            getPlane();
        }catch (ImpossiblePlaneException e){
            throw new ImpossiblePolygonException();
        }
    }

    public Plane3D getPlane(){
        return new Plane3D(a1,a2,a3);
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        Optional<Point2D> a = camera.project(a1),
                b = camera.project(a2),
                c = camera.project(a3);
        if (!a.isPresent() || !b.isPresent() || !c.isPresent())
            return;

        new Polygon2D(a.get(), b.get(), c.get(), color).draw(g, camera);
    }

    @Override
    public Region3D getRegion() {
        return new Region3D(
                new Point3D(min(min(a1.x, a2.x), a3.x), min(min(a1.y, a2.y), a3.y), min(min(a1.z, a2.z), a3.z)),
                new Point3D(max(max(a1.x, a2.x), a3.x), max(max(a1.y, a2.y), a3.y), max(max(a1.z, a2.z), a3.z)));
    }

    public Collection<Polygon3D> split(Polygon3D polygon3D){
        Line3D l12 = new Line3D(a1, a2),
                l23 = new Line3D(a2, a3),
                l13 = new Line3D(a1, a3);

        Optional<Point3D> b12 = l12.getIntersection(polygon3D.getPlane()),
                b23 = l23.getIntersection(polygon3D.getPlane()),
                b13 = l13.getIntersection(polygon3D.getPlane());

        Set<Polygon3D> result = new HashSet<>();
        if(b12.isPresent() && new Region3D(a1, a2).contains(b12.get()) && b23.isPresent() && new Region3D(a3, a2).contains(b23.get())){
            result.add(new Polygon3D(a1, b12.get(), b23.get(), color));
            result.add(new Polygon3D(a1, a3, b23.get(), color));
            result.add(new Polygon3D(a2, b12.get(), b23.get(), color));
        }else if(b12.isPresent() && b13.isPresent() && new Region3D(a1, a2).contains(b12.get()) && new Region3D(a1, a3).contains(b13.get())){
            result.add(new Polygon3D(a1, b12.get(), b13.get(), color));
            result.add(new Polygon3D(a3, b13.get(), b12.get(), color));
            result.add(new Polygon3D(a2, a3, b12.get(), color));
        }else if(b13.isPresent() && b23.isPresent() && new Region3D(a1, a3).contains(b13.get()) && new Region3D(a3, a2).contains(b23.get())){
            result.add(new Polygon3D(a3, b23.get(), b13.get(), color));
            result.add(new Polygon3D(a2, b23.get(), b13.get(), color));
            result.add(new Polygon3D(a2, a1, b13.get(), color));
        }else{
            result.add(this);
        }
        return result;
    }
}
