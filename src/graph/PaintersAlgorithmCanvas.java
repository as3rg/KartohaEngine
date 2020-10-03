package graph;

import utils.Drawable;
import utils.Objects3D.Plane3D;
import utils.Objects3D.Point3D;
import utils.Objects3D.Vector3D;

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
        Action up = new AbstractAction(UP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D mU = camera.getMovingBasises(step, step, step).third;
                    Point3D focus = camera.getScreen().point;

                    camera.setScreen(new Plane3D(camera.getScreen().vector, mU.addToPoint(focus)));
                    repaint();
                }
            }
        };
        this.getActionMap().put(UP, up);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, KeyEvent.SHIFT_DOWN_MASK), DOWN);
        Action down = new AbstractAction(DOWN) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D mU = camera.getMovingBasises(step, step, step).third;
                    Point3D focus = camera.getScreen().point;

                    camera.setScreen(new Plane3D(camera.getScreen().vector, mU.multiply(-1).addToPoint(focus)));
                    repaint();
                }
            }
        };
        this.getActionMap().put(DOWN, down);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), LEFT);
        Action left = new AbstractAction(LEFT) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D mR = camera.getMovingBasises(step, step, step).second;
                    Point3D focus = camera.getScreen().point;

                    camera.setScreen(new Plane3D(camera.getScreen().vector, mR.multiply(-1).addToPoint(focus)));
                    repaint();
                }
            }
        };
        this.getActionMap().put(LEFT, left);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), RIGHT);
        Action right = new AbstractAction(RIGHT) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D mR = camera.getMovingBasises(step, step, step).second;
                    Point3D focus = camera.getScreen().point;

                    camera.setScreen(new Plane3D(camera.getScreen().vector, mR.addToPoint(focus)));
                    repaint();
                }
            }
        };
        this.getActionMap().put(RIGHT, right);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), FORWARD);
        Action forward = new AbstractAction(FORWARD) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D mF = camera.getMovingBasises(step, step, step).first;
                    Point3D focus = camera.getScreen().point;

                    camera.setScreen(new Plane3D(camera.getScreen().vector, mF.addToPoint(focus)));
                    repaint();
                }
            }
        };
        this.getActionMap().put(FORWARD, forward);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), BACK);
        Action back = new AbstractAction(BACK) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D mF = camera.getMovingBasises(step, step, step).first;
                    Point3D focus = camera.getScreen().point;

                    camera.setScreen(new Plane3D(camera.getScreen().vector, mF.multiply(-1).addToPoint(focus)));
                    repaint();
                }
            }
        };
        this.getActionMap().put(BACK, back);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), ROTATEPLUS);
        Action rotatePlus = new AbstractAction(FORWARD) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    camera.setRotateAngle(camera.getRotateAngle() + rotateStep);
                    repaint();
                }
            }
        };
        this.getActionMap().put(ROTATEPLUS, rotatePlus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), ROTATEMINUS);
        Action rotateMinus = new AbstractAction(FORWARD) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    camera.setRotateAngle(camera.getRotateAngle() - rotateStep);
                    repaint();
                }
            }
        };
        this.getActionMap().put(ROTATEMINUS, rotateMinus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), CAMERAXPLUS);
        Action cameraXPlus = new AbstractAction(CAMERAXPLUS) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D rR = camera.getRightRotatedVectors(-rotateStep);
                    camera.setScreen(new Plane3D(rR, camera.getScreen().point));
                    repaint();
                }
            }
        };
        this.getActionMap().put(CAMERAXPLUS, cameraXPlus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), CAMERAXMINUS);
        Action cameraXMinus = new AbstractAction(CAMERAXMINUS) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D rR = camera.getRightRotatedVectors(rotateStep);
                    camera.setScreen(new Plane3D(rR, camera.getScreen().point));
                    repaint();
                }
            }
        };
        this.getActionMap().put(CAMERAXMINUS, cameraXMinus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), CAMERAYPLUS);
        Action cameraYPlus = new AbstractAction(CAMERAYPLUS) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D rT = camera.getTopRotatedVectors(-rotateStep);
                    camera.setScreen(new Plane3D(rT, camera.getScreen().point));
                    repaint();
                }
            }
        };
        this.getActionMap().put(CAMERAYPLUS, cameraYPlus);

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), CAMERAYMINUS);
        Action cameraYMinus = new AbstractAction(CAMERAYMINUS) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D rT = camera.getTopRotatedVectors(rotateStep);
                    camera.setScreen(new Plane3D(rT, camera.getScreen().point));
                    repaint();
                }
            }
        };
        this.getActionMap().put(CAMERAYMINUS, cameraYMinus);
    }

    private static final String CAMERAXPLUS = "CameraX+";

    private static final String CAMERAXMINUS = "CameraX-";

    private static final String CAMERAYPLUS = "CameraY+";

    private static final String CAMERAYMINUS = "CameraY-";

    private static final String LEFT = "Left";
    private static final String RIGHT = "Right";

    private static final String FORWARD = "Forward";
    private static final String BACK = "Back";

    private static final String UP = "Up";
    private static final String DOWN = "Down";


    private static final double rotateStep = Math.PI/180;
    private static final String ROTATEPLUS = "Rotate+";
    private static final String ROTATEMINUS = "Rotate-";
    private final double step = 10;

    private final Set<Drawable> drawables = new HashSet<>();
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

            for (Drawable drawable : drawables) {
                drawable.draw(g, camera);
            }

            startDrawing = System.nanoTime();
            g.dispose();
            ((Graphics2D) g2).drawImage(bufferedImage, null, 0, 0);
            drawingTime += System.nanoTime() - startDrawing;
        }
    }

    public Set<Drawable> getDrawables() {
        return drawables;
    }
}
