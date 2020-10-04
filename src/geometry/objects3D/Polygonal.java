package geometry.objects3D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Polygonal {
    private final Set<Polygon3D> polygons = new HashSet<>();

    Polygonal(Collection<Polygon3D> polygons){
        this.polygons.addAll(polygons);
    }

    public Collection<Polygon3D> getPolygons(){
        return polygons;
    }

}
