package graph;

import com.aparapi.Kernel;
import geometry.objects3D.Vector3D;
import javafx.util.Pair;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class KernelProcess extends Kernel {

    double[] focus, screenVector, screenPoint, bH, bW, res;
    double[] resultX, resultY, resultZ, x, y, z;
    double[] bufferX2D, bufferY2D;
    double[] bufferX, bufferY, bufferZ;
    int[] imageData;
    double[] depth;
    int[] colors;
    int count;

    KernelProcess(Camera c, int count, BufferedImage image){
        this.count = count;
        imageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        setCamera(c);
        x = new double[count*3];
        y = new double[count*3];
        z = new double[count*3];
        colors = new int[count];
        resultX = new double[count];
        resultY = new double[count];
        resultZ = new double[count];
        bufferX2D = new double[count];
        bufferY2D = new double[count];
        bufferX = new double[count];
        bufferY = new double[count];
        bufferZ = new double[count];

        put(x);
        put(y);
        put(z);
        put(colors);
        put(resultX);
        put(resultY);
        put(resultZ);
        put(bufferX2D);
        put(bufferY2D);
        put(bufferX);
        put(bufferY);
        put(bufferZ);


        setExplicit(true);

    }

    public void get(){
        if(isExplicit()){
            get(imageData);
        }
    }

    public void setCamera(Camera c){
        this.focus = new double[]{c.getScreen().focus.x, c.getScreen().focus.y, c.getScreen().focus.z};
        Pair<Vector3D, Vector3D> basises = c.getBasises(c.getResolution().width / 2, c.getResolution().height / 2);
        Vector3D bW = basises.getKey(),
                bH = basises.getValue();
        this.res = new double[]{c.getResolution().width, c.getResolution().height};
        this.bW = new double[]{bW.x, bW.y, bW.z};
        this.bH = new double[]{bH.x, bH.y, bH.z};
        this.screenPoint = new double[]{c.getScreen().point.x, c.getScreen().point.y,c.getScreen().point.z};
        this.screenVector = new double[]{c.getScreen().vector.x, c.getScreen().vector.y, c.getScreen().vector.z};
        depth = new double[(int)c.getResolution().height*(int)c.getResolution().width];

        Arrays.fill(imageData, 0);

        put(imageData);
        put(focus);
        put(res);
        put(this.bW);
        put(this.bH);
        put(screenPoint);
        put(screenVector);
        put(depth);
    }
    
    public double min(double a, double b){
        if(a<b)
            return a;
        return b;
    }

    public double max(double a, double b){
        if(a>b)
            return a;
        return b;
    }
    
    public boolean inRegion(double r1x, double r1y, double r1z, double r2x, double r2y, double r2z, double x, double y, double z){
        double lx = min(r1x, r2x);
        double ly = min(r1y, r2y);
        double lz = min(r1z, r2z);
        double hx = max(r1x, r2x);
        double hy = max(r1y, r2y);
        double hz = max(r1z, r2z);

        return lx <= x && ly <= y && lz <= z && hx >= x && hy >= y && hz >= z;
        
    }

    public boolean getIntersection(int gid, double x1, double y1, double z1, double x2, double y2, double z2){

        double vectorX = x2-x1,
                vectorY = y2-y1,
                vectorZ = z2-z1;
        double sp = vectorX*screenVector[0]+vectorY*screenVector[1]+vectorZ*screenVector[2];
        if (sp != 0) {
            double d = -(screenVector[0] * screenPoint[0] + screenVector[1] * screenPoint[1] + screenVector[2] * screenPoint[2]);
            double t = -(d + x1 * screenVector[0] + y1 * screenVector[1] + z1 * screenVector[2]) / sp;
            bufferX[gid] = vectorX*t+x1;
            bufferY[gid] = vectorY*t+y1;
            bufferZ[gid] = vectorZ*t+z1;
            return true;
        }
        return false;
    }

    public boolean project(int gid, double px, double py, double pz){
        if((focus[0] != px || focus[1] != py || focus[2] != pz) && getIntersection(gid, focus[0], focus[1], focus[2], px, py,pz) && !inRegion(px,py,pz, bufferX[gid], bufferY[gid], bufferZ[gid], focus[0], focus[1], focus[2])) {
            double projectionX = bufferX[gid],
                    projectionY = bufferY[gid],
                    projectionZ = bufferZ[gid];
            double smmX = screenPoint[0];
            double smmY = screenPoint[1];
            double smmZ = screenPoint[2];
            double roW = 0, roH = 0;
            boolean roWNotNull = false, roHNotNull = false;
            if (bH[1] != 0 && bH[2] != 0 && bW[1] != 0 && bW[2] != 0) {
                roW = (bH[2] * (projectionY - smmY) - bH[1] * (projectionZ - smmZ)) / (bH[2] * bW[1] - bH[1] * bW[2]);
                roH = (projectionZ - smmZ - bW[2] * roW) / bH[2];
                roHNotNull = true;
                roWNotNull = true;
            } else if (bH[0] != 0 && bH[2] != 0 && bW[0] != 0 && bW[2] != 0) {
                roW = (bH[2] * (projectionX - smmX) - bH[0] * (projectionZ - smmZ)) / (bH[2] * bW[0] - bH[0] * bW[2]);
                roH = (projectionX - smmX - bW[0] * roW) / bH[0];
                roHNotNull = true;
                roWNotNull = true;
            } else if (bH[0] != 0 && bH[1] != 0 && bW[0] != 0 && bW[1] != 0) {
                roW = (bH[1] * (projectionX - smmX) - bH[0] * (projectionY - smmY)) / (bH[1] * bW[0] - bH[0] * bW[1]);
                roH = (projectionX - smmX - bW[0] * roW) / bH[0];
                roHNotNull = true;
                roWNotNull = true;
            } else {
                if (bH[0] == 0 && bW[0] != 0) {
                    roW = (projectionX - smmX) / bW[0];
                    roWNotNull = true;
                } else if (bH[1] == 0 && bW[1] != 0) {
                    roW = (projectionY - smmY) / bW[1];
                    roWNotNull = true;
                } else if (bH[2] == 0 && bW[2] != 0) {
                    roW = (projectionZ - smmZ) / bW[2];
                    roWNotNull = true;
                }

                if (bH[1] != 0 && bW[1] == 0) {
                    roH = (projectionY - smmY) / bH[1];
                    roHNotNull = true;
                } else if (bH[0] != 0 && bW[0] == 0) {
                    roH = (projectionX - smmX) / bH[0];
                    roHNotNull = true;
                } else if (bH[2] != 0 && bW[2] == 0) {
                    roH = (projectionZ - smmZ) / bH[2];
                    roHNotNull = true;
                }

                if (!roWNotNull && roHNotNull) {
                    if (bW[0] != 0) {
                        roW = (projectionX - smmX - bH[0] * roH) / bW[0];
                        roWNotNull = true;
                    } else if (bW[1] != 0) {
                        roW = (projectionY - smmY - bH[1] * roH) / bW[1];
                        roWNotNull = true;
                    } else if (bW[2] != 0) {
                        roW = (projectionZ - smmZ - bH[2] * roH) / bW[2];
                        roWNotNull = true;
                    }
                }

                if (!roHNotNull && roWNotNull) {
                    if (bH[0] != 0) {
                        roH = (projectionX - smmX - bW[0] * roW) / bH[0];
                        roHNotNull = true;
                    } else if (bH[1] != 0) {
                        roH = (projectionY - smmY - bW[1] * roW) / bH[1];
                        roHNotNull = true;
                    } else if (bH[2] != 0) {
                        roH = (projectionZ - smmZ - bW[2] * roW) / bH[2];
                        roHNotNull = true;
                    }
                }
            }
            if(roHNotNull && roWNotNull){
                bufferX2D[gid] = roW * res[0] + res[0] / 2;
                bufferY2D[gid] = -roH * res[1] + res[1] / 2;
                return true;
            }
        }
        return false;
    }
//
//    public void draw() {
//        Optional<Point2D> a12D = project(a1),
//                a22D = project(a2),
//                a32D = project(a3);
//        if (!a12D.isPresent() || !a22D.isPresent() || !a32D.isPresent() || !new Polygon2D(a12D.get(), a22D.get(), a32D.get(), color).getRegion().crosses(new Region2D(new Point2D(0, 0), new Point2D(camera.getResolution().width, camera.getResolution().height))))
//            return;
//
////        cp.set((int) a12D.get().x, (int) a12D.get().y, new Pixel(1, color));
////        cp.set((int) a22D.get().x, (int) a22D.get().y, new Pixel(1, color));
////        cp.set((int) a32D.get().x, (int) a32D.get().y, new Pixel(1, color));
////        new Polygon2D(a12D.get(), a22D.get(), a32D.get(), color).draw(cp, camera);
//        Vector2D v232D = new Vector2D(a22D.get(), a32D.get());
//        Vector3D v23 = new Vector3D(a2, a3);
//        for (double j = 0; j <= v232D.getLength(); j += 0.5) {
//            Point3D p = v23.multiply(j / v232D.getLength()).addToPoint(a2);
//            Vector3D v1p = new Vector3D(a1, p);
//            Point2D p2D = v232D.multiply(j / v232D.getLength()).addToPoint(a22D.get());
//            Vector2D v1p2D = new Vector2D(a12D.get(), p2D);
//            for (double i = 0; i <= v1p2D.getLength(); i += 0.5) {
//                Point3D p2 = v1p.multiply(i / v1p2D.getLength()).addToPoint(a1);
//                Point2D p22D = v1p2D.multiply(i / v1p2D.getLength()).addToPoint(a12D.get());
//                cp.set((int) p22D.x, (int) p22D.y, new Pixel(new Vector3D(camera.getScreen().focus, p2).getLength(), color));
//            }
//        }
//    }

    @Override
    public void run() {
        int gid = getGlobalId();
        double a1x = 0, a1y = 0, a1z = 0, a12Dx = 0, a12Dy = 0,
                a2x = 0, a2y = 0, a2z = 0, a22Dx = 0, a22Dy = 0,
                a3x = 0, a3y = 0, a3z = 0, a32Dx = 0, a32Dy = 0;
        if(project(gid, x[gid*3], y[gid*3], z[gid*3])) {
            a1x = bufferX[gid];
            a1y = bufferY[gid];
            a1z = bufferZ[gid];
            a12Dx = bufferX2D[gid];
            a12Dy = bufferY2D[gid];
        }else return;
        if(project(gid, x[gid*3+1], y[gid*3+1], z[gid*3+1])) {
            a2x = bufferX[gid];
            a2y = bufferY[gid];
            a2z = bufferZ[gid];
            a22Dx = bufferX2D[gid];
            a22Dy = bufferY2D[gid];
        }else return;
        if(project(gid, x[gid*3+2], y[gid*3+2], z[gid*3+2])) {
            a3x = bufferX[gid];
            a3y = bufferY[gid];
            a3z = bufferZ[gid];
            a32Dx = bufferX2D[gid];
            a32Dy = bufferY2D[gid];
        }else return;


        double v232Dx = a32Dx - a22Dx,
                v232Dy = a32Dy - a22Dy,
                v232DLen = Math.sqrt(v232Dx*v232Dx + v232Dy*v232Dy);
        double v23x = a3x - a2x,
                v23y = a3y - a2y,
                v23z = a3z - a2z;
//                v23Len = Math.sqrt(v23x*v23x + v23y*v23y + v23z*v23z);
        for (double j = 0; j <= v232DLen; j += 1) {
            double px = v23x*(j/v232DLen)+a2x,
                    py = v23y*(j/v232DLen)+a2y,
                    pz = v23z*(j/v232DLen)+a2z;
            double v1px = px - a1x,
                    v1py = py - a1y,
                    v1pz = pz - a1z;

            double p2Dx = v232Dx*(j/v232DLen)+a22Dx,
                    p2Dy = v232Dy*(j/v232DLen)+a22Dy;
            double v1p2Dx = p2Dx - a12Dx,
                    v1p2Dy = p2Dy - a12Dy,
                    v1p2DLen = Math.sqrt(v1p2Dx*v1p2Dx + v1p2Dy*v1p2Dy);
            for (double i = 0; i <= v1p2DLen; i += 1) {
                double p2x = v1px*(i/v1p2DLen)+a1x,
                        p2y = v1py*(i/v1p2DLen)+a1y,
                        p2z = v1pz*(i/v1p2DLen)+a1z;
                double p22Dx = v1p2Dx*(i/v1p2DLen)+a12Dx,
                        p22Dy = v1p2Dy*(i/v1p2DLen)+a12Dy;
                int index = (int) res[0] * (int) p22Dy + (int) p22Dx;
                double p2fx = p2x - focus[0],
                        p2fy = p2y - focus[1],
                        p2fz = p2z - focus[2];
                double d = Math.sqrt(p2fx*p2fx+p2fy*p2fy+p2fz*p2fz);
                if(p22Dx >= 0 && p22Dx < res[0] && p22Dy >= 0 && p22Dy < res[1]) {
                    imageData[index] = colors[gid];
                    depth[index] = d;
                }
            }
        }
    }

//    @Override
//    public void run() {
//        int gid = getGlobalId();
//        int r = colors[gid*3],
//                g = colors[gid*3+1],
//                b = colors[gid*3+2];
//        if(project(gid, x[gid*3], y[gid*3], z[gid*3]) && bufferX2D[gid] >= 0 && bufferX2D[gid] < res[0] && bufferY2D[gid] >= 0 && bufferY2D[gid] <= res[1]) {
//            int i = (int)bufferY2D[gid]*(int)res[0]+(int)bufferX2D[gid];
//            imageData[i]=r*256*256+g*256+b;
////            result[3*i+1]=g;
////            result[3*i+2]=b;
//        }
//        if(project(gid, x[gid*3+1], y[gid*3+1], z[gid*3+1]) && bufferX2D[gid] >= 0 && bufferX2D[gid] < res[0] && bufferY2D[gid] >= 0 && bufferY2D[gid] <= res[1]) {
//            int i = (int)bufferY2D[gid]*(int)res[0]+(int)bufferX2D[gid];
//            imageData[i]=r*256*256+g*256+b;
//        }
//        if(project(gid, x[gid*3+2], y[gid*3+2], z[gid*3+2]) && bufferX2D[gid] >= 0 && bufferX2D[gid] < res[0] && bufferY2D[gid] >= 0 && bufferY2D[gid] <= res[1]) {
//            int i = (int)bufferY2D[gid]*(int)res[0]+(int)bufferX2D[gid];
//            imageData[i]=r*256*256+g*256+b;
//        }
//    }
}
