package com.guryanov_sergeev;

import graph.Camera;
import graph.MyCanvas;
import utils.Point3D;
import utils.Polygon3D;
import utils.Vector3D;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        Camera.Resolution resolution = new Camera.Resolution(1280, 720);
        Camera camera = new Camera(resolution, new Point3D(-400,0, 0), new Vector3D(200, 0, 0), 0);

        MyCanvas canvas = new MyCanvas(camera);

        Point3D A = new Point3D(0, 0, 100),
                B = new Point3D(0, 0, -100),
                C = new Point3D(0, 200, 100),
                D = new Point3D(0, 200, -100),
                A2 = new Point3D(200, 0, 100),
                B2 = new Point3D(200, 0, -100),
                C2 = new Point3D(200, 200, 100),
                D2 = new Point3D(200, 200, -100);

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

        frame.add(canvas);
        new Thread(()-> {
            for (int i = 0; ; i++) {
                camera.setFocus(new Point3D(-400 * Math.cos(i * Math.PI / 180), -400 * Math.sin(i * Math.PI / 180), 0));
                camera.setVector(new Vector3D(200 * Math.cos(i * Math.PI / 180), 200 * Math.sin(i * Math.PI / 180), 0));
                camera.setRotateAngle(Math.PI * i / 180);
                canvas.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
