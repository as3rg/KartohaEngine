package com.guryanov_sergeev;

import com.aparapi.Kernel;
import com.aparapi.Kernel.EXECUTION_MODE;
import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;
import geometry.polygonal.Polyhedron;
import geometry.polygonal.Sphere;
import graph.Camera;
import graph.CanvasPanel;
import graph.Screen;
import utils.Pair;
import utils.throwables.ImpossiblePolygonException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class Main {

    public static final int FocusLength = 300;
    public static final int Distance = 500;

    public static void main(String[] args) {
//        Matrix3 m = new Matrix3(new double[][]{{2,4,1}, {0,2,1},{2,1,1}});
//        System.out.println(m);
//        System.out.println(m.reversedMatrix());
//
//

        Camera.Resolution resolution = new Camera.Resolution(1920, 1080);
        Camera camera = new Camera(new Screen(new Vector3D(FocusLength, 0, 0), new Point3D(-Distance, 500, 2000)), resolution);
        CanvasPanel canvas = new CanvasPanel(camera, EXECUTION_MODE.JTP);

        //Куб
        Point3D A = new Point3D(-50, -5000, 30000),
                B = new Point3D(-50, -5000, 0),
                C = new Point3D(-50, 5000, 30000),
                D = new Point3D(-50, 5000, 0);


        Collection<Polygon3D> polys = new HashSet<>();
        polys.add(new Polygon3D(new Point3D(-1000, -1000, 0),
            new Point3D(2000, -1000, 0),
            new Point3D(-1000, 2000, 0), Color.red));
//
//        polys.add(new Polygon3D(A, B, C, Color.RED));
//        polys.add(new Polygon3D(A, D, C, Color.GREEN));
//        Pair<Polygon3D, Polygon3D> p = Polygon3D.getPolygons(Color.RED, A, B, C, D).get();
//        polys.add(p.first);
//        polys.add(p.second);
////
//        polys.add(new Polygon3D(A, B, B2, Color.GREEN));
//        polys.add(new Polygon3D(A, A2, B2, Color.GREEN));

//        p = Polygon3D.getPolygons(Color.GREEN, A, B, A2, B2).get();
//        polys.add(p.first);
//        polys.add(p.second);
//
//////
////        polys.add(new Polygon3D(D, D2, C2, Color.BLUE));
////        polys.add(new Polygon3D(D, C, C2, Color.BLUE));
//
//        p = Polygon3D.getPolygons(Color.BLUE, C, C2, D, D2).get();
//        polys.add(p.first);
//        polys.add(p.second);
////
////        polys.add(new Polygon3D(D, A, A2, Color.YELLOW));
////        polys.add(new Polygon3D(D, D2, A2, Color.YELLOW));
//
//        p = Polygon3D.getPolygons(Color.YELLOW, A, A2, D, D2).get();
//        polys.add(p.first);
//        polys.add(p.second);
//
////        polys.add(new Polygon3D(B, B2, C2, Color.ORANGE));
////        polys.add(new Polygon3D(B, C, C2, Color.ORANGE));
//
//        p = Polygon3D.getPolygons(Color.ORANGE, B, B2, C, C2).get();
//        polys.add(p.first);
//        polys.add(p.second);


//        Sphere s = new Sphere(A, 100, 15, Color.BLUE);
//        canvas.getPolygonals().add(s);
//        canvas.getPolygonals().add(s);
        canvas.getPolygonals().add(new Polyhedron(new Point3D(0, 100, 0), polys));
//        canvas.getPolygonals().addAll(drawSphere(200,0, 0, 100, 15, Color.RED));
        System.out.println(canvas.getPolygonals().size());
        canvas.setSize(1920, 1080);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setLocationRelativeTo(null);
        canvas.setResizable(false);
        canvas.setUndecorated(true);
        canvas.setVisible(true);

        new Thread(() -> {
            double i = 0;
            while (true) {
                canvas.repaint();
//                synchronized (canvas) {
//                    s.rotate(new Vector3D(0, 0, 0.1), Point3D.ZERO);
//                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Collection<Polygon3D> drawSphere(double x, double y, double z, double R, int step, Color c) {
        Set<Polygon3D> drawableSet = new HashSet<>();
        java.util.List<java.util.List<Point3D>> C = new ArrayList<>();
        for (int j = 0; j < 360; j += step) {
            List<Point3D> B = new ArrayList<>();
            for (int i = 0; i < 360; i += step) {
                B.add(new Point3D(x + R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y + R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z + R * utils.Math.roundNearZero(Math.cos(j * Math.PI / 180))));
            }
            C.add(B);
        }
        for (int j = 0; j < 180 / step - 1; j++) {
            List<Point3D> A = C.get(j), B = C.get(j + 1);
            for (int i = 0; i < 360 / step; i += 1) {
                try {
                    drawableSet.add(new Polygon3D(A.get(i), A.get((i + 1) % (360 / step)), B.get((i + 1) % (360 / step)), c));
                } catch (ImpossiblePolygonException ignored) {
                }
                try {
                    drawableSet.add(new Polygon3D(A.get(i), B.get(i), B.get((i + 1) % (360 / step)), c));
                } catch (ImpossiblePolygonException ignored) {
                }
            }
        }
        return drawableSet;
    }


    public static Collection<Polygon3D> drawCylinder(double x, double y, double z, double R, double h, int step) {
        Set<Polygon3D> drawableSet = new HashSet<>();
        java.util.List<Point3D> A = new ArrayList<>(), B = new ArrayList<>();
        for (int i = 0; i < 360; i += step) {
            A.add(new Point3D(x + R * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y + R * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z));
            B.add(new Point3D(x + R * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y + R * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z + h));
        }
        Random r = new Random();
        for (int i = 0; i < 360 / step; i += 1) {
            Color c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), 255);
            try {
                drawableSet.add(new Polygon3D(A.get(i), A.get((i + 1) % (360 / step)), B.get((i + 1) % (360 / step)), c));
            } catch (ImpossiblePolygonException ignored) {
            }
            try {
                drawableSet.add(new Polygon3D(A.get(i), B.get(i), B.get((i + 1) % (360 / step)), c));
            } catch (ImpossiblePolygonException ignored) {
            }
        }
        return drawableSet;
    }
}
