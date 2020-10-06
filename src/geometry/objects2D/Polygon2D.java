package geometry.objects2D;

import graph.Camera;

import java.awt.*;
import java.util.Random;

public class Polygon2D {
    public final Point2D a1, a2, a3;
    public final Color color;

    public Polygon2D(Point2D a1, Point2D a2, Point2D a3, Color color) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.color = color;
    }

    public void draw(Graphics g, Camera camera) {
//        Random r = new Random();
//        Color c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), 255);
        g.setColor(color);
        g.fillPolygon(new int[]{(int)a1.x,(int)a2.x,(int)a3.x},new int[]{(int)a1.y,(int)a2.y,(int)a3.y},3);
    }
}
