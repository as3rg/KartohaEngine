package com.guryanov_sergeev;

import graph.Camera;
import graph.Drawable;
import graph.CanvasPanel;
import geometry.objects3D.*;
import graph.Screen;
import utils.throwables.ImpossiblePolygonException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.Math;
import java.util.*;
import java.util.List;

public class Main {

    public static final int FocusLength = 300;
    public static final int Distance = 500;
    public static void main(String[] args) {
        int angle = 45;
        Camera.Resolution resolution = new Camera.Resolution(1920, 1080);
        Camera camera = new Camera(new Screen(new Vector3D(FocusLength,0,0), new Point3D(-Distance,0,0)), resolution, 0);
        CanvasPanel canvas = new CanvasPanel(camera);

        //Куб
//        Point3D A = new Point3D(-50, -50, -100),
//                B = new Point3D(-50, -50, -200),
//                C = new Point3D(-50, 50, -200),
//                D = new Point3D(-50, 50, -100),
//                A2 = new Point3D(50, -50, -100),
//                B2 = new Point3D(50, -50, -200),
//                C2 = new Point3D(50, 50, -200),
//                D2 = new Point3D(50, 50, -100);
//
//
//
//        canvas.getDrawables().add(new Polygon3D(A, B, C, Color.RED));
//        canvas.getDrawables().add(new Polygon3D(A, D, C, Color.RED));
//
//        canvas.getDrawables().add(new Polygon3D(A, B, B2, Color.GREEN));
//        canvas.getDrawables().add(new Polygon3D(A, A2, B2, Color.GREEN));
//
//        canvas.getDrawables().add(new Polygon3D(D, D2, C2, Color.BLUE));
//        canvas.getDrawables().add(new Polygon3D(D, C, C2, Color.BLUE));
//
//        canvas.getDrawables().add(new Polygon3D(D, A, A2, Color.YELLOW));
//        canvas.getDrawables().add(new Polygon3D(D, D2, A2, Color.YELLOW));
//        canvas.getDrawables().add(new Polygon3D(B, B2, C2, Color.ORANGE));
//        canvas.getDrawables().add(new Polygon3D(B, C, C2, Color.ORANGE));




//
//        canvas.getDrawables().add(new Polygon3D(A2, B2, C2, Color.LIGHT_GRAY));
//        canvas.getDrawables().add(new Polygon3D(new Point3D(0,0,0), new Point3D(0,0,50), new Point3D(0,50,50), Color.LIGHT_GRAY));

        canvas.getDrawables().addAll(drawSphere(-100, 0,0, 100, 5));
        canvas.getDrawables().addAll(drawSphere(100, 0,0, 100, 5));
        canvas.getDrawables().addAll(drawSphere(200, 0,0, 100, 5));

        canvas.prepare();
//        canvas.getDrawables().addAll(drawSphere(100, 0,0, 100, 1));
//        canvas.getDrawables().addAll(drawSphere(0, 0,350, 75, 15));
//        canvas.getDrawables().addAll(drawCylinder(0, 0,-50, 75, 400, 5));


//        Polygon3D p1 = new Polygon3D(new Point3D(-10,0,0), new Point3D(10,0,0), new Point3D(-10,0,10), Color.YELLOW);
//        Polygon3D p2 = new Polygon3D(new Point3D(-10,-10,0), new Point3D(10,10,0), new Point3D(0,-10,10), Color.RED);
//
//        canvas.getDrawables().add(p1);
//        canvas.getDrawables().add(p2);
//        Optional<Line3D> line = p1.getPlane().getIntersection(p2.getPlane());
//        line.ifPresent(line3D -> {
//            canvas.getDrawables().add(line3D);
//            Optional<Point3D> p = new Line3D(p1.a1, p1.a2).getIntersection(line.get());
//            p.ifPresent(point3D -> canvas.getDrawables().add(point3D));
//            p = new Line3D(p1.a1, p1.a3).getIntersection(line.get());
//            p.ifPresent(point3D -> canvas.getDrawables().add(point3D));
//            p = new Line3D(p1.a3, p1.a2).getIntersection(line.get());
//            p.ifPresent(point3D -> canvas.getDrawables().add(point3D));
//        });

        JFrame frame = new JFrame();

        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
//        frame.setFocusable(true);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.addMouseListener(new MouseAdapter() {
            Point mousePoint;

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
//                synchronized (camera) {
//                    Vector3D rR = camera.getTopRotatedVectors(Math.PI*(e.getY()-canvas.getHeight()/2.0)/canvas.getHeight());
//                    camera.setScreen(new Screen(rR, camera.getScreen().focus));
//                    canvas.kernel.setCamera(camera, canvas.image);
//
//                }
//                mousePoint = e.getPoint();
            }
        });

//        new Thread(()-> {
//            for (int i = 0; ;i = (i+1)%360 ) {
//                canvas.getPolygons().clear();
//                canvas.getPolygons().add(new Polygon3D(new Point3D(0,0,0), new Point3D(0,0,50), new Point3D(50*utils.Math.destroyMinusZeros(Math.cos(2*i * Math.PI / 180)),50*utils.Math.destroyMinusZeros(Math.sin(2*i * Math.PI / 180)),50), Color.LIGHT_GRAY));
//                camera.setFocus(new Point3D(-Distance * utils.Math.destroyMinusZeros(Math.cos(i * Math.PI / 180)), -Distance * utils.Math.destroyMinusZeros(Math.sin(i * Math.PI / 180)), 100));
//                camera.setVector(new Vector3D(FocusLength * utils.Math.destroyMinusZeros(Math.cos(i * Math.PI / 180)), FocusLength * utils.Math.destroyMinusZeros(Math.sin(i * Math.PI / 180)), -150));
//                //System.out.println(i);
//                camera.setRotateAngle(Math.PI * i / 180);
//                //System.out.printf("%.2f %.2f\n",FocusLength * utils.Math.destroyMinusZeros(java.lang.Math.cos(2*i * java.lang.Math.PI / 180)), FocusLength * utils.Math.destroyMinusZeros(java.lang.Math.sin(2*i * java.lang.Math.PI / 180)));
//                canvas.repaint();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        new Thread(()->{
            while (true) {
                canvas.repaint();
            }
        }).start();
    }

    public static Collection<Drawable> drawSphere(double x, double y, double z, double R, int step){
        Set<Drawable> drawableSet = new HashSet<>();
        java.util.List<java.util.List<Point3D>> C = new ArrayList<>();
        for (int j = 0; j < 360; j+= step) {
            List<Point3D> B = new ArrayList<>();
            for (int i = 0; i < 360; i += step) {
                B.add(new Point3D(x+R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y+R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z+R * utils.Math.roundNearZero(Math.cos(j * Math.PI / 180))));
            }
            C.add(B);
        }
        //System.out.println(360*360/step/step+" points");
        Random r = new Random();
        for(int j = 0; j < 180/step -1; j++) {
            List<Point3D> A = C.get(j), B = C.get(j+1);
            for (int i = 0; i < 360/step; i += 1) {
                Color c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), 255);
                try{ drawableSet.add(new Polygon3D(A.get(i), A.get((i + 1) % (360/step)), B.get((i + 1) % (360/step)), c)); } catch (ImpossiblePolygonException ignored){}
                try{ drawableSet.add(new Polygon3D(A.get(i), B.get(i), B.get((i + 1) % (360/step)), c)); } catch (ImpossiblePolygonException ignored){}
            }
        }
        System.out.println(drawableSet.size()+" polygons");
        return drawableSet;
    }



    public static Collection<Drawable> drawCylinder(double x, double y, double z, double R, double h, int step){
        Set<Drawable> drawableSet = new HashSet<>();
        java.util.List<Point3D> A = new ArrayList<>(),B = new ArrayList<>();
        for (int i = 0; i < 360; i += step) {
            A.add(new Point3D(x+R  * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y+R * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z));
            B.add(new Point3D(x+R  * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y+R * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z+h));
        }
        //System.out.println(2*360/step+" points");
        Random r = new Random();
        for (int i = 0; i < 360/step; i += 1) {
            Color c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), 255);
            try {
                drawableSet.add(new Polygon3D(A.get(i), A.get((i + 1) % (360 / step)), B.get((i + 1) % (360/step)), c));
            }catch (ImpossiblePolygonException ignored){}
            try {
                drawableSet.add(new Polygon3D(A.get(i), B.get(i), B.get((i + 1) % (360/step)), c));
            }catch (ImpossiblePolygonException ignored){}
        }
        //System.out.println(drawableSet.size()+" polygons");
        return drawableSet;
    }
}
