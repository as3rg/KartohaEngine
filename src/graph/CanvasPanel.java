package graph;

import com.aparapi.Kernel;
import com.aparapi.Range;
import geometry.objects2D.Point2D;
import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.SearchTree3D;
import geometry.objects3D.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CanvasPanel extends JPanel {
    public CanvasPanel(Camera camera){
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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mU.addToPoint(focus)));
                    kernel.setCamera(camera);
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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mU.multiply(-1).addToPoint(focus)));
                    kernel.setCamera(camera);
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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mR.multiply(-1).addToPoint(focus)));
                    kernel.setCamera(camera);
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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mR.addToPoint(focus)));
                    kernel.setCamera(camera);
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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mF.addToPoint(focus)));
                    kernel.setCamera(camera);
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
                    Point3D focus = camera.getScreen().focus;

                    camera.setScreen(new Screen(camera.getScreen().vector, mF.multiply(-1).addToPoint(focus)));
                    kernel.setCamera(camera);
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
                    kernel.setCamera(camera);
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
                    kernel.setCamera(camera);
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
                    Vector3D rR = camera.getRightRotatedVectors(2*Math.PI-rotateStep);
                    camera.setScreen(new Screen(rR, camera.getScreen().focus));
                    kernel.setCamera(camera);
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
                    camera.setScreen(new Screen(rR, camera.getScreen().focus));
                    kernel.setCamera(camera);
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
                    camera.setScreen(new Screen(rT, camera.getScreen().focus));
                    kernel.setCamera(camera);
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
                    camera.setScreen(new Screen(rT, camera.getScreen().focus));
                    kernel.setCamera(camera);
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

    public KernelProcess kernel;

    public void prepare(){
        kernel = new KernelProcess(camera, 1156680);
        int i = 0;
        for(Drawable d : drawables) {
            Polygon3D p = (Polygon3D) d;
            kernel.colors[i] = p.color.getRed();
            kernel.colors[i+1] = p.color.getGreen();
            kernel.colors[i+2] = p.color.getBlue();

            kernel.x[i] = p.a1.x;
            kernel.y[i] = p.a1.y;
            kernel.z[i] = p.a1.z;
            i++;
            kernel.x[i] = p.a2.x;
            kernel.y[i] = p.a2.y;
            kernel.z[i] = p.a2.z;
            i++;
            kernel.x[i] = p.a3.x;
            kernel.y[i] = p.a3.y;
            kernel.z[i] = p.a3.z;
            i++;
        }
        kernel.count = i;
        kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);

    }

    @Override
    protected void paintComponent(Graphics g2) {
        super.paintComponent(g2);
        synchronized (this) {
            drawingTime = 0;
            calculatingTime = 0;

            long startDrawing = System.nanoTime();
            BufferedImage bufferedImage = new BufferedImage((int) camera.getResolution().width, (int) camera.getResolution().height, BufferedImage.TYPE_INT_RGB);
            drawingTime += System.nanoTime() - startDrawing;

//            SearchTree3D<Drawable> st = new SearchTree3D<>(drawables);

//            Canvas canvas = new Canvas(camera.getResolution());
            long startCalculating = System.nanoTime();
//            Canvas[] canvas1 = new Canvas[drawables2.length];

//            int i = 0;
//            KernelProcess kernel = new KernelProcess(camera, 1156680);
//            for(Drawable d : drawables) {
//                Polygon3D p = (Polygon3D) d;
//                kernel.x[i] = p.a1.x;
//                kernel.y[i] = p.a1.y;
//                kernel.z[i] = p.a1.z;
//                i++;
//                kernel.x[i] = p.a2.x;
//                kernel.y[i] = p.a2.y;
//                kernel.z[i] = p.a2.z;
//                i++;
//                kernel.x[i] = p.a3.x;
//                kernel.y[i] = p.a3.y;
//                kernel.z[i] = p.a3.z;
//                i++;
//            }

            kernel.execute(kernel.count);


            calculatingTime = System.nanoTime() - startCalculating;
            startDrawing = System.nanoTime();

            bufferedImage.getRaster().setPixels(0,0,(int)camera.getResolution().width, (int)camera.getResolution().height, kernel.result);
//            for(Point2D p : kernel.getResult()){
//                if(p.x >= 0 && p.x < camera.getResolution().width && p.y >= 0 && p.y < camera.getResolution().height) {
//                    bufferedImage.setRGB((int) p.x, (int) p.y, Color.BLACK.getRGB());
//                }
//            }
//            for(Canvas i2 : iii){
//                System.out.println(i2);
//            }

//            for (Drawable drawable : drawables) {
////                long time = System.nanoTime();
//                drawable.draw(canvas, camera);
////                System.out.println(System.nanoTime() - time);
//            }
//            for (Drawable drawable : drawables) {
//                for(Drawable drawable2 : st.get(drawable.getRegion())){
//                    if(drawable.equals(drawable2)) continue;
//                    st.remove(drawable2);
//                    st.addAll(drawable2.split(camera, drawable));
//                }
//            }
//
//            Set<Drawable> drawables = new TreeSet<>(((o1, o2) -> {
//                int res = o1.compareZ(camera, o2);
//                if (res == 0) return 1;
//                return -res;
//            }));
//            drawables.addAll(st.get(st.getRegion()));


//            for(int i = 0; i < canvas.getResolution().width; i++){
//                for(int j = 0; j < canvas.getResolution().height; j++){
//                    bufferedImage.setRGB(i, j,canvas.get(i, j).color.getRGB());
//                }
//            }
            ((Graphics2D) g2).drawImage(bufferedImage, null, 0, 0);
            drawingTime += System.nanoTime() - startDrawing;
//            System.out.println("Calculating: " + calculatingTime);
//            System.out.println("Drawing: " + drawingTime);
//            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
//            out.println("Calculating: " + calculatingTime);
//            out.println("Drawing: " + drawingTime);
//            out.flush();
        }
    }

    public Set<Drawable> getDrawables() {
        return drawables;
    }
}
