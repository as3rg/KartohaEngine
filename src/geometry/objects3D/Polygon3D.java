package geometry.objects3D;

import geometry.objects2D.Point2D;
import geometry.objects2D.Polygon2D;
import geometry.objects2D.Region2D;
import geometry.objects2D.Vector2D;
import graph.Camera;
import graph.Drawable;
import graph.Pixel;
import utils.throwables.ImpossiblePlaneException;
import utils.throwables.ImpossiblePolygonException;

import java.awt.*;
import java.util.Optional;

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
        Optional<Point2D> a12D = camera.project(a1),
                a22D = camera.project(a2),
                a32D = camera.project(a3);
        if (!a12D.isPresent() || !a22D.isPresent() || !a32D.isPresent() || !new Polygon2D(a12D.get(), a22D.get(), a32D.get(), color).getRegion().crosses(new Region2D(new Point2D(0,0), new Point2D(camera.getResolution().width, camera.getResolution().height))))
            return;

//        cp.set((int) a12D.get().x, (int) a12D.get().y, new Pixel(1, color));
//        cp.set((int) a22D.get().x, (int) a22D.get().y, new Pixel(1, color));
//        cp.set((int) a32D.get().x, (int) a32D.get().y, new Pixel(1, color));
//        new Polygon2D(a12D.get(), a22D.get(), a32D.get(), color).draw(cp, camera);
        Vector2D v232D = new Vector2D(a22D.get(),a32D.get());
        Vector3D v23 = new Vector3D(a2, a3);
        for(double j = 0; j <= v232D.getLength(); j+=0.5){
            Point3D p = v23.multiply(j/v232D.getLength()).addToPoint(a2);
            Vector3D v1p = new Vector3D(a1, p);
            Point2D p2D = v232D.multiply(j/v232D.getLength()).addToPoint(a22D.get());
            Vector2D v1p2D = new Vector2D(a12D.get(), p2D);
            for(double i = 0; i <= v1p2D.getLength(); i+=0.5){
                Point3D p2 = v1p.multiply(i/v1p2D.getLength()).addToPoint(a1);
                Point2D p22D = v1p2D.multiply(i/v1p2D.getLength()).addToPoint(a12D.get());
                cp.set((int) p22D.x, (int) p22D.y, new Pixel(new Vector3D(camera.getScreen().focus, p2).getLength(), color));
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
