package geometry.objects2D;

public class Point2D {
    public final double x;
    public final double y;

    public Point2D(double x, double y){
        this.x = utils.Math.roundNearZero(x);
        this.y = utils.Math.roundNearZero(y);
    }
}
