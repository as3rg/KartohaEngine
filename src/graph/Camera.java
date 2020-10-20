package graph;

import geometry.objects3D.*;
import javafx.util.Pair;
import geometry.objects2D.Point2D;
import utils.Triplet;

import java.util.Optional;

public class Camera {

    public static class Resolution{
        public final double height,width;

        public Resolution(double width, double height) {
            this.height = height;
            this.width = width;
        }
    }

    private Screen screen;
    private Resolution res;
    private double rotate;
    private Vector3D bW, bH;

    public Camera(Screen screen, Resolution resolution, double rotate) {
        this.screen = screen;
        this.res = resolution;
        this.rotate = -rotate;
        calculateBasises();
    }

    public Pair<Vector3D, Vector3D> getBasises(double w, double h){
        return new Pair<>(bW.multiply(w),bH.multiply(h));
    }

    public void calculateBasises(){
        Vector3D bW, bH;
        double alphaW = 1 / Math.sqrt(screen.vector.x*screen.vector.x+screen.vector.y*screen.vector.y);
        bW = new Vector3D(-alphaW*screen.vector.y, screen.vector.x * alphaW, 0);

        if (screen.vector.x != 0 && screen.vector.z != 0) {
            double alphaH = - screen.vector.x * screen.vector.z / Math.sqrt(Math.pow(screen.vector.x * screen.vector.z, 2) + Math.pow(screen.vector.y * screen.vector.z, 2) + 2 * Math.pow(screen.vector.x * screen.vector.y, 2) + Math.pow(screen.vector.x, 4) + Math.pow(screen.vector.y, 4));
            double ybH = screen.vector.y/screen.vector.x* alphaH,
                    zbH = -(screen.vector.x* alphaH +screen.vector.y*ybH)/screen.vector.z;
            bH = new Vector3D(Math.signum(zbH)* alphaH, Math.signum(zbH)*ybH, Math.abs(zbH));
        }else if(screen.vector.z != 0){
            double alphaH = Math.signum(screen.vector.y/screen.vector.z) * screen.vector.z / Math.sqrt(screen.vector.y*screen.vector.y+screen.vector.z*screen.vector.z);
            bH = new Vector3D(0, -alphaH, -screen.vector.y/screen.vector.z*alphaH);
        }else{
            bH = new Vector3D(0,0,1);
        }

        double cos = utils.Math.roundNearZero(Math.cos(rotate)),
                sin = utils.Math.roundNearZero(Math.sin(rotate));
        this.bH = new Vector3D(bH.x*cos-bW.x*sin,bH.y*cos-bW.y*sin,bH.z*cos-bW.z*sin);
        this.bW = new Vector3D(bW.x*cos+bH.x*sin,bW.y*cos+bH.y*sin,bW.z*cos+bH.z*sin);
    }

    public Vector3D getRotatedVector(double r, double t){
        Pair<Vector3D, Vector3D> basises = getBasises(1, 1);
        Vector3D bH = basises.getValue(),
                bW = basises.getKey();
        Vector3D v1 = getRotatedVector(screen.vector, bW, r);
        Vector3D v2 = getRotatedVector(v1, bH, t);
        return v2;
    }

    private Vector3D getRotatedVector(Vector3D v, Vector3D rotationVector, double a){
        if(utils.Math.roundNearZero(a) == 0)
            return v;
        Vector3D rVector = null;
        double n = v.getLength();

        Point3D zero = new Point3D(0, 0, 0);
        Plane3D plane3D = new Plane3D(zero, v.addToPoint(zero), rotationVector.addToPoint(zero));
        double x = plane3D.vector.x, y = plane3D.vector.y, z = plane3D.vector.z;

        if (utils.Math.roundNearZero(x) != 0){
            if (utils.Math.roundNearZero(v.y - v.x*y/x) != 0){
                double k = utils.Math.roundNearZero(v.z - v.x*z/x)/(v.y - v.x*y/x),
                        n2 = n*n*Math.cos(a)/(v.y - v.x*y/x);
                double a2 = Math.pow((z-y*k)/x, 2)+k*k+1,
                        b2 = 2*y*n2/(x*x)*(z-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/x, 2)+n2*n2-n*n;
                double zT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT = n2 - k*zT,
                        xT = -(y*yT+z*zT)/x;
                double zT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT2 = n2 - k*zT2,
                        xT2 = -(y*yT2+z*zT2)/x;
                if ((Math.sin(a) < 0) == (new Vector3D(xT, yT, zT).subtract(rotationVector).getLength() < new Vector3D(xT2, yT2, zT2).subtract(rotationVector).getLength())){
                    rVector = new Vector3D(xT, yT, zT);
                }else{
                    rVector = new Vector3D(xT2, yT2, zT2);
                }
            }else if (utils.Math.roundNearZero(v.z - v.x*z/x) != 0){
                double k = utils.Math.roundNearZero(v.y - v.x*y/x)/(v.z - v.x*z/x),
                        n2 = n*n*Math.cos(a)/(v.z - v.x*z/x);
                double a2 = Math.pow((y-z*k)/x, 2)+k*k+1,
                        b2 = 2*z*n2/(x*x)*(y-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/x, 2)+n2*n2-n*n;
                double yT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT = n2 - k*yT,
                        xT = -(y*yT+z*zT)/x;
                double yT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT2 = n2 - k*yT2,
                        xT2 = -(y*yT2+z*zT2)/x;
                if ((Math.sin(a) < 0) == (new Vector3D(xT, yT, zT).subtract(rotationVector).getLength() < new Vector3D(xT2, yT2, zT2).subtract(rotationVector).getLength())){
                    rVector = new Vector3D(xT, yT, zT);
                }else{
                    rVector = new Vector3D(xT2, yT2, zT2);
                }
            }
        }else if (utils.Math.roundNearZero(y) != 0){
            if (utils.Math.roundNearZero(v.x - v.y*x/y) != 0){
                double k = utils.Math.roundNearZero(v.z - v.y*z/y)/(v.x - v.y*x/y),
                        n2 = n*n*Math.cos(a)/(v.x - v.y*x/y);
                double a2 = Math.pow((z-x*k)/y, 2)+k*k+1,
                        b2 = 2*x*n2/(y*y)*(z-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/y, 2)+n2*n2-n*n;
                double zT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT = n2 - k*zT,
                        yT = -(x*xT+z*zT)/y;
                double zT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT2 = n2 - k*zT2,
                        yT2 = -(x*xT2+z*zT2)/y;
                if ((Math.sin(a) < 0) == (new Vector3D(xT, yT, zT).subtract(rotationVector).getLength() < new Vector3D(xT2, yT2, zT2).subtract(rotationVector).getLength())){
                    rVector = new Vector3D(xT, yT, zT);
                }else{
                    rVector = new Vector3D(xT2, yT2, zT2);
                }
            }else if (utils.Math.roundNearZero(v.z - v.y*z/y) != 0){
                double k = utils.Math.roundNearZero(v.x - v.y*x/y)/(v.z - v.y*z/y),
                        n2 = n*n*Math.cos(a)/(v.z - v.y*z/y);
                double a2 = Math.pow((x-z*k)/y, 2)+k*k+1,
                        b2 = 2*z*n2/(y*y)*(x-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/y, 2)+n2*n2-n*n;
                double xT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT = n2 - k*xT,
                        yT = -(x*xT+z*zT)/y;
                double xT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT2 = n2 - k*xT2,
                        yT2 = -(x*xT2+z*zT2)/y;
                if ((Math.sin(a) < 0) == (new Vector3D(xT, yT, zT).subtract(rotationVector).getLength() < new Vector3D(xT2, yT2, zT2).subtract(rotationVector).getLength())){
                    rVector = new Vector3D(xT, yT, zT);
                }else{
                    rVector = new Vector3D(xT2, yT2, zT2);
                }
            }
        }else if (utils.Math.roundNearZero(z) != 0){
            if (utils.Math.roundNearZero(v.y - v.z*y/z) != 0){
                double k = utils.Math.roundNearZero(v.x - v.z*x/z)/(v.y - v.z*y/z),
                        n2 = n*n*Math.cos(a)/(v.y - v.z*y/z);
                double a2 = Math.pow((x-y*k)/z, 2)+k*k+1,
                        b2 = 2*y*n2/(z*z)*(x-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/z, 2)+n2*n2-n*n;
                double xT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT = n2 - k*xT,
                        zT = -(y*yT+x*xT)/z;
                double xT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT2 = n2 - k*xT2,
                        zT2 = -(y*yT2+x*xT2)/z;
                if ((Math.sin(a) < 0) == (new Vector3D(xT, yT, zT).subtract(rotationVector).getLength() < new Vector3D(xT2, yT2, zT2).subtract(rotationVector).getLength())){
                    rVector = new Vector3D(xT, yT, zT);
                }else{
                    rVector = new Vector3D(xT2, yT2, zT2);
                }
            }else if (utils.Math.roundNearZero(v.x - v.z*x/z) != 0){
                double k = utils.Math.roundNearZero(v.y - v.z*y/z)/(v.x - v.z*x/z),
                        n2 = n*n*Math.cos(a)/(v.x - v.z*x/z);
                double a2 = Math.pow((y-x*k)/z, 2)+k*k+1,
                        b2 = 2*x*n2/(z*z)*(y-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/z, 2)+n2*n2-n*n;
                double yT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT = n2 - k*yT,
                        zT = -(y*yT+x*xT)/z;
                double yT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT2 = n2 - k*yT2,
                        zT2 = -(y*yT2+x*xT2)/z;
                if ((Math.sin(a) < 0) == (new Vector3D(xT, yT, zT).subtract(rotationVector).getLength() < new Vector3D(xT2, yT2, zT2).subtract(rotationVector).getLength())){
                    rVector = new Vector3D(xT, yT, zT);
                }else{
                    rVector = new Vector3D(xT2, yT2, zT2);
                }
            }
        }
        assert rVector != null;
        if(rVector.x == 0 && rVector.y == 0)
            return v;
        return rVector;
    }

    public Triplet<Vector3D, Vector3D, Vector3D> getMovingBasises(double f, double r, double t){
        Vector3D mR, mF, mT;
        Pair<Vector3D, Vector3D> basises = getBasises(r, t);
        mR = basises.getKey();
        mT = basises.getValue();

        double n = screen.vector.getLength();
        mF = screen.vector.multiply(f/n);

        return new Triplet<>(mF, mR, mT);
    }
    
    public Optional<Point2D> project(Point3D point3D){
        if(screen.focus.equals(point3D))
            return Optional.empty();
        Optional<Point3D> projectionO = screen.getIntersection(new Line3D(screen.focus, point3D));
        if (!projectionO.isPresent() || new Region3D(point3D, projectionO.get()).contains(screen.focus))
            return Optional.empty();
        Point3D projection = projectionO.get();
        Point3D smm = screen.point;
        Pair<Vector3D, Vector3D> basises = getBasises(res.width/2, res.height/2);
        Vector3D bW = basises.getKey(),
                bH = basises.getValue();
        Double roW = null, roH = null;
        if(bH.y != 0 && bH.z != 0 && bW.y != 0 && bW.z != 0) {
            roW = (bH.z * (projection.y - smm.y) - bH.y * (projection.z - smm.z)) / (bH.z * bW.y - bH.y * bW.z);
            roH = (projection.z-smm.z-bW.z*roW)/bH.z;
        }else if(bH.x != 0 && bH.z != 0 && bW.x != 0 && bW.z != 0) {
            roW = (bH.z * (projection.x - smm.x) - bH.x * (projection.z - smm.z)) / (bH.z * bW.x - bH.x * bW.z);
            roH = (projection.x - smm.x - bW.x * roW) / bH.x;
        }else if(bH.x != 0 && bH.y != 0 && bW.x != 0 && bW.y != 0) {
            roW = (bH.y * (projection.x - smm.x) - bH.x * (projection.y - smm.y)) / (bH.y * bW.x - bH.x * bW.y);
            roH = (projection.x - smm.x - bW.x * roW) / bH.x;
        }else {
            if(bH.x == 0 && bW.x != 0){
                roW = (projection.x-smm.x)/bW.x;
            }else if(bH.y == 0 && bW.y != 0){
                roW = (projection.y-smm.y)/bW.y;
            }else if(bH.z == 0 && bW.z != 0){
                roW = (projection.z-smm.z)/bW.z;
            }

            if(bH.y != 0 && bW.y == 0) {
                roH = (projection.y-smm.y)/bH.y;
            }else if(bH.x != 0 && bW.x == 0){
                roH = (projection.x-smm.x)/bH.x;
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
        return Optional.of(new Point2D(roW*res.width + res.width/2, -roH*res.height + res.height/2));
    }
    
    public Resolution getResolution() {
        return res;
    }

    public void setResolution(Resolution res) {
        synchronized (this) {
            this.res = res;
        }
    }

    public double getRotateAngle() {
        return rotate;
    }

    public void setRotateAngle(double rotate) {
        synchronized (this) {
            this.rotate = rotate;
            calculateBasises();
        }
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        synchronized (this) {
            this.screen = screen;
            calculateBasises();
        }
    }
}
