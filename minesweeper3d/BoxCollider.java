package minesweeper3d;

public class BoxCollider {

    //i really wish PApplet just had a vector structure...
    public float minx, miny, minz;

    public float maxx, maxy, maxz;

    public BoxCollider(float x1, float y1, float z1, float x2, float y2, float z2) {
        minx = x1;
        miny = y1;
        minz = z1;
        maxx = x2;
        maxy = y2;
        maxz = z2;
    }
    //https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-box-intersection
    boolean intersect(Ray r) {
        float tmin, tmax, tymin, tymax, tzmin, tzmax;
        if (r.getdirx() >= 0) {
            tmin = (minx - r.getorix()) / r.getdirx();
            tmax = (maxx - r.getorix()) / r.getdirx();
        }
        else {
            tmin = (maxx - r.getorix()) / r.getdirx();
            tmax = (minx - r.getorix()) / r.getdirx();
        }
        if (r.getdiry() >= 0) {
            tymin = (miny - r.getoriy()) / r.getdiry();
            tymax = (maxy - r.getoriy()) / r.getdiry();
        }
        else {
            tymin = (maxy - r.getoriy()) / r.getdiry();
            tymax = (miny - r.getoriy()) / r.getdiry();
        }
        if ((tmin > tymax) || (tymin > tmax))
            return false;
        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;
        if (r.getdirz() >= 0) {
            tzmin = (minz - r.getoriz()) / r.getdirz();
            tzmax = (maxz - r.getoriz()) / r.getdirz();
        }
        else {
            tzmin = (maxz - r.getoriz()) / r.getdirz();
            tzmax = (minz - r.getoriz()) / r.getdirz();
        }
        if ((tmin > tzmax) || (tzmin > tmax))
            return false;
        if (tzmin > tmin)
            tmin = tzmin;
        if (tzmax < tmax)
            tmax = tzmax;
        return true;
    }

}
