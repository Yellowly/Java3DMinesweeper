package minesweeper3d;

import processing.core.PApplet;

public class Camera {

    private float x;
    private float y;
    private float z;

    private PApplet p;

    public Camera(PApplet p) {
        this.x=0;
        this.y=0;
        this.z=0;
        this.p=p;
    }
    /**
     * sets position of camera to x, y, z
     * @param x
     * @param y
     * @param z
     */
    public void setPos(float x, float y, float z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    /**
     * sets position based off polar coordinates
     * @param longitude - radians horizontally around a sphere
     * @param latitude - radians vertically up and down a sphere
     * @param dist - distance from 0,0,0 the camera should be placed at
     */
    public void setPolarPos(float longitude, float latitude, float dist) {
        setPos((-dist*PApplet.cos(longitude)*PApplet.cos(latitude)), (-dist*PApplet.sin(latitude)), (-dist*PApplet.sin(longitude)*PApplet.cos(latitude)));
    }
    public float getPolarLongitude() {
        return z == 0? PApplet.PI/2 : PApplet.atan(z/x) + ((x>0) ? PApplet.PI : 0);
    }
    public float getPolarLatitude() {
        return -PApplet.asin(y/PApplet.sqrt(x*x+y*y+z*z));
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getZ() {
        return z;
    }

    public void draw() {
        p.camera(x, y, z, 0, 0, 0, 0, 1, 0);
    }
}