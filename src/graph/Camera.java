package graph;

import javafx.util.Pair;
import utils.*;

public class Camera {
    public Resolution getResolution() {
        return res;
    }

    public void setResolution(Resolution res) {
        synchronized (this) {
            this.res = res;
        }
    }

    public Vector3D getVector() {
        return vector;
    }

    public void setVector(Vector3D vector) {
        synchronized (this) {
            this.vector = vector;
        }
    }

    public Point3D getFocus() {
        return focus;
    }

    public void setFocus(Point3D focus) {
        synchronized (this) {
            this.focus = focus;
        }
    }

    static class Resolution{
        final double height,width;

        Resolution(double height, double width) {
            this.height = height;
            this.width = width;
        }
    }
    static class UndefinedVectorOfView extends RuntimeException{}

    private Point3D focus;
    private Vector3D vector;
    private Resolution res;
    private Double rotate;

    public Camera(Resolution resolution, Point3D focus, Vector3D vector) {
        if(vector.x == 0 && vector.y == 0)
            throw new UndefinedVectorOfView();
        this.focus = focus;
        this.vector = vector;
        this.res = resolution;
    }

    private Pair<Vector3D, Vector3D> getBasises(){
        Vector3D bW, bH;
        if (vector.y != 0){
            double alphaW = -res.width/2 * vector.y / Math.sqrt(vector.x*vector.x+vector.y*vector.y);
            bW = new Vector3D(-alphaW, vector.x/vector.y * alphaW, 0);
        }else{
            bW = new Vector3D(0, Math.signum(vector.x)*res.width/2, 0);
        }

        if (vector.x != 0 && vector.z != 0) {
            double alphaH = res.height * vector.x * vector.z / 2 / Math.sqrt(Math.pow(vector.x * vector.z, 2) + Math.pow(vector.y * vector.z, 2) + 2 * Math.pow(vector.x * vector.y, 2) + Math.pow(vector.x, 4) + Math.pow(vector.y, 2));
            double xbH = -alphaH,
                    ybH = vector.y/vector.x*xbH,
                    zbH = -(vector.x*xbH+vector.y*ybH)/vector.z;
            bH = new Vector3D(Math.signum(zbH)*xbH, Math.signum(zbH)*ybH, Math.abs(zbH));
        }else if(vector.z != 0){
            double alphaH = Math.signum(vector.y/vector.z) * res.height * vector.z / 2 / Math.sqrt(vector.y*vector.y+vector.z*vector.z);
            bH = new Vector3D(0, -alphaH, vector.y/vector.z*alphaH);
        }else{
            bH = new Vector3D(0,0,res.height/2);
        }

        double cos = Math.cos(rotate),
                sin = Math.sin(rotate);
        Vector3D bH2 = new Vector3D(bW.x*sin+bH.x*cos,bW.y*sin+bH.y*cos,bW.z*sin+bH.z*cos),
                bW2 = new Vector3D(bW.x*cos+bH.x*sin,bW.y*cos+bH.y*sin,bW.z*cos+bH.z*sin);
        return new Pair<>(bW2, bH2);
    }

    public Point2D project(Point3D point3D){
        double t = (vector.x*vector.x + vector.y*vector.y + vector.z*vector.z)/(vector.x*point3D.x + vector.y*point3D.y + vector.z*point3D.z - vector.x*focus.x - vector.y*focus.y - vector.z*focus.z);
        Point3D projection = new Point3D((point3D.x - focus.x)*t+focus.x, (point3D.y - focus.y)*t+focus.y, (point3D.z - focus.z)*t+focus.z);
        Point3D smm = new Point3D(focus.x+vector.x, focus.y+vector.y,focus.z+vector.z);
        Pair<Vector3D, Vector3D> basises = getBasises();
        Vector3D bW = basises.getKey(),
                bH = basises.getValue();
        double roW;
        if(bH.y != 0)
            roW = (bH.z*(projection.y-smm.y)-bH.y*(projection.z-smm.z))/(bH.y*bW.y - bH.z*bW.z);
        else
            roW = (bH.z*(projection.x-smm.x)-bH.x*(projection.z-smm.z))/(bH.x*bW.x - bH.z*bW.z);
        double roH = (bH.z-smm.z-bW.z*roW)/bH.z;
        return new Point2D(roW*res.width, roH*res.height);
    }

    public Polygon2D project(Polygon3D poly){
        return new Polygon2D(project(poly.A), project(poly.B), project(poly.C));
    }
}
