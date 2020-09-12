package com.guryanov_sergeev;

import graph.Camera;
import graph.PaintersAlgorithmCanvas;
import utils.Point3D;
import utils.Polygon3D;
import utils.Vector3D;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static final int FocusLength = 200;
    public static final int Distance = 400;
    public static void main(String[] args) {
        Camera.Resolution resolution = new Camera.Resolution(1280, 720);
        Camera camera = new Camera(resolution, new Point3D(Distance,0, 0), new Vector3D(-FocusLength, 0, 0), 0);

        PaintersAlgorithmCanvas canvas = new PaintersAlgorithmCanvas(camera);

        Point3D A = new Point3D(-50, -50, -100),
                B = new Point3D(-50, -50, -200),
                C = new Point3D(-50, 50, -100),
                D = new Point3D(-50, 50, -200),
                A2 = new Point3D(50, -50, -100),
                B2 = new Point3D(50, -50, -200),
                C2 = new Point3D(50, 50, -100),
                D2 = new Point3D(50, 50, -200);

        canvas.getPolygons().add(new Polygon3D(A, B, C, Color.RED));
        canvas.getPolygons().add(new Polygon3D(A, D, C, Color.RED));

        canvas.getPolygons().add(new Polygon3D(A, B, B2, Color.GREEN));
        canvas.getPolygons().add(new Polygon3D(A, A2, B, Color.GREEN));

        canvas.getPolygons().add(new Polygon3D(D, B, B2, Color.BLUE));
        canvas.getPolygons().add(new Polygon3D(D, B2, D2, Color.BLUE));
        canvas.getPolygons().add(new Polygon3D(B, B2, D2, Color.BLUE));

        canvas.getPolygons().add(new Polygon3D(D, D2, C2, Color.YELLOW));
        canvas.getPolygons().add(new Polygon3D(D2, D, C, Color.YELLOW));

        canvas.getPolygons().add(new Polygon3D(A, A2, C, Color.ORANGE));
        canvas.getPolygons().add(new Polygon3D(A, C, C2, Color.ORANGE));

        canvas.getPolygons().add(new Polygon3D(A2, B2, C2, Color.LIGHT_GRAY));
        canvas.getPolygons().add(new Polygon3D(A2, D2, C2, Color.LIGHT_GRAY));

        JFrame frame = new JFrame();

        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setVisible(true);


        new Thread(()-> {
            for (int i = 0; ;i++ ) {
                camera.setFocus(new Point3D(-Distance * utils.Math.destroyMinusZeros(Math.cos(2*i * Math.PI / 180)), -Distance * utils.Math.destroyMinusZeros(Math.sin(2*i * Math.PI / 180)), 0));
                camera.setVector(new Vector3D(FocusLength * utils.Math.destroyMinusZeros(Math.cos(2*i * Math.PI / 180)), FocusLength * utils.Math.destroyMinusZeros(Math.sin(2*i * Math.PI / 180)), 0));
//                camera.setRotateAngle(Math.PI * i / 180);
                canvas.repaint();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
