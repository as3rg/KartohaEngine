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
        this.rotate = -rotate;
    }

    public Pair<Vector3D, Vector3D> getBasises(double w, double h){
        Vector3D bW, bH;
        double alphaW = w / Math.sqrt(vector.x*vector.x+vector.y*vector.y);
        bW = new Vector3D(-alphaW*vector.y, vector.x * alphaW, 0);

        if (vector.x != 0 && vector.z != 0) {
            double alphaH = -h * vector.x * vector.z / Math.sqrt(Math.pow(vector.x * vector.z, 2) + Math.pow(vector.y * vector.z, 2) + 2 * Math.pow(vector.x * vector.y, 2) + Math.pow(vector.x, 4) + Math.pow(vector.y, 4));
            double ybH = vector.y/vector.x* alphaH,
                    zbH = -(vector.x* alphaH +vector.y*ybH)/vector.z;
            bH = new Vector3D(Math.signum(zbH)* alphaH, Math.signum(zbH)*ybH, Math.abs(zbH));
        }else if(vector.z != 0){
            double alphaH = Math.signum(vector.y/vector.z) * h * vector.z / Math.sqrt(vector.y*vector.y+vector.z*vector.z);
            bH = new Vector3D(0, -alphaH, -vector.y/vector.z*alphaH);
        }else{
            bH = new Vector3D(0,0,h);
        }

        return new Pair<>(bW, bH);
    }

    public Vector3D getRightRotatedVectors(double r){
        Vector3D rR = null;
        Pair<Vector3D, Vector3D> basises = getBasises(1, 1);
        Vector3D bW = basises.getKey(),
                bH = basises.getValue();
        double xW = vector.x + bW.x*Math.cos(rotate) + bH.x*Math.sin(rotate),
                yW = vector.y + bW.y*Math.cos(rotate) + bH.y*Math.sin(rotate),
                zW = vector.z + bW.z*Math.cos(rotate) + bH.z*Math.sin(rotate),
                n = Math.sqrt(Math.pow(vector.x, 2)+Math.pow(vector.y, 2)+Math.pow(vector.z, 2));
        double x = vector.y * zW - vector.z*yW,
                y = vector.z * xW - vector.x * zW,
                z = vector.x * yW - vector.y * xW;
        if (utils.Math.destroyMinusZeros(x) != 0){
            if (utils.Math.destroyMinusZeros(vector.y - vector.x*y/x) != 0){
                double k = utils.Math.destroyMinusZeros(vector.z - vector.x*z/x)/(vector.y - vector.x*y/x),
                        n2 = n*n*Math.cos(r)/(vector.y - vector.x*y/x);
                double a2 = Math.pow((z-y*k)/x, 2)+k*k+1,
                        b2 = 2*y*n2/(x*x)*(z-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/x, 2)+n2*n2-n*n;
                double zR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR = n2 - k*zR,
                        xR = -(y*yR+z*zR)/x;
                double zR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR2 = n2 - k*zR2,
                        xR2 = -(y*yR2+z*zR2)/x;
                if ((r > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(vector.z - vector.x*z/x) != 0){
                double k = utils.Math.destroyMinusZeros(vector.y - vector.x*y/x)/(vector.z - vector.x*z/x),
                        n2 = n*n*Math.cos(r)/(vector.z - vector.x*z/x);
                double a2 = Math.pow((y-z*k)/x, 2)+k*k+1,
                        b2 = 2*z*n2/(x*x)*(y-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/x, 2)+n2*n2-n*n;
                double yR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR = n2 - k*yR,
                        xR = -(y*yR+z*zR)/x;
                double yR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR2 = n2 - k*yR2,
                        xR2 = -(y*yR2+z*zR2)/x;
                if ((r > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(y) != 0){
            if (utils.Math.destroyMinusZeros(vector.x - vector.y*x/y) != 0){
                double k = utils.Math.destroyMinusZeros(vector.z - vector.y*z/y)/(vector.x - vector.y*x/y),
                        n2 = n*n*Math.cos(r)/(vector.x - vector.y*x/y);
                double a2 = Math.pow((z-x*k)/y, 2)+k*k+1,
                        b2 = 2*x*n2/(y*y)*(z-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/y, 2)+n2*n2-n*n;
                double zR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR = n2 - k*zR,
                        yR = -(x*xR+z*zR)/y;
                double zR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR2 = n2 - k*zR2,
                        yR2 = -(x*xR2+z*zR2)/y;
                if ((r > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(vector.z - vector.y*z/y) != 0){
                double k = utils.Math.destroyMinusZeros(vector.x - vector.y*x/y)/(vector.z - vector.y*z/y),
                        n2 = n*n*Math.cos(r)/(vector.z - vector.y*z/y);
                double a2 = Math.pow((x-z*k)/y, 2)+k*k+1,
                        b2 = 2*z*n2/(y*y)*(x-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/y, 2)+n2*n2-n*n;
                double xR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR = n2 - k*xR,
                        yR = -(x*xR+z*zR)/y;
                double xR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR2 = n2 - k*xR2,
                        yR2 = -(x*xR2+z*zR2)/y;
                if ((r > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(z) != 0){
            if (utils.Math.destroyMinusZeros(vector.y - vector.z*y/z) != 0){
                double k = utils.Math.destroyMinusZeros(vector.x - vector.z*x/z)/(vector.y - vector.z*y/z),
                        n2 = n*n*Math.cos(r)/(vector.y - vector.z*y/z);
                double a2 = Math.pow((x-y*k)/z, 2)+k*k+1,
                        b2 = 2*y*n2/(z*z)*(x-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/z, 2)+n2*n2-n*n;
                double xR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR = n2 - k*xR,
                        zR = -(y*yR+x*xR)/z;
                double xR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR2 = n2 - k*xR2,
                        zR2 = -(y*yR2+x*xR2)/z;
                if ((r > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(vector.x - vector.z*x/z) != 0){
                double k = utils.Math.destroyMinusZeros(vector.y - vector.z*y/z)/(vector.x - vector.z*x/z),
                        n2 = n*n*Math.cos(r)/(vector.x - vector.z*x/z);
                double a2 = Math.pow((y-x*k)/z, 2)+k*k+1,
                        b2 = 2*x*n2/(z*z)*(y-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/z, 2)+n2*n2-n*n;
                double yR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR = n2 - k*yR,
                        zR = -(y*yR+x*xR)/z;
                double yR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR2 = n2 - k*yR2,
                        zR2 = -(y*yR2+x*xR2)/z;
                if ((r > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rR = new Vector3D(xR, yR, zR);
                }else{
                    rR = new Vector3D(xR2, yR2, zR2);
                }
            }
        }
        assert rR != null;
        return rR;
    }

    public Vector3D getUpRotatedVectors(double t){
        Vector3D rT = null;
        Pair<Vector3D, Vector3D> basises = getBasises(1, 1);
        Vector3D bW = basises.getKey(),
                bH = basises.getValue();
        double xW = vector.x + bW.x*Math.cos(rotate+Math.PI/2) + bH.x*Math.sin(rotate+Math.PI/2),
                yW = vector.y + bW.y*Math.cos(rotate+Math.PI/2) + bH.y*Math.sin(rotate+Math.PI/2),
                zW = vector.z + bW.z*Math.cos(rotate+Math.PI/2) + bH.z*Math.sin(rotate+Math.PI/2),
                n = Math.sqrt(Math.pow(vector.x, 2)+Math.pow(vector.y, 2)+Math.pow(vector.z, 2));
        double x = vector.y * zW - vector.z*yW,
                y = vector.z * xW - vector.x * zW,
                z = vector.x * yW - vector.y * xW;
        if (utils.Math.destroyMinusZeros(x) != 0){
            if (utils.Math.destroyMinusZeros(vector.y - vector.x*y/x) != 0){
                double k = utils.Math.destroyMinusZeros(vector.z - vector.x*z/x)/(vector.y - vector.x*y/x),
                        n2 = n*n*Math.cos(t)/(vector.y - vector.x*y/x);
                double a2 = Math.pow((z-y*k)/x, 2)+k*k+1,
                        b2 = 2*y*n2/(x*x)*(z-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/x, 2)+n2*n2-n*n;
                double zR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR = n2 - k*zR,
                        xR = -(y*yR+z*zR)/x;
                double zR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR2 = n2 - k*zR2,
                        xR2 = -(y*yR2+z*zR2)/x;
                if ((t > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rT = new Vector3D(xR, yR, zR);
                }else{
                    rT = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(vector.z - vector.x*z/x) != 0){
                double k = utils.Math.destroyMinusZeros(vector.y - vector.x*y/x)/(vector.z - vector.x*z/x),
                        n2 = n*n*Math.cos(t)/(vector.z - vector.x*z/x);
                double a2 = Math.pow((y-z*k)/x, 2)+k*k+1,
                        b2 = 2*z*n2/(x*x)*(y-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/x, 2)+n2*n2-n*n;
                double yR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR = n2 - k*yR,
                        xR = -(y*yR+z*zR)/x;
                double yR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR2 = n2 - k*yR2,
                        xR2 = -(y*yR2+z*zR2)/x;
                if ((t > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rT = new Vector3D(xR, yR, zR);
                }else{
                    rT = new Vector3D(xR2, yR2, zR2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(y) != 0){
            if (utils.Math.destroyMinusZeros(vector.x - vector.y*x/y) != 0){
                double k = utils.Math.destroyMinusZeros(vector.z - vector.y*z/y)/(vector.x - vector.y*x/y),
                        n2 = n*n*Math.cos(t)/(vector.x - vector.y*x/y);
                double a2 = Math.pow((z-x*k)/y, 2)+k*k+1,
                        b2 = 2*x*n2/(y*y)*(z-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/y, 2)+n2*n2-n*n;
                double zR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR = n2 - k*zR,
                        yR = -(x*xR+z*zR)/y;
                double zR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR2 = n2 - k*zR2,
                        yR2 = -(x*xR2+z*zR2)/y;
                if ((t > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rT = new Vector3D(xR, yR, zR);
                }else{
                    rT = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(vector.z - vector.y*z/y) != 0){
                double k = utils.Math.destroyMinusZeros(vector.x - vector.y*x/y)/(vector.z - vector.y*z/y),
                        n2 = n*n*Math.cos(t)/(vector.z - vector.y*z/y);
                double a2 = Math.pow((x-z*k)/y, 2)+k*k+1,
                        b2 = 2*z*n2/(y*y)*(x-z*k)-2*n2*k,
                        c2 = Math.pow(z*n2/y, 2)+n2*n2-n*n;
                double xR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR = n2 - k*xR,
                        yR = -(x*xR+z*zR)/y;
                double xR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        zR2 = n2 - k*xR2,
                        yR2 = -(x*xR2+z*zR2)/y;
                if ((t > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rT = new Vector3D(xR, yR, zR);
                }else{
                    rT = new Vector3D(xR2, yR2, zR2);
                }
            }
        }else if (utils.Math.destroyMinusZeros(z) != 0){
            if (utils.Math.destroyMinusZeros(vector.y - vector.z*y/z) != 0){
                double k = utils.Math.destroyMinusZeros(vector.x - vector.z*x/z)/(vector.y - vector.z*y/z),
                        n2 = n*n*Math.cos(t)/(vector.y - vector.z*y/z);
                double a2 = Math.pow((x-y*k)/z, 2)+k*k+1,
                        b2 = 2*y*n2/(z*z)*(x-y*k)-2*n2*k,
                        c2 = Math.pow(y*n2/z, 2)+n2*n2-n*n;
                double xR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR = n2 - k*xR,
                        zR = -(y*yR+x*xR)/z;
                double xR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        yR2 = n2 - k*xR2,
                        zR2 = -(y*yR2+x*xR2)/z;
                if ((t > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rT = new Vector3D(xR, yR, zR);
                }else{
                    rT = new Vector3D(xR2, yR2, zR2);
                }
            }else if (utils.Math.destroyMinusZeros(vector.x - vector.z*x/z) != 0){
                double k = utils.Math.destroyMinusZeros(vector.y - vector.z*y/z)/(vector.x - vector.z*x/z),
                        n2 = n*n*Math.cos(t)/(vector.x - vector.z*x/z);
                double a2 = Math.pow((y-x*k)/z, 2)+k*k+1,
                        b2 = 2*x*n2/(z*z)*(y-x*k)-2*n2*k,
                        c2 = Math.pow(x*n2/z, 2)+n2*n2-n*n;
                double yR = (-b2 + Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR = n2 - k*yR,
                        zR = -(y*yR+x*xR)/z;
                double yR2 = (-b2 - Math.sqrt(b2*b2-4*a2*c2))/(2*a2),
                        xR2 = n2 - k*yR2,
                        zR2 = -(y*yR2+x*xR2)/z;
                if ((t > 0) == (Math.pow(xR-xW,2)+Math.pow(yR-yW,2)+Math.pow(zR-zW,2) < Math.pow(xR2-xW,2)+Math.pow(yR2-yW,2)+Math.pow(zR2-zW,2))){
                    rT = new Vector3D(xR, yR, zR);
                }else{
                    rT = new Vector3D(xR2, yR2, zR2);
                }
            }
        }
        assert rT != null;
        return rT;
    }

    public Triplet<Vector3D, Vector3D, Vector3D> getMovingBasises(double f, double r, double t){
        Vector3D mR, mF, mT;
        double gamma = 1/Math.sqrt(vector.x*vector.x+vector.y*vector.y);
        double COS = Math.cos(rotate), SIN = Math.sin(rotate);
        mR = new Vector3D(-gamma*vector.y*COS*r, vector.x * gamma*COS*r,  SIN*r);
        mT = new Vector3D(gamma*vector.y*SIN*t, -vector.x * gamma*SIN*t,  COS*t);

        mF = new Vector3D(vector.x*gamma*f, vector.y*gamma*f, 0);

        return new Triplet<>(mF, mR, mT);
    }

    public Point2D project(Point3D point3D){
        double t = (vector.x*vector.x + vector.y*vector.y + vector.z*vector.z)/(vector.x*point3D.x + vector.y*point3D.y + vector.z*point3D.z - vector.x*focus.x - vector.y*focus.y - vector.z*focus.z);
        Point3D projection = new Point3D((point3D.x - focus.x)*t+focus.x, (point3D.y - focus.y)*t+focus.y, (point3D.z - focus.z)*t+focus.z);
        Point3D smm = new Point3D(focus.x+vector.x, focus.y+vector.y,focus.z+vector.z);
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
        double cos = utils.Math.destroyMinusZeros(Math.cos(-rotate)),
                sin = utils.Math.destroyMinusZeros(Math.sin(-rotate));
        return new Point2D(roW*res.width*cos-roH*res.height*sin + res.width/2, -roW*res.width*sin-roH*res.height*cos + res.height/2);
    }

    public Polygon2D project(Polygon3D poly){
        return new Polygon2D(project(poly.a1), project(poly.a2), project(poly.a3), poly.color);
    }
}
