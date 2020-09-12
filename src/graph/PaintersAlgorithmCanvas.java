package graph;

import utils.Polygon2D;
import utils.Polygon3D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class PaintersAlgorithmCanvas extends JPanel {
    public PaintersAlgorithmCanvas(Camera camera){
        this.camera = camera;
        setDoubleBuffered(false);
    }

    private final Set<Polygon3D> polygons = new HashSet<>();
    private final Camera camera;

    @Override
    protected void paintComponent(Graphics g2) {
        super.paintComponent(g2);
        BufferedImage bufferedImage = new BufferedImage( (int)camera.getResolution().width, (int)camera.getResolution().height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = bufferedImage.createGraphics ();
        g.clearRect(0, 0, (int) camera.getResolution().width, (int) camera.getResolution().height);
        for(Polygon3D polygon3D : polygons){
            paintPolygon(g, camera, polygon3D);
        }
        g.dispose();
        ((Graphics2D)g2).drawImage(bufferedImage,null,0,0);
    }

    public void paintPolygon(Graphics g, Camera camera, Polygon3D polygon3D){
        Polygon2D polygon2D = camera.project(polygon3D);

        g.setColor(polygon2D.color);

        g.drawLine(((int)polygon2D.a1.x), (int)polygon2D.a1.y, (int)polygon2D.a2.x, (int)polygon2D.a2.y);
        g.drawLine(((int)polygon2D.a1.x), (int)polygon2D.a1.y, (int)polygon2D.a3.x, (int)polygon2D.a3.y);
        g.drawLine(((int)polygon2D.a3.x), (int)polygon2D.a3.y, (int)polygon2D.a2.x, (int)polygon2D.a2.y);
    }

    public Set<Polygon3D> getPolygons() {
        return polygons;
    }
}
