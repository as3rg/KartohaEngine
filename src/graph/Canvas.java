package graph;

import geometry.objects2D.Point2D;
import geometry.objects2D.Vector2D;
import geometry.objects3D.Point3D;

public class Canvas {

    private final Camera.Resolution resolution;
    private final Pixel[][] canvas;
    Canvas(Camera.Resolution resolution){
        this.resolution = resolution;
        canvas = new Pixel[(int) resolution.width][(int) resolution.height];
        for(int i = 0; i < canvas.length; i++){
            for(int j = 0; j < canvas[i].length; j++){
                canvas[i][j] = new Pixel();
            }
        }
    }

    public Camera.Resolution getResolution() {
        return resolution;
    }

    public Pixel get(int i, int j){
        if(i < 0 || j < 0 || i >= resolution.width || j >= resolution.height){
            return new Pixel();
        }
        return canvas[i][j];
    }

    public void set(int i, int j, Pixel pixel){
        if(i < 0 || j < 0 || i >= resolution.width || j >= resolution.height){
            return;
        }
        if(pixel.distance < get(i,j).distance) canvas[i][j] = pixel;
    }
}
