package graph;

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
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), UP);
        this.getActionMap().put(UP, up);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK), DOWN);
        this.getActionMap().put(DOWN, down);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), LEFT);
        this.getActionMap().put(LEFT, left);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), RIGHT);
        this.getActionMap().put(RIGHT, right);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), FORWARD);
        this.getActionMap().put(FORWARD, forward);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), BACK);
        this.getActionMap().put(BACK, back);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), ROTATEPLUS);
        this.getActionMap().put(ROTATEPLUS, rotatePlus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), ROTATEMINUS);
        this.getActionMap().put(ROTATEMINUS, rotateMinus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), CAMERAXPLUS);
        this.getActionMap().put(CAMERAXPLUS, cameraXPlus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), CAMERAXMINUS);
        this.getActionMap().put(CAMERAXMINUS, cameraXMinus);
    }

    private static final String CAMERAXPLUS = "CameraX+";
    private Action cameraXPlus = new AbstractAction(CAMERAXPLUS) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D rR = camera.getRotatedVectors(-rotateStep, 0).getKey();
                camera.setVector(rR);
                repaint();
            }
        }
    };

    private static final String CAMERAXMINUS = "CameraX-";
    private Action cameraXMinus = new AbstractAction(CAMERAXMINUS) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D rR = camera.getRotatedVectors(rotateStep, 0).getKey();
                camera.setVector(rR);
                repaint();
            }
        }
    };

    private static final String LEFT = "Left";
    private Action left = new AbstractAction(LEFT) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D mR = camera.getMovingBasises(step, step, step).second;
                Point3D focus = camera.getFocus();

                camera.setFocus(new Point3D(focus.x-mR.x, focus.y-mR.y, focus.z-mR.z));
                repaint();
            }
        }
    };
    private static final String RIGHT = "Right";
    private Action right = new AbstractAction(RIGHT) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D mR = camera.getMovingBasises(step, step, step).second;
                Point3D focus = camera.getFocus();

                camera.setFocus(new Point3D(focus.x+mR.x, focus.y+mR.y, focus.z+mR.z));
                repaint();
            }
        }
    };

    private static final String FORWARD = "Forward";
    private Action forward = new AbstractAction(FORWARD) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D mF = camera.getMovingBasises(step, step, step).first;
                Point3D focus = camera.getFocus();

                camera.setFocus(new Point3D(focus.x+mF.x, focus.y+mF.y, focus.z+mF.z));
                repaint();
            }
        }
    };
    private static final String BACK = "Back";
    private Action back = new AbstractAction(BACK) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D mF = camera.getMovingBasises(step, step, step).first;
                Point3D focus = camera.getFocus();

                camera.setFocus(new Point3D(focus.x-mF.x, focus.y-mF.y, focus.z-mF.z));
                repaint();
            }
        }
    };

    private static final String UP = "Up";
    private Action up = new AbstractAction(UP) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D mF = camera.getMovingBasises(step, step, step).third;
                Point3D focus = camera.getFocus();

                camera.setFocus(new Point3D(focus.x+mF.x, focus.y+mF.y, focus.z+mF.z));
                repaint();
            }
        }
    };
    private static final String DOWN = "Down";
    private Action down = new AbstractAction(DOWN) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                Vector3D mF = camera.getMovingBasises(step, step, step).third;
                Point3D focus = camera.getFocus();

                camera.setFocus(new Point3D(focus.x-mF.x, focus.y-mF.y, focus.z-mF.z));
                repaint();
            }
        }
    };


    private static final double rotateStep = Math.PI/180;
    private static final String ROTATEPLUS = "Rotate+";
    private Action rotatePlus = new AbstractAction(FORWARD) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                camera.setRotateAngle(camera.getRotateAngle()+rotateStep);
                repaint();
            }
        }
    };
    private static final String ROTATEMINUS = "Rotate-";
    private Action rotateMinus = new AbstractAction(FORWARD) {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (camera) {
                camera.setRotateAngle(camera.getRotateAngle()-rotateStep);
                repaint();
            }
        }
    };
    private final double step = 10;

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