package graph;

import com.aparapi.Kernel;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Collection;
import utils.Pair;

public class KernelProcess extends Kernel {

    double[] focus, screenVector, screenPoint, bH, bW, res;
    double[] x, y, z, x2D, y2D;
    double[] bufferX, bufferY, bufferZ;
    double[][] matrix;
    int[] imageData, bounds, bounds2;
    int[] prefix, minX;
    double[] depth;
    int[] colors;
    int count, prefixSumSize, prefixSumStep;
    boolean[] projectFlag;
    BufferedImage image;
    private int mode;

    private static final int CALC = 0, PREPARE = 1, BOUNDS = 2, HALVE = 3, ADD = 4, COPY = 5, CHANGE_COORDS = 6;

    public static final int MAX_COUNT = 1156680;

    KernelProcess(Camera c, BufferedImage image, EXECUTION_MODE em) {
        this.count = 0;
        setExecutionMode(em);
        x = new double[MAX_COUNT * 3];
        y = new double[MAX_COUNT * 3];
        z = new double[MAX_COUNT * 3];
        x2D = new double[MAX_COUNT * 3];
        y2D = new double[MAX_COUNT * 3];
        colors = new int[MAX_COUNT];
        bufferX = new double[3 * MAX_COUNT];
        bufferY = new double[3 * MAX_COUNT];
        bufferZ = new double[3 * MAX_COUNT];
        prefix = new int[MAX_COUNT + 1];
        minX = new int[MAX_COUNT];
        projectFlag = new boolean[3 * MAX_COUNT];
        bounds = new int[MAX_COUNT];
        bounds2 = new int[MAX_COUNT];
        matrix = new double[3][3];

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
        depth = new double[(int) c.getResolution().height * (int) c.getResolution().width];

        setExplicit(true);
        setCamera(c, image);

    }

    public BufferedImage get() {
        if (isExplicit()) {
            get(imageData);
        }
        return image;
    }

    public void setPolygons(Collection<Polygon3D> drawables) {
        int i = 0;
        for (Polygon3D p : drawables) {
            colors[i] = p.color.getRGB();
            x[3 * i] = p.a1.x;
            y[3 * i] = p.a1.y;
            z[3 * i] = p.a1.z;

            x[3 * i + 1] = p.a2.x;
            y[3 * i + 1] = p.a2.y;
            z[3 * i + 1] = p.a2.z;

            x[3 * i + 2] = p.a3.x;
            y[3 * i + 2] = p.a3.y;
            z[3 * i + 2] = p.a3.z;

            i++;
        }
        count = drawables.size();
        put(x);
        put(y);
        put(z);
        put(colors);
    }

    public void setCamera(Camera c, BufferedImage image) {
        this.focus[0] = c.getScreen().focus.x;
        this.focus[1] = c.getScreen().focus.y;
        this.focus[2] = c.getScreen().focus.z;


        Pair<Vector3D, Vector3D> basises = c.getBasises(1, 1);
        Vector3D bW = basises.first,
                bH = basises.second;

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
        put(bounds);
        put(bounds2);
        put(minX);
        put(x2D);
        put(y2D);
        put(prefix);


        double det = this.bW[0] * this.bH[1] * screenVector[2]
                + this.bW[1] * this.bH[2] * screenVector[0]
                + this.bW[2] * this.bH[0] * screenVector[1]
                - this.bW[2] * this.bH[1] * screenVector[0]
                - this.bW[1] * this.bH[0] * screenVector[2]
                - this.bW[0] * this.bH[2] * screenVector[1];

        matrix[0][0] = (this.bH[1] * screenVector[2] - this.bH[2] * screenVector[1]) / det;
        matrix[1][0] = -(this.bW[1] * screenVector[2] - this.bW[2] * screenVector[1]) / det;
        matrix[2][0] = (this.bW[1] * this.bH[2] - this.bW[2] * this.bH[1]) / det;
        matrix[0][1] = -(this.bH[0] * screenVector[2] - this.bH[2] * screenVector[0]) / det;
        matrix[1][1] = (this.bW[0] * screenVector[2] - this.bW[2] * screenVector[0]) / det;
        matrix[2][1] = -(this.bW[0] * this.bH[2] - this.bW[2] * this.bH[0]) / det;
        matrix[0][2] = (this.bH[0] * screenVector[1] - this.bH[1] * screenVector[0]) / det;
        matrix[1][2] = -(this.bW[0] * screenVector[1] - this.bW[1] * screenVector[0]) / det;
        matrix[2][2] = (this.bW[0] * this.bH[1] - this.bW[1] * this.bH[0]) / det;

        put(matrix);

        if (count != 0) {
            mode = CHANGE_COORDS;
            execute(3 * count);

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
    }

    public BufferedImage draw() {
        mode = CALC;
        get(prefix);
        if (count != 0 && prefix[count] != 0) {
            execute(prefix[count]);
        }
        return get();
    }

    public double getValue(double x1, double y1, double x2, double y2, double x3) {
        return (y2 - y1) * (x3 - x1) / (x2 - x1) + y1;
    }

    public double roundNearZero(double d) {
        if (d < 0.0000001 && d > -0.0000001) {
            return 0;
        }
        return d;
    }

    public double getDistance2D(double x1, double y1, double x2, double y2) {
        return sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2));
    }

    public double getDistance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        return sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2) + pow(z2 - z1, 2));
    }

    public boolean getIntersection(int gid,
                                   double x1, double y1, double z1,
                                   double k1x, double k1y, double k1z,
                                   double x2, double y2, double z2,
                                   double k2x, double k2y, double k2z) {
        double t2 = 0;
        if (k1y * k2x - k1x * k2y != 0) {
            t2 = (k1x * (y2 - y1) - k1y * (x2 - x1)) / (k1y * k2x - k1x * k2y);
        } else if (k1z * k2x - k1x * k2z != 0) {
            t2 = (k1x * (z2 - z1) - k1z * (x2 - x1)) / (k1z * k2x - k1x * k2z);
        } else if (k1y * k2z - k1z * k2y != 0) {
            t2 = (k1z * (y2 - y1) - k1y * (z2 - z1)) / (k1y * k2z - k1z * k2y);
        } else {
            return false;
        }
        bufferX[gid] = x2 + t2 * k2x;
        bufferY[gid] = y2 + t2 * k2y;
        bufferZ[gid] = z2 + t2 * k2z;
        return true;
    }

    public void changeCoord(int i) {
        double oldX = x[i] - focus[0],
                oldY = y[i] - focus[1],
                oldZ = z[i] - focus[2];
        double newX = oldX * matrix[0][0] + oldY * matrix[0][1] + oldZ * matrix[0][2],
                newY = oldX * matrix[1][0] + oldY * matrix[1][1] + oldZ * matrix[1][2],
                newZ = oldX * matrix[2][0] + oldY * matrix[2][1] + oldZ * matrix[2][2];
        x[i] = newX;
        y[i] = newY;
        z[i] = newZ;
    }

    public int getPolyIndex(int i) {
        int min = 0,
                max = count;
        while (max - min > 1) {
            int mid = (max + min) / 2;
            if (prefix[mid] > i) {
                max = mid;
            } else {
                min = mid;
            }
        }
        return min;
    }


    @Override
    public void run() {
        if (mode == CHANGE_COORDS)
            changeCoord(getGlobalId());
        else if (mode == PREPARE)
            prepare(getGlobalId());
        else if (mode == BOUNDS)
            bounds(getGlobalId());
        else if (mode == ADD)
            add(getGlobalId());
        else if (mode == HALVE)
            halve(getGlobalId());
        else if (mode == COPY)
            copy(getGlobalId());
        else if (mode == CALC)
            calc(getGlobalId());
    }

    public void copy(int gid) {
        bounds[gid] = bounds2[gid];
    }

    public void halve(int gid) {
        if (prefixSumSize > 2 * gid + 1) {
            bounds2[gid] = bounds[2 * gid] + bounds[2 * gid + 1];
        } else {
            bounds2[gid] = bounds[2 * gid];
        }
    }

    public void add(int gid) {
        int mask = 1 << prefixSumStep;
        if ((mask & gid) == mask && gid > 0) {
            prefix[gid] += bounds[gid / mask - 1];
        }
    }

    public void prepare(int gid) {
        if (x[gid] != 0 && y[gid] != 0 && roundNearZero(z[gid]) > 0) {
            x2D[gid] = x[gid] / z[gid];
            y2D[gid] = y[gid] / z[gid];
            projectFlag[gid] = true;
        }else{
            projectFlag[gid] = false;
        }
    }

    public void bounds(int gid) {
        double minX2 = min(x2D[3 * gid], min(x2D[3 * gid + 1], x2D[3 * gid + 2]));
        double maxX = max(x2D[3 * gid], max(x2D[3 * gid + 1], x2D[3 * gid + 2]));
        maxX = max(-res[0] / 2, min(res[0] / 2, maxX));
        minX2 = max(-res[0] / 2, min(res[0] / 2, minX2));
        bounds[gid] = (int) (ceil(maxX) - floor(minX2));
        minX[gid] = (int) minX2;
    }

    public double getDepth(double Xa, double Ya, double Za, double Xb, double Yb, double Zb, double Dab, double dab, double dac) {
        double Dac = Dab * dac * Za / ((dab - dac) * Zb + dac * Za);
        return getDistance3D(0, 0, 0, Xa + (Xb - Xa) * Dac / Dab, Ya + (Yb - Ya) * Dac / Dab, Za + (Zb - Za) * Dac / Dab);
    }

    public void calc(int gid) {
        int poly = getPolyIndex(gid);
        if (poly < count && projectFlag[3 * poly] && projectFlag[3 * poly + 1] && projectFlag[3 * poly + 2]) {
            int i = gid - prefix[poly] + minX[poly];
            double a12Dx = x2D[3 * poly], a12Dy = y2D[3 * poly],
                    a22Dx = x2D[3 * poly + 1], a22Dy = y2D[3 * poly + 1],
                    a32Dx = x2D[3 * poly + 2], a32Dy = y2D[3 * poly + 2],
                    a1x = x[3 * poly], a1y = y[3 * poly], a1z = z[3 * poly],
                    a2x = x[3 * poly + 1], a2y = y[3 * poly + 1], a2z = z[3 * poly + 1],
                    a3x = x[3 * poly + 2], a3y = y[3 * poly + 2], a3z = z[3 * poly + 2];

            double maxPointY = 0, minPointY = 0, maxPointZ = -1, minPointZ = -1, maxPointX = -1, minPointX = -1;
            if (i >= min(a22Dx, a12Dx) && i <= max(a22Dx, a12Dx)) {
                double y = getValue(a12Dx, a12Dy, a22Dx, a22Dy, i);
                boolean flag = getIntersection(gid, 0, 0, 0, i, y, 1, a1x, a1y, a1z, a2x - a1x, a2y - a1y, a2z - a1z);
                if (flag) {
                    maxPointX = bufferX[gid];
                    maxPointY = bufferY[gid];
                    maxPointZ = bufferZ[gid];
                }
                if (flag) {
                    minPointX = bufferX[gid];
                    minPointY = bufferY[gid];
                    minPointZ = bufferZ[gid];
                }
            }
            if (i >= min(a22Dx, a32Dx) && i <= max(a22Dx, a32Dx)) {
                double y = getValue(a22Dx, a22Dy, a32Dx, a32Dy, i);
                boolean flag = getIntersection(gid, 0, 0, 0, i, y, 1, a3x, a3y, a3z, a2x - a3x, a2y - a3y, a2z - a3z);
                if (flag && (maxPointZ == -1 || y > maxPointY / maxPointZ)) {
                    maxPointX = bufferX[gid];
                    maxPointY = bufferY[gid];
                    maxPointZ = bufferZ[gid];
                }
                if (flag && (minPointZ == -1 || y < minPointY / minPointZ)) {
                    minPointX = bufferX[gid];
                    minPointY = bufferY[gid];
                    minPointZ = bufferZ[gid];
                }
            }
            if (i >= min(a32Dx, a12Dx) && i <= max(a32Dx, a12Dx)) {
                double y = getValue(a12Dx, a12Dy, a32Dx, a32Dy, i);
                boolean flag = getIntersection(gid, 0, 0, 0, i, y, 1, a1x, a1y, a1z, a3x - a1x, a3y - a1y, a3z - a1z);
                if (flag && (maxPointZ == -1 || y > maxPointY / maxPointZ)) {
                    maxPointX = bufferX[gid];
                    maxPointY = bufferY[gid];
                    maxPointZ = bufferZ[gid];
                }
                if (flag && (minPointZ == -1 || y < minPointY / minPointZ)) {
                    minPointX = bufferX[gid];
                    minPointY = bufferY[gid];
                    minPointZ = bufferZ[gid];
                }
            }

            int minBound = (int) floor(max(-res[1] / 2, min(res[1] / 2, minPointY / minPointZ))),
                    maxBound = (int) ceil(max(-res[1] / 2, min(res[1] / 2, maxPointY / maxPointZ)));
            double Dab = getDistance3D(minPointX, minPointY, minPointZ, maxPointX, maxPointY, maxPointZ),
                    dab = getDistance2D(minPointX / minPointZ, minPointY / minPointZ, maxPointX / maxPointZ, maxPointY / maxPointZ);
            if (minPointZ != -1 && maxPointZ != -1) {
                for (int j = minBound; j <= maxBound; j++) {
                    int index = (int) ((res[1] / 2 - j) * res[0]) + i + (int) res[0] / 2;
                    double d = getDepth(minPointX, minPointY, minPointZ, maxPointX, maxPointY, maxPointZ, Dab, dab, getDistance2D(i, j, minPointX / minPointZ, minPointY / minPointZ));
                    if (i >= -res[0] / 2 && i < res[0] / 2 && j >= -res[1] / 2 && j < res[1] / 2 && index >= 0 && index < depth.length && depth[index] > d) {
                        imageData[index] = colors[poly];
                        depth[index] = d;
                    }
                }
            }
        }
    }
}
