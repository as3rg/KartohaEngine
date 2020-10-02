package graph;

import com.sun.istack.internal.Nullable;
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

    public double getRotateAngle() {
        return rotate;
    }

    public void setRotateAngle(double rotate) {
        this.rotate = rotate;
        calculateBasises();
    }

    public Plane getScreen() {
        return screen;
    }

    public void setScreen(Plane screen) {
        this.screen = screen;
    }

    public static class Resolution{
        public final double height,width;

        public Resolution(double width, double height) {
            this.height = height;
            this.width = width;
        }
    }
    static class UndefinedVectorOfView extends RuntimeException{}

    private Plane screen;
    private Resolution res;
    private double rotate;

    public Camera(Plane screen, Resolution resolution, double rotate) {
        this.screen = screen;
        if(screen.vector.x == 0 && screen.vector.y == 0)
            throw new UndefinedVectorOfView();
        this.res = resolution;
        this.rotate = -rotate;
        calculateBasises();
    }

    public Pair<Vector3D, Vector3D> getBasises(double w, double h){
        return new Pair<>(new Vector3D(w*bW.x,w*bW.y,w*bW.z),new Vector3D(h*bH.x,h*bH.y,h*bH.z));
    }

    Vector3D bW, bH;

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

        double cos = utils.Math.destroyMinusZeros(Math.cos(rotate)),
                sin = utils.Math.destroyMinusZeros(Math.sin(rotate));
        this.bH = new Vector3D(bH.x*cos-bW.x*sin,bH.y*cos-bW.y*sin,bH.z*cos-bW.z*sin);
        this.bW = new Vector3D(bW.x*cos+bH.x*sin,bW.y*cos+bH.y*sin,bW.z*cos+bH.z*sin);
    }

    public Vector3D getRightRotatedVectors(double r){
        Vector3D rR = null;
        Pair<Vector3D, Vector3D> basises = getBasises(1, 1);
        Vector3D bW = basises.getKey();
        double xW = screen.vector.x + bW.x,
                yW = screen.vector.y + bW.y,
                zW = screen.vector.z + bW.z,
                n = Math.sqrt(Math.pow(screen.vector.x, 2)+Math.pow(screen.vector.y, 2)+Math.pow(screen.vector.z, 2));
        Point3D zero = new Point3D(0,0,0);
        Plane plane = new Plane(zero, screen.vector.addToPoint(zero), new Point3D(xW, yW, zW));
        double x = plane.vector.x, y = plane.vector.y, z = plane.vector.z;
        if (utils.Math.destroyMinusZeros(x) != 0){
            if (utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.x*y/x) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.x*z/x)/(screen.vector.y - screen.vector.x*y/x),
                        n2 = n*n*Math.cos(r)/(screen.vector.y - screen.vector.x*y/x);
                double a2 = Math.pow((z-y*k)/x, 2)+k*k+1,
                        b2 = 2*y*n2/(x*x)*(z-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/x, 2)+n2*n2-n*n;
                double zR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR = n2 - k*zR,
                        xR = -(y*yR+z*zR)/x;
                double zR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR2 = n2 - k*zR2,
                        xR2 = -(y*yR2+z*zR2)/x;
                if ((r < 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.x*z/x) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.x*y/x)/(screen.vector.z - screen.vector.x*z/x),
                        n2 = n*n*Math.cos(r)/(screen.vector.z - screen.vector.x*z/x);
                double a2 = Math.pow((y-z*k)/x, 2)+k*k+1,
                        b2 = 2*z*n2/(x*x)*(y-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/x, 2)+n2*n2-n*n;
                double yR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR = n2 - k*yR,
                        xR = -(y*yR+z*zR)/x;
                double yR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR2 = n2 - k*yR2,
                        xR2 = -(y*yR2+z*zR2)/x;
                if ((r < 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(y) != 0){
            if (utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.y*x/y) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.y*z/y)/(screen.vector.x - screen.vector.y*x/y),
                        n2 = n*n*Math.cos(r)/(screen.vector.x - screen.vector.y*x/y);
                double a2 = Math.pow((z-x*k)/y, 2)+k*k+1,
                        b2 = 2*x*n2/(y*y)*(z-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/y, 2)+n2*n2-n*n;
                double zR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR = n2 - k*zR,
                        yR = -(x*xR+z*zR)/y;
                double zR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR2 = n2 - k*zR2,
                        yR2 = -(x*xR2+z*zR2)/y;
                if ((r < 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.y*z/y) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.y*x/y)/(screen.vector.z - screen.vector.y*z/y),
                        n2 = n*n*Math.cos(r)/(screen.vector.z - screen.vector.y*z/y);
                double a2 = Math.pow((x-z*k)/y, 2)+k*k+1,
                        b2 = 2*z*n2/(y*y)*(x-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/y, 2)+n2*n2-n*n;
                double xR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR = n2 - k*xR,
                        yR = -(x*xR+z*zR)/y;
                double xR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR2 = n2 - k*xR2,
                        yR2 = -(x*xR2+z*zR2)/y;
                if ((r < 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(z) != 0){
            if (utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.z*y/z) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.z*x/z)/(screen.vector.y - screen.vector.z*y/z),
                        n2 = n*n*Math.cos(r)/(screen.vector.y - screen.vector.z*y/z);
                double a2 = Math.pow((x-y*k)/z, 2)+k*k+1,
                        b2 = 2*y*n2/(z*z)*(x-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/z, 2)+n2*n2-n*n;
                double xR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR = n2 - k*xR,
                        zR = -(y*yR+x*xR)/z;
                double xR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR2 = n2 - k*xR2,
                        zR2 = -(y*yR2+x*xR2)/z;
                if ((r < 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.z*x/z) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.z*y/z)/(screen.vector.x - screen.vector.z*x/z),
                        n2 = n*n*Math.cos(r)/(screen.vector.x - screen.vector.z*x/z);
                double a2 = Math.pow((y-x*k)/z, 2)+k*k+1,
                        b2 = 2*x*n2/(z*z)*(y-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/z, 2)+n2*n2-n*n;
                double yR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR = n2 - k*yR,
                        zR = -(y*yR+x*xR)/z;
                double yR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR2 = n2 - k*yR2,
                        zR2 = -(y*yR2+x*xR2)/z;
                if ((r < 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }
        }
        assert rR != null;
        return rR;
    }

    public Vector3D getTopRotatedVectors(double t){
        Vector3D rT = null;
        Pair<Vector3D, Vector3D> basises = getBasises(1, 1);
        Vector3D bH = basises.getValue();
        double xH = screen.vector.x + bH.x,
                yH = screen.vector.y + bH.y,
                zH = screen.vector.z + bH.z,
                n = Math.sqrt(Math.pow(screen.vector.x, 2)+Math.pow(screen.vector.y, 2)+Math.pow(screen.vector.z, 2));
        Point3D zero = new Point3D(0,0,0);
        Plane plane = new Plane(zero, screen.vector.addToPoint(zero), new Point3D(xH, yH, zH));
        double x = plane.vector.x, y = plane.vector.y, z = plane.vector.z;
        if (utils.Math.destroyMinusZeros(x) != 0){
            if (utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.x*y/x) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.x*z/x)/(screen.vector.y - screen.vector.x*y/x),
                        n2 = n*n*Math.cos(t)/(screen.vector.y - screen.vector.x*y/x);
                double a2 = Math.pow((z-y*k)/x, 2)+k*k+1,
                        b2 = 2*y*n2/(x*x)*(z-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/x, 2)+n2*n2-n*n;
                double zT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT = n2 - k*zT,
                        xT = -(y*yT+z*zT)/x;
                double zT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT2 = n2 - k*zT2,
                        xT2 = -(y*yT2+z*zT2)/x;
                if ((t < 0) == (Math.pow(xT-xH,2)+Math.pow(yT-yH,2)+Math.pow(zT-zH,2) < Math.pow(xT2-xH,2)+Math.pow(yT2-yH,2)+Math.pow(zT2-zH,2))){
                    rT = new Vector3D(xT, yT, zT);
                }else{
                    rT = new Vector3D(xT2, yT2, zT2);
                }
            }else if (utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.x*z/x) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.x*y/x)/(screen.vector.z - screen.vector.x*z/x),
                        n2 = n*n*Math.cos(t)/(screen.vector.z - screen.vector.x*z/x);
                double a2 = Math.pow((y-z*k)/x, 2)+k*k+1,
                        b2 = 2*z*n2/(x*x)*(y-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/x, 2)+n2*n2-n*n;
                double yT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT = n2 - k*yT,
                        xT = -(y*yT+z*zT)/x;
                double yT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT2 = n2 - k*yT2,
                        xT2 = -(y*yT2+z*zT2)/x;
                if ((t < 0) == (Math.pow(xT-xH,2)+Math.pow(yT-yH,2)+Math.pow(zT-zH,2) < Math.pow(xT2-xH,2)+Math.pow(yT2-yH,2)+Math.pow(zT2-zH,2))){
                    rT = new Vector3D(xT, yT, zT);
                }else{
                    rT = new Vector3D(xT2, yT2, zT2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(y) != 0){
            if (utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.y*x/y) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.y*z/y)/(screen.vector.x - screen.vector.y*x/y),
                        n2 = n*n*Math.cos(t)/(screen.vector.x - screen.vector.y*x/y);
                double a2 = Math.pow((z-x*k)/y, 2)+k*k+1,
                        b2 = 2*x*n2/(y*y)*(z-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/y, 2)+n2*n2-n*n;
                double zT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT = n2 - k*zT,
                        yT = -(x*xT+z*zT)/y;
                double zT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT2 = n2 - k*zT2,
                        yT2 = -(x*xT2+z*zT2)/y;
                if ((t < 0) == (Math.pow(xT-xH,2)+Math.pow(yT-yH,2)+Math.pow(zT-zH,2) < Math.pow(xT2-xH,2)+Math.pow(yT2-yH,2)+Math.pow(zT2-zH,2))){
                    rT = new Vector3D(xT, yT, zT);
                }else{
                    rT = new Vector3D(xT2, yT2, zT2);
                }
            }else if (utils.Math.destroyMinusZeros(screen.vector.z - screen.vector.y*z/y) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.y*x/y)/(screen.vector.z - screen.vector.y*z/y),
                        n2 = n*n*Math.cos(t)/(screen.vector.z - screen.vector.y*z/y);
                double a2 = Math.pow((x-z*k)/y, 2)+k*k+1,
                        b2 = 2*z*n2/(y*y)*(x-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/y, 2)+n2*n2-n*n;
                double xT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT = n2 - k*xT,
                        yT = -(x*xT+z*zT)/y;
                double xT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zT2 = n2 - k*xT2,
                        yT2 = -(x*xT2+z*zT2)/y;
                if ((t < 0) == (Math.pow(xT-xH,2)+Math.pow(yT-yH,2)+Math.pow(zT-zH,2) < Math.pow(xT2-xH,2)+Math.pow(yT2-yH,2)+Math.pow(zT2-zH,2))){
                    rT = new Vector3D(xT, yT, zT);
                }else{
                    rT = new Vector3D(xT2, yT2, zT2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(z) != 0){
            if (utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.z*y/z) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.z*x/z)/(screen.vector.y - screen.vector.z*y/z),
                        n2 = n*n*Math.cos(t)/(screen.vector.y - screen.vector.z*y/z);
                double a2 = Math.pow((x-y*k)/z, 2)+k*k+1,
                        b2 = 2*y*n2/(z*z)*(x-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/z, 2)+n2*n2-n*n;
                double xT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT = n2 - k*xT,
                        zT = -(y*yT+x*xT)/z;
                double xT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yT2 = n2 - k*xT2,
                        zT2 = -(y*yT2+x*xT2)/z;
                if ((t < 0) == (Math.pow(xT-xH,2)+Math.pow(yT-yH,2)+Math.pow(zT-zH,2) < Math.pow(xT2-xH,2)+Math.pow(yT2-yH,2)+Math.pow(zT2-zH,2))){
                    rT = new Vector3D(xT, yT, zT);
                }else{
                    rT = new Vector3D(xT2, yT2, zT2);
                }
            }else if (utils.Math.destroyMinusZeros(screen.vector.x - screen.vector.z*x/z) != 0){
                double k = utils.Math.destroyMinusZeros(screen.vector.y - screen.vector.z*y/z)/(screen.vector.x - screen.vector.z*x/z),
                        n2 = n*n*Math.cos(t)/(screen.vector.x - screen.vector.z*x/z);
                double a2 = Math.pow((y-x*k)/z, 2)+k*k+1,
                        b2 = 2*x*n2/(z*z)*(y-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/z, 2)+n2*n2-n*n;
                double yT = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT = n2 - k*yT,
                        zT = -(y*yT+x*xT)/z;
                double yT2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xT2 = n2 - k*yT2,
                        zT2 = -(y*yT2+x*xT2)/z;
                if ((t < 0) == (Math.pow(xT-xH,2)+Math.pow(yT-yH,2)+Math.pow(zT-zH,2) < Math.pow(xT2-xH,2)+Math.pow(yT2-yH,2)+Math.pow(zT2-zH,2))){
                    rT = new Vector3D(xT, yT, zT);
                }else{
                    rT = new Vector3D(xT2, yT2, zT2);
                }
            }
        }
        assert rT != null;
        return rT;
    }

    public Triplet<Vector3D, Vector3D, Vector3D> getMovingBasises(double f, double r, double t){
        Vector3D mR, mF, mT;
        Pair<Vector3D, Vector3D> basises = getBasises(r, t);
        mR = basises.getKey();
        mT = basises.getValue();

        double n = Math.sqrt(Math.pow(screen.vector.x, 2)+Math.pow(screen.vector.y, 2)+Math.pow(screen.vector.z, 2));
        mF = new Vector3D(screen.vector.x*f/n, screen.vector.y*f/n, screen.vector.z*f/n);

        return new Triplet<>(mF, mR, mT);
    }

    @Nullable
    public Point2D project(Point3D point3D){
        Point3D projection = screen.getIntersectionPoint(new Line(screen.point, point3D));
        if (projection == null)
            return null;
        Point3D smm = new Point3D(screen.point.x+screen.vector.x, screen.point.y+screen.vector.y,screen.point.z+screen.vector.z);
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
        double cos = 1,// utils.Math.destroyMinusZeros(Math.cos(-rotate)),
                sin = 0;//utils.Math.destroyMinusZeros(Math.sin(-rotate));
        return new Point2D(roW*res.width*cos-roH*res.height*sin + res.width/2, -roW*res.width*sin-roH*res.height*cos + res.height/2);
    }

    @Nullable
    public Polygon2D project(Polygon3D poly){
        Point2D a = project(poly.a1),
                b = project(poly.a2),
                c = project(poly.a3);
        if (a == null || b == null || c == null)
            return null;
        return new Polygon2D(a,b,c, poly.color);
    }
}
