package graph;

import java.awt.*;

public class Pixel {
    double distance;
    Color color;
    public Pixel(double distance, Color color){
        this.distance = distance;
        this.color = color;
    }

    Pixel(){
        distance = Double.MAX_VALUE;
        this.color = Color.BLACK;
    }
}
