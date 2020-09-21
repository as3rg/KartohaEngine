package com.guryanov_sergeev;

import graph.Camera;
import graph.PaintersAlgorithmCanvas;
import utils.Point3D;
import utils.Polygon3D;
import utils.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static final int FocusLength = 300;
    public static final int Distance = 500;
    public static void main(String[] args) {
        int angle = 45;
        Camera.Resolution resolution = new Camera.Resolution(1280, 720);
        Camera camera = new Camera(resolution, new Point3D(-Distance,-Distance, 100), new Vector3D(FocusLength,20, 20), 0);
        PaintersAlgorithmCanvas canvas = new PaintersAlgorithmCanvas(camera);

//        Point3D A = new Point3D(-50, -50, -100),
//                B = new Point3D(-50, -50, -200),
//                C = new Point3D(-50, 50, -100),
//                D = new Point3D(-50, 50, -200),
//                A2 = new Point3D(50, -50, -100),
//                B2 = new Point3D(50, -50, -200),
//                C2 = new Point3D(50, 50, -100),
//                D2 = new Point3D(50, 50, -200);
//
//
//
//        canvas.getPolygons().add(new Polygon3D(A, B, C, Color.RED));
//        canvas.getPolygons().add(new Polygon3D(A, D, C, Color.RED));
//
//        canvas.getPolygons().add(new Polygon3D(A, B, B2, Color.GREEN));
//        canvas.getPolygons().add(new Polygon3D(A, A2, B, Color.GREEN));
//
//        canvas.getPolygons().add(new Polygon3D(D, B, B2, Color.BLUE));
//        canvas.getPolygons().add(new Polygon3D(D, B2, D2, Color.BLUE));
//        canvas.getPolygons().add(new Polygon3D(B, B2, D2, Color.BLUE));
//
//        canvas.getPolygons().add(new Polygon3D(D, D2, C2, Color.YELLOW));
//        canvas.getPolygons().add(new Polygon3D(D2, D, C, Color.YELLOW));
//
//        canvas.getPolygons().add(new Polygon3D(A, A2, C, Color.ORANGE));
//        canvas.getPolygons().add(new Polygon3D(A, C, C2, Color.ORANGE));
//
//        canvas.getPolygons().add(new Polygon3D(A2, B2, C2, Color.LIGHT_GRAY));
//        canvas.getPolygons().add(new Polygon3D(new Point3D(0,0,0), new Point3D(0,0,50), new Point3D(0,50,50), Color.LIGHT_GRAY));
        java.util.List<java.util.List<Point3D>> C = new ArrayList<>();
        int R = 100;
        int step = 20;
        for (int j = 0; j < 360; j+= step) {
            List<Point3D> B = new ArrayList<>();
            for (int i = 0; i < 360; i += step) {
                B.add(new Point3D(100+R * utils.Math.destroyMinusZeros(Math.sin(j * Math.PI / 180)) * utils.Math.destroyMinusZeros(Math.cos(i * Math.PI / 180)), 100+R * utils.Math.destroyMinusZeros(Math.sin(j * Math.PI / 180)) * utils.Math.destroyMinusZeros(Math.sin(i * Math.PI / 180)), R * utils.Math.destroyMinusZeros(Math.cos(j * Math.PI / 180))));
            }
            C.add(B);
        }
        System.out.println(360*360/step/step+" points");
        Random r = new Random();
        for(int j = 0; j < 360/step -1; j++) {
            List<Point3D> A = C.get(j), B = C.get(j+1);
            for (int i = 0; i < 360/step; i += 1) {
                Color c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), r.nextInt(256));
                canvas.getPolygons().add(new Polygon3D(A.get(i), B.get(i), A.get((i + 1) % (360/step)), c));
                canvas.getPolygons().add(new Polygon3D(A.get(i), B.get(i), B.get((i + 1) % (360/step)), c));
            }
        }
        System.out.println(2*360*360/step/step+" polygons");

        JFrame frame = new JFrame();

        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
//        frame.setFocusable(true);
        frame.setUndecorated(true);
        frame.setVisible(true);

//        new Thread(()-> {
//            for (int i = 0; ;i = (i+1)%360 ) {
//                canvas.getPolygons().clear();
//                canvas.getPolygons().add(new Polygon3D(new Point3D(0,0,0), new Point3D(0,0,50), new Point3D(50*utils.Math.destroyMinusZeros(Math.cos(2*i * Math.PI / 180)),50*utils.Math.destroyMinusZeros(Math.sin(2*i * Math.PI / 180)),50), Color.LIGHT_GRAY));
//                camera.setFocus(new Point3D(-Distance * utils.Math.destroyMinusZeros(Math.cos(i * Math.PI / 180)), -Distance * utils.Math.destroyMinusZeros(Math.sin(i * Math.PI / 180)), 100));
//                camera.setVector(new Vector3D(FocusLength * utils.Math.destroyMinusZeros(Math.cos(i * Math.PI / 180)), FocusLength * utils.Math.destroyMinusZeros(Math.sin(i * Math.PI / 180)), -150));
//                System.out.println(i);
//                camera.setRotateAngle(Math.PI * i / 180);
//                System.out.printf("%.2f %.2f\n",FocusLength * utils.Math.destroyMinusZeros(java.lang.Math.cos(2*i * java.lang.Math.PI / 180)), FocusLength * utils.Math.destroyMinusZeros(java.lang.Math.sin(2*i * java.lang.Math.PI / 180)));
//                canvas.repaint();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
}
