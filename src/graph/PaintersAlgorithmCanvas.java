package graph;

import javafx.util.Pair;
import utils.Point3D;
import utils.Polygon2D;
import utils.Polygon3D;
import utils.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class PaintersAlgorithmCanvas extends JPanel {
    public PaintersAlgorithmCanvas(Camera camera){
        this.camera = camera;
        setDoubleBuffered(false);
        this.setFocusable(true);
        this.requestFocusInWindow();

        requestFocus();
        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), LEFT);
        this.getActionMap().put(LEFT, left);
        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), RIGHT);
        this.getActionMap().put(RIGHT, right);

    }

    private static final String LEFT = "Left";
    private Action left = new AbstractAction(LEFT) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Pair<Vector3D, Vector3D> basises = camera.getBasises();
                Vector3D bW = basises.getKey(),
                        bH = basises.getValue();
                Point3D focus = camera.getFocus();

                Camera.Resolution res = camera.getResolution();
                camera.setFocus(new Point3D(focus.x-bW.x/(res.width/2.0), focus.y-bW.y/(res.width/2.0), focus.z-bW.z/(res.width/2.0)));
                repaint();
            }
        }
    };
    private static final String RIGHT = "Right";
    private Action right = new AbstractAction(RIGHT) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Pair<Vector3D, Vector3D> basises = camera.getBasises();
                Vector3D bW = basises.getKey(),
                        bH = basises.getValue();
                Point3D focus = camera.getFocus();

                Camera.Resolution res = camera.getResolution();
                camera.setFocus(new Point3D(focus.x+bW.x/(res.width/2.0), focus.y+bW.y/(res.width/2.0), focus.z+bW.z/(res.width/2.0)));
                repaint();
            }
        }
    };

    private final Set<Polygon3D> polygons = new HashSet<>();
    private final Camera camera;

    long drawingTime, calculatingTime;

    @Override
    protected void paintComponent(Graphics g2) {
        super.paintComponent(g2);
        synchronized (this) {
            drawingTime = 0;
            calculatingTime = 0;

            long startDrawing = System.nanoTime();
            BufferedImage bufferedImage = new BufferedImage((int) camera.getResolution().width, (int) camera.getResolution().height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.clearRect(0, 0, (int) camera.getResolution().width, (int) camera.getResolution().height);
            drawingTime += System.nanoTime() - startDrawing;

            for (Polygon3D polygon3D : polygons) {
                paintPolygon(g, camera, polygon3D);
            }

            startDrawing = System.nanoTime();
            g.dispose();
            ((Graphics2D) g2).drawImage(bufferedImage, null, 0, 0);
            drawingTime += System.nanoTime() - startDrawing;
        }
    }

    public void paintPolygon(Graphics g, Camera camera, Polygon3D polygon3D){
        long startCalculating = System.nanoTime();
        Polygon2D polygon2D = camera.project(polygon3D);
        calculatingTime += System.nanoTime() - startCalculating;
        long startDrawing = System.nanoTime();
        g.setColor(polygon2D.color);
        g.drawLine(((int)polygon2D.a1.x), (int)polygon2D.a1.y, (int)polygon2D.a2.x, (int)polygon2D.a2.y);
        g.drawLine(((int)polygon2D.a1.x), (int)polygon2D.a1.y, (int)polygon2D.a3.x, (int)polygon2D.a3.y);
        g.drawLine(((int)polygon2D.a3.x), (int)polygon2D.a3.y, (int)polygon2D.a2.x, (int)polygon2D.a2.y);
        drawingTime += System.nanoTime() - startDrawing;
    }

    public Set<Polygon3D> getPolygons() {
        return polygons;
    }
}
