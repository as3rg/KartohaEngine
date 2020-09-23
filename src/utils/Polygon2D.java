package utils;

import graph.Camera;

import java.awt.*;

public class Polygon2D implements Drawable {
    public final Point2D a1, a2, a3;
    public final Color color;

    public Polygon2D(Point2D a1, Point2D a2, Point2D a3, Color color) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.color = color;
    }

    @Override
    public void draw(Graphics g, Camera camera) {
        g.setColor(color);
        g.drawLine(((int)a1.x), (int)a1.y, (int)a2.x, (int)a2.y);
        g.drawLine(((int)a1.x), (int)a1.y, (int)a3.x, (int)a3.y);
        g.drawLine(((int)a3.x), (int)a3.y, (int)a2.x, (int)a2.y);
    }
}
