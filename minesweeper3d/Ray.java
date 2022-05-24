package minesweeper3d;

public class Ray {
    /** origin of this ray */
    private float orix;
    private float oriy;
    private float oriz;

    /** direction of this ray */
    private float dirx;
    private float diry;
    private float dirz;


    public Ray(float originX, float originY, float originZ, float directionX, float directionY, float directionZ) {
        orix = originX;
        oriy = originY;
        oriz = originZ;
        if(Math.sqrt(directionX*directionX+directionY*directionY+directionZ*directionZ)>1) {
            float mag = (float) Math.sqrt(directionX*directionX+directionY*directionY+directionZ*directionZ);
            dirx = directionX/mag;
            diry = directionY/mag;
            dirz = directionZ/mag;
        }else {
            dirx = directionX;
            diry = directionY;
            dirz = directionZ;
        }
    }

    public float getorix() {
        return orix;
    }
    public float getoriy() {
        return oriy;
    }
    public float getoriz() {
        return oriz;
    }
    public float getdirx() {
        return dirx;
    }
    public float getdiry() {
        return diry;
    }
    public float getdirz() {
        return dirz;
    }

}
