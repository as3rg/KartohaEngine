package geometry.objects3D;

import geometry.objects2D.Point2D;
import geometry.objects2D.Polygon2D;
import graph.Camera;
import graph.Drawable;
import utils.throwables.ImpossiblePlaneException;
import utils.throwables.ImpossiblePolygonException;

import java.awt.*;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Polygon3D implements Drawable {
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
    public void draw(Graphics g, Camera camera) {
        Optional<Point2D> a = camera.project(a1),
                b = camera.project(a2),
                c = camera.project(a3);
        if (!a.isPresent() || !b.isPresent() || !c.isPresent())
            return;

        new Polygon2D(a.get(), b.get(), c.get(), color).draw(g, camera);
    }

    @Override
    public int compareZ(Camera camera, Drawable drawable) {
        if (drawable instanceof Polygon3D) {
            Polygon3D polygon3D = (Polygon3D) drawable;

            Plane3D plane3D = polygon3D.getPlane();
            Line3D l1p = new Line3D(a1, camera.getScreen().focus),
                    l2p = new Line3D(a2, camera.getScreen().focus),
                    l3p = new Line3D(a3, camera.getScreen().focus);

            Optional<Point3D> a12 = plane3D.getIntersection(l1p),
                    a22 = plane3D.getIntersection(l2p),
                    a32 = plane3D.getIntersection(l3p);
            if((!a12.isPresent() || a12.get().equals(a1)) && (!a22.isPresent() || a22.get().equals(a2)) && (!a32.isPresent() || a32.get().equals(a3))){
                return 0;
            }

            boolean isInFront = true;
            if(a12.isPresent() && !a1.equals(a12.get())){
                isInFront = !new Region3D(a1, camera.getScreen().focus).contains(a12.get());
            }

            if(a22.isPresent() && !a2.equals(a22.get())){
                isInFront = isInFront && !new Region3D(a2, camera.getScreen().focus).contains(a22.get());
            }

            if(a32.isPresent() && !a3.equals(a32.get())){
                isInFront = isInFront && !new Region3D(a3, camera.getScreen().focus).contains(a32.get());
            }

            return isInFront ? -1 : 1;
        }
        return Double.compare(Math.min(new Vector3D(camera.getScreen().focus, drawable.getRegion().low).getLength(),
                new Vector3D(camera.getScreen().focus, drawable.getRegion().high).getLength()),
                Math.min(new Vector3D(camera.getScreen().focus, getRegion().low).getLength(),
                        new Vector3D(camera.getScreen().focus, getRegion().high).getLength()));
    }

    @Override
    public Region3D getRegion() {
        return new Region3D(
                new Point3D(min(min(a1.x, a2.x), a3.x), min(min(a1.y, a2.y), a3.y), min(min(a1.z, a2.z), a3.z)),
                new Point3D(max(max(a1.x, a2.x), a3.x), max(max(a1.y, a2.y), a3.y), max(max(a1.z, a2.z), a3.z)));
    }

    @Override
    public Collection<Drawable> split(Camera camera, Drawable drawable) {
        if (drawable instanceof Polygon3D){
            return split(camera, (Polygon3D)drawable);
        }
        return Collections.singleton(this);
    }

    private Collection<Drawable> split(Camera camera, Polygon3D polygon3D){


        int commonPointsCount = 0;
        if(polygon3D.a1.equals(a1) || polygon3D.a1.equals(a2) || polygon3D.a1.equals(a3)) commonPointsCount++;
        if(polygon3D.a2.equals(a1) || polygon3D.a2.equals(a2) || polygon3D.a2.equals(a3)) commonPointsCount++;
        if(polygon3D.a3.equals(a1) || polygon3D.a3.equals(a2) || polygon3D.a3.equals(a3)) commonPointsCount++;
        if(commonPointsCount > 1 || polygon3D.compareZ(camera, polygon3D) == 0){
            return Collections.singleton(this);
        }

        Line3D l12 = new Line3D(a1, a2),
                l23 = new Line3D(a2, a3),
                l13 = new Line3D(a1, a3);

        Optional<Point3D> b12 = l12.getIntersection(polygon3D.getPlane()),
                b23 = l23.getIntersection(polygon3D.getPlane()),
                b13 = l13.getIntersection(polygon3D.getPlane());

        Set<Drawable> result = new HashSet<>();
        if(b12.isPresent() && new Region3D(a1, a2).contains(b12.get()) && b23.isPresent() && new Region3D(a3, a2).contains(b23.get()) && !b12.get().equals(b23.get())){
            try{ result.add(new Polygon3D(a1, b12.get(), b23.get(), color)); }catch(ImpossiblePolygonException ignored){}
            try{ result.add(new Polygon3D(a1, a3, b23.get(), color));}catch(ImpossiblePolygonException ignored){}
            try{ result.add(new Polygon3D(a2, b12.get(), b23.get(), color));}catch(ImpossiblePolygonException ignored){}
        }else if(b12.isPresent() && b13.isPresent() && new Region3D(a1, a2).contains(b12.get()) && new Region3D(a1, a3).contains(b13.get()) && !b12.get().equals(b13.get())){
            try{ result.add(new Polygon3D(a1, b12.get(), b13.get(), color));}catch(ImpossiblePolygonException ignored){}
            try{ result.add(new Polygon3D(a3, b13.get(), b12.get(), color));}catch(ImpossiblePolygonException ignored){}
            try{ result.add(new Polygon3D(a2, a3, b12.get(), color));}catch(ImpossiblePolygonException ignored){}
        }else if(b13.isPresent() && b23.isPresent() && new Region3D(a1, a3).contains(b13.get()) && new Region3D(a3, a2).contains(b23.get()) && !b13.get().equals(b23.get())){
            try{ result.add(new Polygon3D(a3, b23.get(), b13.get(), color));}catch(ImpossiblePolygonException ignored){}
            try{ result.add(new Polygon3D(a2, b23.get(), b13.get(), color));}catch(ImpossiblePolygonException ignored){}
            try{ result.add(new Polygon3D(a2, a1, b13.get(), color));}catch(ImpossiblePolygonException ignored){}
        }else{
            result.add(this);
        }
        return result;
    }

    public boolean contains(Point3D p){
        if(a1.equals(p) || a2.equals(p) || a3.equals(p))
            return true;
        Line3D l12 = new Line3D(a1, a2),
                l23 = new Line3D(a2, a3),
                l13 = new Line3D(a1, a3),
                l1p = new Line3D(a1, p),
                l2p = new Line3D(a2, p),
                l3p = new Line3D(a3, p);

        Optional<Point3D> a12 = l1p.getIntersection(l23),
                a22 = l2p.getIntersection(l13),
                a32 = l3p.getIntersection(l12);

        if(!a12.isPresent() || !a22.isPresent() || !a32.isPresent())
            return false;
        return new Region3D(a1, a12.get()).contains(p)
                && new Region3D(a2, a22.get()).contains(p)
                && new Region3D(a3, a32.get()).contains(p);
    }
}
