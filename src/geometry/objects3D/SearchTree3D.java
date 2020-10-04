package geometry.objects3D;

import com.sun.istack.internal.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SearchTree3D {

    SearchTree3D(Point3D a, Point3D b){
        region = new Region3D(new Point3D(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z)), new Point3D(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z)));

        Point3D high = region.high, low = region.low;
        if((high.x-low.x)*(high.y-low.y)*(high.z-low.z) <= 0.25){
            isLeaf = true;
            isInited = true;
        }else {
            isLeaf = false;
            isInited = false;
        }
    }

    SearchTree3D(Collection<Object3D> collection){
        double minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for(Object3D object3D : collection){
            minX = min(minX, object3D.getRegion().low.x);
            minY = min(minY, object3D.getRegion().low.y);
            minZ = min(minZ, object3D.getRegion().low.z);

            maxX = max(maxX, object3D.getRegion().high.x);
            maxY = max(maxY, object3D.getRegion().high.y);
            maxZ = max(maxZ, object3D.getRegion().high.z);
        }

        region = new Region3D(new Point3D(minX, minY, minZ), new Point3D(maxX, maxY, maxZ));

        if((maxX-minX)*(maxY-minY)*(maxZ-minZ) <= 0.25){
            isLeaf = true;
            isInited = true;
        }else {
            isLeaf = false;
            isInited = false;
        }

        for(Object3D object3D : collection){
            add(object3D);
        }
    }

    private final Region3D region;
    private int count = 0;
    private final boolean isLeaf;
    private boolean isInited;
    private final Set<Object3D> objects = new HashSet<>();
    private @Nullable SearchTree3D lll, llh, lhl, lhh, hll, hlh, hhl, hhh;

    public void add(Object3D o){
        if (!region.crosses(o.getRegion())){
            return;
        }
        count++;

        if(isLeaf){
            objects.add(o);
        }else {
            init();
            lll.add(o);
            llh.add(o);
            lhl.add(o);
            lhh.add(o);
            hll.add(o);
            hlh.add(o);
            hhl.add(o);
            hhh.add(o);
        }
    }

    public void remove(Object3D o){
        if (!region.crosses(o.getRegion())){
            return;
        }
        count = max(0, count-1);

        if(isLeaf){
            objects.remove(o);
        }else if (count == 0){
            deinit();
        }else {
            lll.remove(o);
            llh.remove(o);
            lhl.remove(o);
            lhh.remove(o);
            hll.remove(o);
            hlh.remove(o);
            hhl.remove(o);
            hhh.remove(o);
        }
    }

    public int getCount() {
        return count;
    }

    public Set<Object3D> get(Region3D r) {
        if (!region.crosses(r) || !isInited) {
            return new HashSet<>();
        }
        if(isLeaf){
            Set<Object3D> res = new HashSet<>();
            for (Object3D o : objects){
                if (r.crosses(o)){
                    res.add(o);
                }
            }
            return res;
        }

        Set<Object3D> res = new HashSet<>();
        res.addAll(lll.get(r));
        res.addAll(llh.get(r));
        res.addAll(lhl.get(r));
        res.addAll(lhh.get(r));
        res.addAll(hll.get(r));
        res.addAll(hlh.get(r));
        res.addAll(hhl.get(r));
        res.addAll(hhh.get(r));
        return res;
    }

    private void init(){
        if (isInited)
            return;

        Point3D high = region.high,
                low = region.low,
                center = new Point3D((low.x + high.x) / 2.0, (low.y + high.y) / 2.0, (low.z + high.z) / 2.0);
        lll = new SearchTree3D(low, center);
        hhh = new SearchTree3D(center, high);
        llh = new SearchTree3D(new Point3D(low.x, low.y, high.z), center);
        lhl = new SearchTree3D(new Point3D(low.x, high.y, low.z), center);
        lhh = new SearchTree3D(new Point3D(low.x, high.y, high.z), center);
        hll = new SearchTree3D(new Point3D(high.x, low.y, low.z), center);
        hlh = new SearchTree3D(new Point3D(high.x, low.y, high.z), center);
        hhl = new SearchTree3D(new Point3D(high.x, high.y, low.z), center);
        isInited = true;
    }

    private void deinit(){
        if (!isInited)
            return;
        lll = null;
        hhh = null;
        llh = null;
        lhl = null;
        lhh = null;
        hll = null;
        hlh = null;
        hhl = null;
        isInited = false;
    }
}
