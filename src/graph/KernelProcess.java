package graph;

import com.aparapi.Kernel;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;
import javafx.util.Pair;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Set;

public class KernelProcess extends Kernel {

    double[] focus, screenVector, screenPoint, bH, bW, res;
    double[] resultX, resultY, resultZ, x, y, z, x2D, y2D, D, d, polyVectorX, polyVectorY, polyVectorZ;
    double[] bufferX2D, bufferY2D;
    double[] bufferX, bufferY, bufferZ;
    int[] imageData, bounds, bounds2;
    int[] prefix, minX;
    double[] depth;
    int[] colors;
    int count, prefixSumSize, prefixSumStep;
    boolean[] projectFlag;
    BufferedImage image;
    private int mode;

    private static final int CALC = 0, PREPARE = 1, BOUNDS = 2, HALVE = 3, ADD = 4, COPY = 5;

    int maxCount = 1156680;
    KernelProcess(Camera c, BufferedImage image){
        this.count = 0;
        x = new double[maxCount*3];
        y = new double[maxCount*3];
        z = new double[maxCount*3];
        polyVectorX = new double[maxCount];
        polyVectorY = new double[maxCount];
        polyVectorZ = new double[maxCount];
        x2D = new double[maxCount*3];
        y2D = new double[maxCount*3];
        colors = new int[maxCount];
        resultX = new double[maxCount];
        resultY = new double[maxCount];
        resultZ = new double[maxCount];
        bufferX2D = new double[3*maxCount];
        bufferY2D = new double[3*maxCount];
        bufferX = new double[3*maxCount];
        bufferY = new double[3*maxCount];
        bufferZ = new double[3*maxCount];
        prefix = new int[maxCount+1];
        minX = new int[maxCount];
        projectFlag = new boolean[3*maxCount];
        bounds = new int[maxCount];
        bounds2 = new int[maxCount];

        put(resultX);
        put(resultY);
        put(resultZ);
        put(bufferX2D);
        put(bufferY2D);
        put(bufferX);
        put(bufferY);
        put(bufferZ);
        put(projectFlag);

        this.focus = new double[3];
        this.res = new double[2];
        this.bW = new double[3];
        this.bH = new double[3];
        this.screenPoint = new double[3];
        this.screenVector = new double[3];
        depth = new double[(int)c.getResolution().height*(int)c.getResolution().width];
        D = new double[maxCount*3];
        d = new double[maxCount*3];

        setCamera(c, image);
        setExplicit(true);

    }

    public BufferedImage get(){
        if(isExplicit()){
            get(imageData);
        }
        return image;
    }

    public void setDrawables(Set<Polygon3D> drawables){
        int i = 0;
        for(Polygon3D p : drawables) {
            colors[i] = p.color.getRGB();

            Vector3D v = p.getPlane().vector;
            polyVectorX[i] = v.x;
            polyVectorY[i] = v.y;
            polyVectorZ[i] = v.z;

            x[3*i] = p.a1.x;
            y[3*i] = p.a1.y;
            z[3*i] = p.a1.z;

            x[3*i+1] = p.a2.x;
            y[3*i+1] = p.a2.y;
            z[3*i+1] = p.a2.z;

            x[3*i+2] = p.a3.x;
            y[3*i+2] = p.a3.y;
            z[3*i+2] = p.a3.z;

            i++;
        }
        count = drawables.size();
        put(x);
        put(y);
        put(z);
        put(polyVectorX);
        put(polyVectorY);
        put(polyVectorZ);
        put(colors);
    }

    public void setCamera(Camera c, BufferedImage image){
        this.focus[0] = c.getScreen().focus.x;
        this.focus[1] = c.getScreen().focus.y;
        this.focus[2] = c.getScreen().focus.z;


        Pair<Vector3D, Vector3D> basises = c.getBasises(c.getResolution().width / 2, c.getResolution().height / 2);
        Vector3D bW = basises.getKey(),
                bH = basises.getValue();

        this.res[0] = c.getResolution().width;
        this.res[1] = c.getResolution().height;

        this.bW[0] = bW.x;
        this.bW[1] = bW.y;
        this.bW[2] = bW.z;

        this.bH[0] = bH.x;
        this.bH[1] = bH.y;
        this.bH[2] = bH.z;

        this.screenPoint[0] = c.getScreen().point.x;
        this.screenPoint[1] = c.getScreen().point.y;
        this.screenPoint[2] = c.getScreen().point.z;

        this.screenVector[0] = c.getScreen().vector.x;
        this.screenVector[1] = c.getScreen().vector.y;
        this.screenVector[2] = c.getScreen().vector.z;

        Arrays.fill(depth, Integer.MAX_VALUE);
        Arrays.fill(x2D, Integer.MAX_VALUE);
        Arrays.fill(D, -1);
        Arrays.fill(prefix, 0);

        this.image = image;
        imageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        Arrays.fill(imageData, 0);

        put(imageData);
        put(focus);
        put(res);
        put(this.bW);
        put(this.bH);
        put(screenPoint);
        put(screenVector);
        put(depth);
        put(D);
        put(d);
        put(bounds);
        put(bounds2);
        put(minX);
        put(x2D);
        put(y2D);
        put(prefix);

        setExecutionMode(EXECUTION_MODE.CPU);

        if(count != 0) {
            mode = PREPARE;
            execute(3 * count);

            mode = BOUNDS;
            execute(count);

            prefixSumSize = count;
            prefixSumStep = 0;
            mode = ADD;
            execute(count + 1);
            prefixSumStep++;
            while (prefixSumSize > 1) {
                int prefixSumSize2 = (int) ceil(prefixSumSize / 2.0);
                mode = HALVE;
                execute(prefixSumSize2);

                mode = COPY;
                execute(prefixSumSize2);

                mode = ADD;
                execute(count + 1);

                prefixSumSize = prefixSumSize2;
                prefixSumStep++;
            }

        }
        mode = CALC;
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

    public double getValue(double x1, double y1, double x2, double y2, double x3){
        return (y2-y1)*(x3-x1)/(x2-x1)+y1;
    }

    public double getDepth(double Da, double Db, double Dab, double da, double db, double dab, double dac){
        double dc = sqrt(dac*dac+da*da-dac*(dab*dab+da*da-db*db)/dab);
        double X = Da*(da*da+dc*dc-dac*dac)/da/dc;
        double Y = (Dab*Dab+Da*Da-Db*Db)/Dab;
        double a = X*X-Y*Y;
        double b = X*(Y*Y-4*Da*Da);
        double c = (4*Da*Da-Y*Y)*Da*Da;
        double res = Double.NaN;
        if (roundNearZero(a) == 0){
            res = -c/b;
        }else {
            double res1 = (-b - sqrt(b * b - 4 * a * c)) / (2 * a);
            double res2 = (-b + sqrt(b * b - 4 * a * c)) / (2 * a);
            if (!Double.isNaN(res1) && res1 >= min(Da, Db) && res1 <= max(Da, Db)) {        //ToDo: Ограничения не работают
                res = min(Double.isNaN(res) ? Double.MAX_VALUE : res, res1);
            }
            if (!Double.isNaN(res2) && res2 >= min(Da, Db) && res2 <= max(Da, Db)) {        //ToDo: Ограничения не работают
                res = min(Double.isNaN(res) ? Double.MAX_VALUE : res, res2);
            }
        }
        if(Double.isNaN(res))
        {
            double res1 = (-b - sqrt(b * b - 4 * a * c)) / (2 * a);
            double res2 = (-b + sqrt(b * b - 4 * a * c)) / (2 * a);
            int hmm = 1;
//            System.out.println("fuuuuuuuuuuuck");
        }
        return res;
    }

    public static double roundNearZero(double d){
        if(d < 0.0000001 && d > -0.0000001){
            return 0;
        }
        return  d;
    }

    public double getDistance2D(double x1, double y1, double x2, double y2){
        return getDistance3D(0,0,0,bW[0]/(res[0]/2)*(x2-x1)+bH[0]*(y2-y1)/(res[1]/2),bW[1]/(res[0]/2)*(x2-x1)+bH[1]*(y2-y1)/(res[1]/2),bW[2]*(x2-x1)/(res[0]/2)+bH[2]*(y2-y1)/(res[1]/2));
    }

    public double getDistanceByDepth(double x1, double y1, double d1, double x2, double y2, double d2){
        double vector1X = screenVector[0]+(x1/(res[0]/2)-1)*bW[0]+(y1/(res[1]/2)-1)*bH[0],
                vector1Y = screenVector[1]+(x1/(res[0]/2)-1)*bW[1]+(y1/(res[1]/2)-1)*bH[1],
                vector1Z = screenVector[2]+(x1/(res[0]/2)-1)*bW[2]+(y1/(res[1]/2)-1)*bH[2],
                vector1L = sqrt(vector1X*vector1X+vector1Y*vector1Y+vector1Z*vector1Z);
        vector1X /= vector1L;
        vector1Y /= vector1L;
        vector1Z /= vector1L;
        vector1X *= d1;
        vector1Y *= d1;
        vector1Z *= d1;


        double vector2X = screenVector[0]+(x2/(res[0]/2)-1)*bW[0]+(y2/(res[1]/2)-1)*bH[0],
                vector2Y = screenVector[1]+(x2/(res[0]/2)-1)*bW[1]+(y2/(res[1]/2)-1)*bH[1],
                vector2Z = screenVector[2]+(x2/(res[0]/2)-1)*bW[2]+(y2/(res[1]/2)-1)*bH[2],
                vector2L = sqrt(vector2X*vector2X+vector2Y*vector2Y+vector2Z*vector2Z);
        vector2X /= vector2L;
        vector2Y /= vector2L;
        vector2Z /= vector2L;
        vector2X *= d2;
        vector2Y *= d2;
        vector2Z *= d2;

        return getDistance3D(vector1X, vector1Y, vector1Z, vector2X, vector2Y, vector2Z);
    }

    public double getDistance3D(double x1, double y1, double z1, double x2, double y2, double z2){
        return sqrt(pow(x2-x1, 2)+pow(y2-y1, 2)+pow(z2-z1, 2));
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


    public int getPolyIndex(int i){
        int min = 0,
            max = count;
        while (max - min > 1){
            int mid = (max+min)/2;
            if(prefix[mid] > i){
                max = mid;
            }else{
                min = mid;
            }
        }
        return min;
    }


    @Override
    public void run() {
        if(mode == PREPARE)
            prepare(getGlobalId());
        else if(mode == CALC)
            calc(getGlobalId());
        else if(mode == BOUNDS)
            bounds(getGlobalId());
        else if(mode == ADD)
            add(getGlobalId());
        else if(mode == HALVE)
            halve(getGlobalId());
        else if(mode == COPY)
            copy(getGlobalId());
    }

    public void copy(int gid){
        bounds[gid] = bounds2[gid];
    }

    public void halve(int gid) {
        bounds2[gid] = bounds[2 * gid];
        if (prefixSumSize > 2 * gid + 1) {
            bounds2[gid] += bounds[2 * gid + 1];
        }
    }

    public void add(int gid) {
        int mask = 1<<prefixSumStep;
        if((mask&gid) == mask && gid > 0) {
            prefix[gid] += bounds[gid/mask-1];
        }
    }

    public void prepare(int gid){
        projectFlag[gid] = project(gid, x[gid], y[gid], z[gid]);
        x2D[gid] = bufferX2D[gid];
        y2D[gid] = bufferY2D[gid];
        D[gid] = getDistance3D(x[gid], y[gid], z[gid], focus[0], focus[1], focus[2]);
        d[gid] = getDistance3D(bufferX[gid], bufferY[gid], bufferZ[gid], focus[0], focus[1], focus[2]);
    }

    public void bounds(int gid){
        double minX2 = min(x2D[3*gid],min(x2D[3*gid+1],x2D[3*gid+2]));
        double maxX = max(x2D[3*gid],max(x2D[3*gid+1],x2D[3*gid+2]));
        bounds[gid] = (int)(ceil(maxX) - floor(minX2));
        minX[gid] = (int)minX2;
    }

    public void calc(int gid) {
        int poly = getPolyIndex(gid);
        imageData[(int)((res[1]/2+0.5)*res[0])] = 0x0000ff;
        if(poly < count) {
            int i = gid - prefix[poly] + minX[poly];
            double a12Dx = x2D[3 * poly], a12Dy = y2D[3 * poly],
                    a22Dx = x2D[3 * poly+1], a22Dy = y2D[3 * poly+1],
                    a32Dx = x2D[3 * poly+2], a32Dy = y2D[3 * poly+2],
                    a1D = D[3*poly], a2D = D[3*poly+1], a3D = D[3*poly+2],
                    a1d = d[3*poly], a2d = d[3*poly+1], a3d = d[3*poly+2],
                    a1x = x[3*poly], a1y = y[3*poly], a1z = z[3*poly],
                    a2x = x[3*poly+1], a2y = y[3*poly+1], a2z = z[3*poly+1],
                    a3x = x[3*poly+2], a3y = y[3*poly+2], a3z = z[3*poly+2],
                    D12 = getDistance3D(a1x,a1y,a1z, a2x, a2y, a2z),
                    D13 = getDistance3D(a1x,a1y,a1z, a3x, a3y, a3z),
                    D23 = getDistance3D(a3x,a3y,a3z, a2x, a2y, a2z),
                    d12 = getDistance2D(a12Dx, a12Dy, a22Dx, a22Dy),
                    d13 = getDistance2D(a12Dx, a12Dy, a32Dx, a32Dy),
                    d23 = getDistance2D(a32Dx, a32Dy, a22Dx, a22Dy);

            if (a1D == -1 || a2D == -1 || a3D == -1)
                return;

            double maxPointY = 0, maxPointD = -1, maxPointd = -1, minPointY = 0, minPointD = -1, minPointd = -1;
            if (i >= min(a22Dx, a12Dx) && i <= max(a22Dx, a12Dx)) {
                double y = getValue(a12Dx, a12Dy, a22Dx, a22Dy, i),
                        d1c = getDistance2D(a12Dx, a12Dy, i, y),
                        dis = getDistance2D(a12Dx, a12Dy, a22Dx, a22Dy),
                        D = getDepth(a1D, a2D, D12, a1d, a2d, d12, d1c),
                        d = getDistance3D(0,0,0,screenVector[0]+bW[0]*(i/(res[0]/2)-1)+bH[0]*(y/(res[1]/2)-1),screenVector[1]+bW[1]*(i/(res[0]/2)-1)+bH[1]*(y/(res[1]/2)-1),screenVector[2]+bW[2]*(i/(res[0]/2)-1)+bH[2]*(y/(res[1]/2)-1));
                if (dis != 0 && !Double.isNaN(D) && !Double.isNaN(d) && (maxPointD == -1 || y > maxPointY || maxPointd == -1)) {
                    maxPointY = y;
                    maxPointD = D;
                    maxPointd = d;
                }
                if (dis != 0 && !Double.isNaN(D) && !Double.isNaN(d) && (minPointD == -1 || y < minPointY || minPointd == -1)) {
                    minPointY = y;
                    minPointD = D;
                    minPointd = d;
                }
            }
            if (i >= min(a22Dx, a32Dx) && i <= max(a22Dx, a32Dx)) {
                double y = getValue(a22Dx, a22Dy, a32Dx, a32Dy, i),
                        d2c = getDistance2D(a22Dx, a22Dy, i, y),
                        dis = getDistance2D(a22Dx, a22Dy, a32Dx, a32Dy),
                        D = getDepth(a2D, a3D, D23, a2d, a3d, d23, d2c),
                        d = getDistance3D(0,0,0,screenVector[0]+bW[0]*(i/(res[0]/2)-1)+bH[0]*(y/(res[1]/2)-1),screenVector[1]+bW[1]*(i/(res[0]/2)-1)+bH[1]*(y/(res[1]/2)-1),screenVector[2]+bW[2]*(i/(res[0]/2)-1)+bH[2]*(y/(res[1]/2)-1));
                if (dis != 0 && !Double.isNaN(D) && !Double.isNaN(d) && (maxPointD == -1 || y > maxPointY || maxPointd == -1)) {
                    maxPointY = y;
                    maxPointD = D;
                    maxPointd = d;
                }
                if (dis != 0 && !Double.isNaN(D) && !Double.isNaN(d) && (minPointD == -1 || y < minPointY || minPointd == -1)) {
                    minPointY = y;
                    minPointD = D;
                    minPointd = d;
                }
            }
            if (i >= min(a32Dx, a12Dx) && i <= max(a32Dx, a12Dx)) {
                double y = getValue(a12Dx, a12Dy, a32Dx, a32Dy, i),
                        d1c = getDistance2D(a12Dx, a12Dy, i, y),
                        dis = getDistance2D(a12Dx, a12Dy, a32Dx, a32Dy),
                        D = getDepth(a1D, a3D, D13, a1d, a3d, d13, d1c),
                        d = getDistance3D(0,0,0,screenVector[0]+bW[0]*(i/(res[0]/2)-1)+bH[0]*(y/(res[1]/2)-1),screenVector[1]+bW[1]*(i/(res[0]/2)-1)+bH[1]*(y/(res[1]/2)-1),screenVector[2]+bW[2]*(i/(res[0]/2)-1)+bH[2]*(y/(res[1]/2)-1));
                if (dis != 0 && !Double.isNaN(D) && !Double.isNaN(d) && (maxPointD == -1 || y > maxPointY || maxPointd == -1)) {
                    maxPointY = y;
                    maxPointD = D;
                    maxPointd = d;
                }
                if (dis != 0 && !Double.isNaN(D) && !Double.isNaN(d) && (minPointD == -1 || y < minPointY || minPointd == -1)) {
                    minPointY = y;
                    minPointD = D;
                    minPointd = d;
                }

            }

            if (maxPointD != -1 && minPointD != -1) {
                int minBound = (int) floor(max(0, min(res[1], minPointY))),
                        maxBound = (int) ceil(max(0, min(res[1], maxPointY)));
                for (int j = minBound; j <= maxBound; j++) {
                    double dminmax = getDistance2D(i,minPointY, i, maxPointY),
                            dminc = getDistance2D(i,minPointY, i, j),
                            Dminmax = getDistanceByDepth(i, minPointY, minPointD, i, maxPointY, maxPointD);
                    double d = getDepth(minPointD, maxPointD, Dminmax, minPointd, maxPointd, dminmax, dminc);
                    if(Double.isNaN(d)){
                        int hmm = 1;
                    }
                    int index = j * (int) res[0] + i;
                    if (i >= 0 && i < res[0] && j >= 0 && j < res[1] && d < depth[index]) {
                        imageData[index] = colors[poly];
                        depth[index] = d;
                    }
                }
            }
            else{
                int hmm = 1;
            }
        }
    }
}
