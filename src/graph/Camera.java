package graph;

import geometry.objects3D.Vector3D;
import utils.Pair;
import utils.Triplet;

public class Camera {

    public static class Resolution {
        public final double height, width;

        public Resolution(double width, double height) {
            this.height = height;
            this.width = width;
        }
    }

    private Screen screen;
    private Resolution res;

    public Camera(Screen screen, Resolution resolution) {
        this.screen = screen;
        this.res = resolution;
    }

    /**
     * @param w Длина горизонтального вектора
     * @param h Длина вертикального вектора
     * @return Пара векторов, соответсвующих осям в состеме координат экрана
     */
    public Pair<Vector3D, Vector3D> getBasises(double w, double h) {
        Vector3D bW = new Vector3D(screen.vector.y,-screen.vector.x,0);
        Vector3D bH = screen.vector.vectorProduct(bW);

        return new Pair<>(bW.normalize().multiply(w), bH.normalize().multiply(-h));
    }

    /**
     * @param r Угол поворота вправо
     * @param t Угол поворота вверх
     * @return Повернутый вектор нормали экрана
     */
    public Vector3D getRotatedVector(double r, double t) {
        Pair<Vector3D, Vector3D> basises = getBasises(t, r);
        Vector3D bH = basises.second,
                bW = basises.first;
        Vector3D res = screen.vector;
        if(r != 0){
            res = res.rotate(bH);
        }
        if(t != 0){
            res = res.rotate(bW);
        }
        return res;
    }

    /**
     * @param f Длина вектора направления вперед
     * @param r Длина вектора направления вправо
     * @param t Длина вектора направления вверх
     * @return Тройка векторов направления движения
     */
    public Triplet<Vector3D, Vector3D, Vector3D> getMovingBasises(double f, double r, double t) {
        Vector3D mR, mF, mT;
        Pair<Vector3D, Vector3D> basises = getBasises(r, t);
        mR = basises.first;
        mT = basises.second;

        double n = screen.vector.getLength();
        mF = screen.vector.multiply(f / n);

        return new Triplet<>(mF, mR, mT);
    }

    public Resolution getResolution() {
        return res;
    }

    public void setResolution(Resolution res) {
        synchronized (this) {
            this.res = res;
        }
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        synchronized (this) {
            this.screen = screen;
        }
    }
}
