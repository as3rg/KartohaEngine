package geometry.objects3D;

import graph.Camera;
import graph.Drawable;
import geometry.objects2D.Point2D;
import geometry.objects2D.Polygon2D;
import utils.throwables.ImpossiblePlaneException;
import utils.throwables.ImpossiblePolygonException;

import java.awt.*;
import java.util.Optional;

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
}
