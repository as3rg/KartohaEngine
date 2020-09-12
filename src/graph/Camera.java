package graph;

import javafx.util.Pair;
import utils.*;

import java.lang.Math;

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

    public double getRotateAngle() {
        return rotate;
    }

    public void setRotateAngle(double rotate) {
        this.rotate = rotate;
    }

    public Point3D getFocus() {
        return focus;
    }

    public void setFocus(Point3D focus) {
        synchronized (this) {
            this.focus = focus;
        }
    }

    public static class Resolution{
        public final double height,width;

        public Resolution(double width, double height) {
            this.height = height;
            this.width = width;
        }
    }
    static class UndefinedVectorOfView extends RuntimeException{}

    private Point3D focus;
    private Vector3D vector;
    private Resolution res;
    private double rotate;

    public Camera(Resolution resolution, Point3D focus, Vector3D vector, double rotate) {
        if(vector.x == 0 && vector.y == 0)
            throw new UndefinedVectorOfView();
        this.focus = focus;
        this.vector = vector;
        this.res = resolution;
        this.rotate = rotate;
    }

    private Pair<Vector3D, Vector3D> getBasises(){
        Vector3D bW, bH;
        if (vector.y != 0){
            double alphaW = -res.width/2 * vector.y / Math.sqrt(vector.x*vector.x+vector.y*vector.y);
            bW = new Vector3D(-alphaW, vector.x/vector.y * alphaW, 0);
        }else{
            bW = new Vector3D(0, -Math.signum(vector.x)*res.width/2, 0);
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

        return new Pair<>(bW, bH);
    }

    public Point2D project(Point3D point3D){
        double t = (vector.x*vector.x + vector.y*vector.y + vector.z*vector.z)/(vector.x*point3D.x + vector.y*point3D.y + vector.z*point3D.z - vector.x*focus.x - vector.y*focus.y - vector.z*focus.z);
        Point3D projection = new Point3D((point3D.x - focus.x)*t+focus.x, (point3D.y - focus.y)*t+focus.y, (point3D.z - focus.z)*t+focus.z);
        Point3D smm = new Point3D(focus.x+vector.x, focus.y+vector.y,focus.z+vector.z);
        Pair<Vector3D, Vector3D> basises = getBasises();
        Vector3D bW = basises.getKey(),
                bH = basises.getValue();
        Double roW = null, roH = null;
        if(bH.y != 0 && bH.z != 0 && bW.y != 0 && bW.z != 0) {
            roW = (bH.z * (projection.y - smm.y) - bH.y * (projection.z - smm.z)) / (bH.y * bW.y - bH.z * bW.z);
            roH = (projection.z-smm.z-bW.z*roW)/bH.z;
        }else if(bH.x != 0 && bH.z != 0 && bW.x != 0 && bW.z != 0) {
            roW = (bH.z * (projection.x - smm.x) - bH.x * (projection.z - smm.z)) / (bH.x * bW.x - bH.z * bW.z);
            roH = (projection.x - smm.x - bW.x * roW) / bH.x;
        }else if(bH.x != 0 && bH.y != 0 && bW.x != 0 && bW.y != 0) {
            roW = (bH.y * (projection.x - smm.x) - bH.x * (projection.y - smm.y)) / (bH.x * bW.x - bH.y * bW.y);
            roH = (projection.x - smm.x - bW.x * roW) / bH.x;
        }else {
            if(bH.x == 0 && bW.x != 0){
                roW = (projection.x-smm.x)/bW.x;
            }else if(bH.y == 0 && bW.y != 0){
                roW = (projection.y-smm.y)/bW.y;
            }else if(bH.z == 0 && bW.z != 0){
                roW = (projection.z-smm.z)/bW.z;
            }

            if(bH.x != 0 && bW.x == 0){
                roH = (projection.x-smm.x)/bH.x;
            }else if(bH.y != 0 && bW.y == 0){
                roH = (projection.y-smm.y)/bH.y;
            }else if(bH.z != 0 && bW.z == 0){
                roH = (projection.z-smm.z)/bH.z;
            }

            if(roW == null && roH != null){
                if(bW.x != 0){
                    roW = (projection.x-smm.x-bH.x*roH)/bW.x;
                }else if(bW.y != 0){
                    roW = (projection.y-smm.y-bH.y*roH)/bW.y;
                }else if(bW.z != 0){
                    roW = (projection.z-smm.z-bH.z*roH)/bW.z;
                }
            }

            if(roH == null && roW != null){
                if(bH.x != 0){
                    roH = (projection.x-smm.x-bW.x*roW)/bH.x;
                }else if(bH.y != 0){
                    roH = (projection.y-smm.y-bW.y*roW)/bH.y;
                }else if(bH.z != 0){
                    roH = (projection.z-smm.z-bW.z*roW)/bH.z;
                }
            }
        }
        assert roH != null && roW != null;
        double cos = utils.Math.destroyMinusZeros(Math.cos(rotate)),
                sin = utils.Math.destroyMinusZeros(Math.sin(rotate));
        return new Point2D(roW*res.width*cos-roH*res.height*sin + res.width/2, -roW*res.width*sin-roH*res.height*cos + res.height/2);
    }

    public Polygon2D project(Polygon3D poly){
        return new Polygon2D(project(poly.a1), project(poly.a2), project(poly.a3), poly.color);
    }
}
