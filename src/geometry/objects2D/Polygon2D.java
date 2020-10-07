package geometry.objects2D;

import geometry.objects3D.Point3D;
import geometry.objects3D.Region3D;
import graph.Camera;
import graph.CanvasPanel;
import graph.Drawable;
import graph.Pixel;

import java.awt.*;
import java.util.Random;

public class Polygon2D implements Drawable {
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

    @Override
    public void draw(graph.Canvas cp, Camera camera) {
        cp.set((int) a1.x, (int) a1.y, new Pixel(1, color));
        cp.set((int) a2.x, (int) a2.y, new Pixel(1, color));
        cp.set((int) a3.x, (int) a3.y, new Pixel(1, color));


        Vector2D v23 = new Vector2D(a2, a3);
        for(int j = 1; j <= v23.getLength(); j++){
            Point2D p = v23.multiply(j/v23.getLength()).addToPoint(a2);
            Vector2D v1p = new Vector2D(a1, p);
            for(int i = 1; i <= v1p.getLength(); i++){
                Point2D p2 = v1p.multiply(i/v1p.getLength()).addToPoint(a1);
                cp.set((int)p2.x, (int)p2.y, new Pixel(1,color));
            }
        }
    }
}
