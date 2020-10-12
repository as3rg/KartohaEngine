package graph;

import com.aparapi.Kernel;
import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class CanvasPanel extends JPanel {
    public CanvasPanel(Camera camera){
        this.camera = camera;
        image = new BufferedImage((int)camera.getResolution().width, (int)camera.getResolution().height, BufferedImage.TYPE_INT_RGB);
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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mU.addToPoint(focus)));
                    kernel.setCamera(camera);
                    
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
                    kernel.setCamera(camera);
                    
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
                    kernel.setCamera(camera);
                    
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
                    kernel.setCamera(camera);
                    
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
                    kernel.setCamera(camera);
                    
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
                    kernel.setCamera(camera);
                    
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
                    kernel.setCamera(camera);
                    
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
                    kernel.setCamera(camera);
                    
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
                    Vector3D rR = camera.getRightRotatedVectors(2*Math.PI-rotateStep);
                    camera.setScreen(new Screen(rR, camera.getScreen().focus));
                    kernel.setCamera(camera);
                    
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
                    camera.setScreen(new Screen(rR, camera.getScreen().focus));
                    kernel.setCamera(camera);
                    
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
                    camera.setScreen(new Screen(rT, camera.getScreen().focus));
                    kernel.setCamera(camera);
                    
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
                    camera.setScreen(new Screen(rT, camera.getScreen().focus));
                    kernel.setCamera(camera);
                    
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

    public KernelProcess kernel;

    final BufferedImage image;

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
            kernel.get();
            kernel.execute(kernel.count);

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
