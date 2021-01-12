package com.guryanov_sergeev;

import geometry.objects3D.Point3D;
import geometry.objects3D.Polygon3D;
import geometry.objects3D.Vector3D;
import graph.Camera;
import graph.CanvasPanel;
import graph.Drawable;
import graph.Screen;
import utils.throwables.ImpossiblePolygonException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class Main {

    public static final int FocusLength = 300;
    public static final int Distance = 500;
    public static void main(String[] args) {
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


        canvas.prepare();
        canvas.setSize(1920, 1080);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setLocationRelativeTo(null);
        canvas.setResizable(false);
        canvas.setUndecorated(true);
        canvas.setVisible(true);
        new Thread(()->{
            while (true) {
                canvas.repaint();
            }
        }).start();
    }

    public static Collection<Drawable> drawSphere(double x, double y, double z, double R, int step, Color c){
        Set<Drawable> drawableSet = new HashSet<>();
        java.util.List<java.util.List<Point3D>> C = new ArrayList<>();
        for (int j = 0; j < 360; j+= step) {
            List<Point3D> B = new ArrayList<>();
            for (int i = 0; i < 360; i += step) {
                B.add(new Point3D(x+R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y+R * utils.Math.roundNearZero(Math.sin(j * Math.PI / 180)) * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z+R * utils.Math.roundNearZero(Math.cos(j * Math.PI / 180))));
            }
            C.add(B);
        }
        for(int j = 0; j < 180/step -1; j++) {
            List<Point3D> A = C.get(j), B = C.get(j+1);
            for (int i = 0; i < 360/step; i += 1) {
                try{ drawableSet.add(new Polygon3D(A.get(i), A.get((i + 1) % (360/step)), B.get((i + 1) % (360/step)), c)); } catch (ImpossiblePolygonException ignored){}
                try{ drawableSet.add(new Polygon3D(A.get(i), B.get(i), B.get((i + 1) % (360/step)), c)); } catch (ImpossiblePolygonException ignored){}
            }
        }
        return drawableSet;
    }



    public static Collection<Drawable> drawCylinder(double x, double y, double z, double R, double h, int step){
        Set<Drawable> drawableSet = new HashSet<>();
        java.util.List<Point3D> A = new ArrayList<>(),B = new ArrayList<>();
        for (int i = 0; i < 360; i += step) {
            A.add(new Point3D(x+R  * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y+R * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z));
            B.add(new Point3D(x+R  * utils.Math.roundNearZero(Math.cos(i * Math.PI / 180)), y+R * utils.Math.roundNearZero(Math.sin(i * Math.PI / 180)), z+h));
        }
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
        return drawableSet;
    }
}
