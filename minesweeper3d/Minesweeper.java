package minesweeper3d;

import processing.core.PApplet;

public class Minesweeper {
    private Timer time;
    private int flagsRemaining;
    private Gameboard board;
    private int numBombs;

    /** depth, num of rows, and num of columns respectively */
    private int dep, rows, cols;

    private Camera cam;

    private BoxCollider[][][] tileColliders;

    private int tileLen;

    /** the minimum x y and z cords of where board should be drawn, the board will always be centered at 0,0,0 */
    private int boardx, boardy, boardz;

    /** previously clicked tile depth, row, and column */
    private int td,tr,tc;


    public Minesweeper() {
        tileLen = 100;
        dep = 5;
        rows = 5;
        cols = 5;
        numBombs = 7;
        boardx=(int)(-(dep*tileLen)/2);
        boardy=(int)(-(rows*tileLen)/2);
        boardz=(int)(-(cols*tileLen)/2);
        board = new Gameboard(dep,rows,cols,numBombs);
        setupColliders();
    }
    public Minesweeper(int dep, int rows, int cols) {
        tileLen = 100;
        this.dep = dep;
        this.rows = rows;
        this.cols = cols;
        numBombs = (int)(0.05*dep*rows*cols);
        boardx=(int)(-(dep*tileLen)/2);
        boardy=(int)(-(rows*tileLen)/2);
        boardz=(int)(-(cols*tileLen)/2);
        setupColliders();
        board = new Gameboard(dep,rows,cols,numBombs);
    }

    public void draw(PApplet p, Camera cam) {
        //draws the box representing the last tile that was clicked
        p.pushMatrix();
        p.translate(tileColliders[td][tr][tc].minx+50,tileColliders[td][tr][tc].miny+50,tileColliders[td][tr][tc].minz+50);
        p.noFill();
        p.stroke(0);
        p.strokeWeight(2);
        p.box(100);
        p.popMatrix();

        board.draw(p, boardx, boardy, boardz, tileLen, cam);
    }

    public void leftClick(int x, int y, Camera cam, PApplet p) {
        int[] selectedTileLoc = screenposToTileLoc(x,y,cam,p,true);
        if(selectedTileLoc!=null) board.leftClick(selectedTileLoc[0],selectedTileLoc[1],selectedTileLoc[2]);
        if(board.isGameWon()) System.out.println("you won! backspace to reset or num 1-3 to set difficulty");
    }

    public void rightClick(int x, int y, Camera cam, PApplet p) {
        int[] selectedTileLoc = screenposToTileLoc(x,y,cam,p,true);
        if(selectedTileLoc!=null) board.rightClick(selectedTileLoc[0],selectedTileLoc[1],selectedTileLoc[2]);
    }

    public void centerClick(int x, int y, Camera cam, PApplet p) {
        int[] selectedTileLoc = screenposToTileLoc(x,y,cam,p,false);
        if(selectedTileLoc!=null) board.centerClick(selectedTileLoc[0],selectedTileLoc[1],selectedTileLoc[2]);
        if(board.isGameWon()) System.out.println("you won! backspace to reset or num 1-3 to set difficulty");
    }
    /** given a 2d mouse position, camera, and papplet, returns a 3d ray representing where the position would be in 3d space */
    private Ray mouseRay(int x, int y, Camera cam, PApplet p) {
        p.pushMatrix();
        //p.translate(cam.getX(),cam.getY(),cam.getZ());
        p.rotateY(-cam.getPolarLongitude());
        p.rotateZ(cam.getPolarLatitude());
        //untransformed x, y, z of mouse direction / mouse direction without taking into consideration camera position and rotation
        float untrx = 1000;
        float untry = 1000*((p.mouseY-p.height/2)/(float)p.height)*(1.16f);
        float untrz = 1000*((p.mouseX-p.width/2)/(float)p.width)*(1.16f);
        //transformed position
        float transx=p.modelX(untrx,untry,untrz);
        float transy=p.modelY(untrx,untry,untrz);
        float transz=p.modelZ(untrx,untry,untrz);
        p.popMatrix();
        Ray ret = new Ray(cam.getX(),cam.getY(),cam.getZ(),transx,transy,transz);
        return ret;
    }

    /**
     * converts a screen position in 2d to the location of the nearest tile that screen position "hovers over"
     * @param x - x position in pixels on the screen
     * @param y - y position in pixels on the screen
     * @param cam - camera instance
     * @param p - PApplet instance
     * @return
     */
    private int[] screenposToTileLoc(int x, int y, Camera cam, PApplet p, boolean ignoreHints) {
        //sob
        float nearestDist = 1000000;
        //location in depth, rows, columns
        int[] nearestTileLoc = null;
        Ray ray = mouseRay(x,y,cam,p);
        for(int d = 0; d<tileColliders.length; d++) {
            for(int r = 0; r<tileColliders[0].length; r++) {
                for(int c = 0; c<tileColliders[0][0].length; c++) {
                    //System.out.println(board.tileIsHidden(d, r, c));
                    if(tileColliders[d][r][c]!=null && board.tileCollidable(d, r, c, ignoreHints) && dist(depToX(d),rowToY(r),colToZ(c),(int)cam.getX(),(int)cam.getY(),(int)cam.getZ())<nearestDist) { //dist(depToX(d),rowToY(r),colToZ(c),(int)cam.getX(),(int)cam.getY(),(int)cam.getZ())<nearestDist &&
                        //System.out.println("ahhh");
                        if(tileColliders[d][r][c].intersect(ray)) {
                            nearestDist = dist(depToX(d),rowToY(r),colToZ(c),(int)cam.getX(),(int)cam.getY(),(int)cam.getZ());
                            nearestTileLoc = new int[3];
                            nearestTileLoc[0]=d;
                            nearestTileLoc[1]=r;
                            nearestTileLoc[2]=c;
                            td=d;
                            tr=r;
                            tc=c;
                            //System.out.println(d+", "+r+", "+c + " dist: "+nearestDist);
                        }
                    }
                }
            }
        }
        return nearestTileLoc;
    }
    private float dist(int x1, int y1, int z1, int x2, int y2, int z2) {
        return (float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)+(z2-z1)*(z2-z1));
    }
    private int depToX(int d) {
        return d*tileLen+boardx;
    }
    private int rowToY(int r) {
        return r*tileLen+boardy;
    }
    private int colToZ(int c) {
        return c*tileLen+boardz;
    }

    private void setupColliders() {
        tileColliders = new BoxCollider[dep][rows][cols];
        for(int d = 0; d<dep; d++) {
            for(int r = 0; r<rows; r++) {
                for(int c = 0; c<cols; c++) {
                    tileColliders[d][r][c] = new BoxCollider(depToX(d), rowToY(r), colToZ(c), depToX(d)+tileLen, rowToY(r)+tileLen, colToZ(c)+tileLen);
                }
            }
        }
    }

}
