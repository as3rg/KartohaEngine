package utils;

import com.sun.istack.internal.Nullable;
import utils.Objects3D.Object3D;
import utils.Objects3D.Point3D;
import utils.Objects3D.Region3D;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SearchTree3D {

    SearchTree3D(Point3D a, Point3D b){
        low = new Point3D(min(a.x, b.x), min(a.y, b.y), min(a.z, b.z));
        high = new Point3D(max(a.x, b.x), max(a.y, b.y), max(a.z, b.z));

        if((high.x-low.x)*(high.y-low.y)*(high.z-low.z) <= 0.25){
            isLeaf = true;
            isInited = true;
        }else {
            isLeaf = false;
            isInited = false;
        }
    }

    private final Point3D low, high;
    private int count = 0;
    private final boolean isLeaf;
    private boolean isInited;
    private final Set<Object3D> objects = new HashSet<>();
    private @Nullable SearchTree3D lll, llh, lhl, lhh, hll, hlh, hhl, hhh;

    public void add(Object3D o){
        if (o.getHighPoint().x < low.x
                || o.getHighPoint().y < low.y
                || o.getHighPoint().z < low.z
                || o.getLowPoint().x > high.x
                || o.getLowPoint().y > high.y
                || o.getLowPoint().z > high.z){
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
        if (o.getHighPoint().x < low.x
                || o.getHighPoint().y < low.y
                || o.getHighPoint().z < low.z
                || o.getLowPoint().x > high.x
                || o.getLowPoint().y > high.y
                || o.getLowPoint().z > high.z){
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
        if (r.getHighPoint().x < low.x
                || r.getHighPoint().y < low.y
                || r.getHighPoint().z < low.z
                || r.getLowPoint().x > high.x
                || r.getLowPoint().y > high.y
                || r.getLowPoint().z > high.z
                || !isInited) {
            return new HashSet<>();
        }
        if(isLeaf){
            Set<Object3D> res = new HashSet<>();
            for (Object3D o : objects){
                if (r.partiallyContains(o)){
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
        Point3D center = new Point3D((low.x + high.x) / 2.0, (low.y + high.y) / 2.0, (low.z + high.z) / 2.0);
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
