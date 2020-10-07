package geometry.objects3D;

import geometry.objects2D.Point2D;
import geometry.objects2D.Polygon2D;
import geometry.objects2D.Vector2D;
import graph.Camera;
import graph.CanvasPanel;
import graph.Drawable;
import graph.Pixel;
import utils.throwables.ImpossiblePlaneException;
import utils.throwables.ImpossiblePolygonException;

import java.awt.*;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Polygon3D implements Drawable, Object3D {
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
        if(a1.equals(a2) || a1.equals(a3) || a2.equals(a3))
            throw new ImpossiblePolygonException();
    }

    public Plane3D getPlane(){
        return new Plane3D(a1,a2,a3);
    }

    @Override
    public void draw(graph.Canvas cp, Camera camera) {
        Optional<Point2D> a = camera.project(a1),
                b = camera.project(a2),
                c = camera.project(a3);
        if (!a.isPresent() || !b.isPresent() || !c.isPresent())
            return;

//        new Polygon2D(a.get(), b.get(), c.get(), color).draw(cp, camera);

        Vector3D v23 = new Vector3D(a2, a3);
        for(double j = 1; j <= v23.getLength(); j+=0.5){
            Point3D p = v23.multiply(j/v23.getLength()).addToPoint(a2);
            Vector3D v1p = new Vector3D(a1, p);
            for(double i = 1; i <= v1p.getLength(); i+=0.5){
                Point3D p2 = v1p.multiply(i/v1p.getLength()).addToPoint(a1);
                Optional<Point2D> p22 = camera.project(p2);
                p22.ifPresent(point2D -> cp.set((int) point2D.x, (int) point2D.y, new Pixel(new Vector3D(camera.getScreen().focus, p2).getLength(), color)));
            }
        }
    }

    @Override
    public Region3D getRegion() {
        return new Region3D(
                new Point3D(min(min(a1.x, a2.x), a3.x), min(min(a1.y, a2.y), a3.y), min(min(a1.z, a2.z), a3.z)),
                new Point3D(max(max(a1.x, a2.x), a3.x), max(max(a1.y, a2.y), a3.y), max(max(a1.z, a2.z), a3.z)));
    }
}
