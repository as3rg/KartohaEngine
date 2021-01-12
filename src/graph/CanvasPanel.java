package graph;

import com.aparapi.Kernel;
import geometry.objects3D.Point3D;
import geometry.objects3D.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class CanvasPanel extends JFrame implements KeyListener {
    public CanvasPanel(Camera camera) {
        this.camera = camera;
        image = new BufferedImage((int) camera.getResolution().width, (int) camera.getResolution().height, BufferedImage.TYPE_INT_RGB);
        this.setFocusable(true);
        this.requestFocusInWindow();

        addKeyListener(this);

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
                        r.mouseMove(getX() + getWidth() / 2, getY() + getHeight() / 2);
                        working = false;
                    }
                }
            }
        });

    }
    private static final double rotateStep = Math.PI / 180;
    private final double step = 10;

    private final Set<Drawable> drawables = new HashSet<>();
    private final Camera camera;

    public KernelProcess kernel;

    public BufferedImage image;

    public void prepare() {
        kernel = new KernelProcess(camera, image);
        kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
    }

    long start;
    public long frames;
    double fps = 0;

    @Override
    public void paint(Graphics g) {

        kernel.setDrawables(getDrawables());
        kernel.setCamera(camera, image);

        BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();
        }
        Graphics g2 = bufferStrategy.getDrawGraphics();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (kernel.count != 0 && kernel.prefix[kernel.count] != 0)
            for (int i = 0; i < kernel.prefix[kernel.count]; i++) {
                kernel.calc(i);
            }
        image = kernel.get();

        ((Graphics2D) g2).drawImage(image, null, 0, 0);
        final long now = System.currentTimeMillis();

        frames++;
        if ((now - start) > 1000) {
            fps = (frames * 1000.0) / (now - start);
            start = now;
            frames = 0;
        }
        g2.drawString(String.format("%5.2f", fps), 20, 100);
        g2.dispose();
        bufferStrategy.show();
    }

    public Set<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (this) {
            Point3D focus = camera.getScreen().focus;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    Vector3D mU = camera.getMovingBasises(step, step, step).third;

                    camera.setScreen(new Screen(camera.getScreen().vector, mU.addToPoint(focus)));
                    break;
                case KeyEvent.VK_SHIFT:
                    Vector3D mD = camera.getMovingBasises(step, step, step).third;

                    camera.setScreen(new Screen(camera.getScreen().vector, mD.multiply(-1).addToPoint(focus)));
                    break;
                case KeyEvent.VK_A:

                    Vector3D mR = camera.getMovingBasises(step, step, step).second;

                    camera.setScreen(new Screen(camera.getScreen().vector, mR.multiply(-1).addToPoint(focus)));
                    break;
                case KeyEvent.VK_D:

                    Vector3D mL = camera.getMovingBasises(step, step, step).second;

                    camera.setScreen(new Screen(camera.getScreen().vector, mL.addToPoint(focus)));
                    break;
                case KeyEvent.VK_W:
                    Vector3D mF = camera.getMovingBasises(step, step, step).first;

                    camera.setScreen(new Screen(camera.getScreen().vector, mF.addToPoint(focus)));
                    break;
                case KeyEvent.VK_S:
                    Vector3D mB = camera.getMovingBasises(step, step, step).first;

                    camera.setScreen(new Screen(camera.getScreen().vector, mB.multiply(-1).addToPoint(focus)));
                    break;
                case KeyEvent.VK_Q:
                    camera.setRotateAngle(camera.getRotateAngle() + rotateStep);
                    break;
                case KeyEvent.VK_E:
                    camera.setRotateAngle(camera.getRotateAngle() - rotateStep);
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
