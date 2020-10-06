package graph;

import geometry.objects3D.Object3D;
import geometry.objects3D.Polygon3D;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;

public interface Drawable extends Object3D {

    void draw(Graphics g, Camera camera);
    default Collection<Drawable> split(Drawable drawable){
        return Collections.singleton(this);
    }

    int compareZ(Camera camera, Drawable drawable);
}
