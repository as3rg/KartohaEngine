package graph;

import com.aparapi.Kernel;
import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class CanvasPanel extends JPanel {
    public CanvasPanel(Camera camera) {
        this.camera = camera;
        image = new BufferedImage((int) camera.getResolution().width, (int) camera.getResolution().height, BufferedImage.TYPE_INT_RGB);
        setDoubleBuffered(false);
        this.setFocusable(true);
        this.requestFocusInWindow();

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);

        requestFocus();


        addMouseMotionListener(new MouseMotionAdapter() {
            Robot r;

            boolean working = false;

            {
                try {
                    r = new Robot();
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (!working) {
                    synchronized (camera) {
                        working = true;
                        Vector3D v = camera.getRotatedVector(Math.PI * (e.getX() - (getWidth() / 2.0 + getX())) / getWidth(),
                                -Math.PI * (e.getY() - (getHeight() / 2.0 + getY())) / getHeight());
                        camera.setScreen(new Screen(v, camera.getScreen().focus));
                        kernel.setCamera(camera, image);
                        r.mouseMove(getX() + getWidth() / 2, getY() + getHeight() / 2);
                        working = false;
                    }
                }
            }
        });

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), UP);
        Action up = new AbstractAction(UP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (camera) {
                    Vector3D mU = camera.getMovingBasises(step, step, step).third;
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mU.addToPoint(focus)));
                    kernel.setCamera(camera, image);

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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mU.multiply(-1).addToPoint(focus)));
                    kernel.setCamera(camera, image);

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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mR.multiply(-1).addToPoint(focus)));
                    kernel.setCamera(camera, image);

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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mR.addToPoint(focus)));
                    kernel.setCamera(camera, image);

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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mF.addToPoint(focus)));
                    kernel.setCamera(camera, image);

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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mF.multiply(-1).addToPoint(focus)));
                    kernel.setCamera(camera, image);

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
                    kernel.setCamera(camera, image);

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
                    kernel.setCamera(camera, image);

                }
            }
        };
        this.getActionMap().put(ROTATEMINUS, rotateMinus);

    }

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

    public KernelProcess kernel;

    public BufferedImage image;

    public void prepare(){
        kernel = new KernelProcess(camera, 1156680, image);
        int i = 0;
        for(Drawable d : drawables) {
            Polygon3D p = (Polygon3D) d;
            kernel.colors[i] = p.color.getRGB();

            kernel.x[3*i] = p.a1.x;
            kernel.y[3*i] = p.a1.y;
            kernel.z[3*i] = p.a1.z;

            kernel.x[3*i+1] = p.a2.x;
            kernel.y[3*i+1] = p.a2.y;
            kernel.z[3*i+1] = p.a2.z;

            kernel.x[3*i+2] = p.a3.x;
            kernel.y[3*i+2] = p.a3.y;
            kernel.z[3*i+2] = p.a3.z;
            i++;
        }
        kernel.count = i;
        kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
    }

    long start;
    public long frames;
    double fps = 0;

    @Override
    protected void paintComponent(Graphics g2) {
        super.paintComponent(g2);
        synchronized (this) {
            kernel.execute(kernel.count);

            image = kernel.get();

            ((Graphics2D) g2).drawImage(image, null, 0, 0);
//            g2.drawImage(image, 0, 0, (int)camera.getResolution().width, (int)camera.getResolution().height, 0, 0, (int)camera.getResolution().width, (int)camera.getResolution().height, this);

            final long now = System.currentTimeMillis();


            frames++;
            if ((now - start) > 1000) {
                fps = (frames * 1000.0) / (now - start);
                start = now;
                frames = 0;
            }
            g2.drawString(String.format("%5.2f", fps), 20, 100);
        }
    }

    public Set<Drawable> getDrawables() {
        return drawables;
    }
}
