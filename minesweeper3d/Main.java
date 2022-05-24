package minesweeper3d;


import processing.core.PApplet;
import processing.event.MouseEvent;

public class Main extends PApplet{

    Camera cam;
    float longitude = PI/4;
    float latitude = PI/5;

    float distance = 1024;


    //last time a mouse button was pressed and released respectively in milliseconds
    int lastPressTime, lastReleaseTime;
    //start position after a mouse button has been pressed
    int lastMousePressedX, lastMousePressedY;

    int prevDifficulty = 1;

    Minesweeper game;

    public void settings() {
        size(800,800,P3D);
        cam = new Camera(this);

    }

    public void setup() {
        cam.setPolarPos(longitude, latitude, distance);
        game = new Minesweeper();

    }
    public void draw() {
        background(40);
        if(mousePressed && (mouseButton == LEFT || mouseButton == RIGHT)) {
            longitude+=(mouseX-pmouseX)*(PI/180);
            latitude+=(mouseY-pmouseY)*(PI/180);
            latitude = latitude > (PI/2-0.0001f) ? (PI/2-0.0001f) : latitude < -(PI/2-0.0001f) ? -(PI/2-0.0001f) : latitude;
            cam.setPolarPos(longitude, latitude, distance);
        }
        cam.draw();


        if(keyPressed) {
            if(key==CODED) {
                if(keyCode==UP) {
                    changeDistance(-20);
                }
                else if(keyCode==DOWN) {
                    changeDistance(20);
                }
            }
        }
        game.draw(this,cam);
    }

    public void mousePressed() {
        lastPressTime = millis();
        lastMousePressedX = mouseX;
        lastMousePressedY = mouseY;
    }
    public void mouseReleased() {
        //if mouse button was held for longer than 1000 milliseconds or mouse moved more than 5 pixels
        if(millis()-lastPressTime > 1500 || Math.abs(lastMousePressedX-mouseX)+Math.abs(lastMousePressedY-mouseY) > 5) {
            return;
        }else if(mouseButton==CENTER) {
            game.centerClick(mouseX, mouseY, cam, this);
        }else if(mouseButton==LEFT) {
            game.leftClick(mouseX, mouseY, cam, this);
        }else if(mouseButton==RIGHT) {
            game.rightClick(mouseX, mouseY, cam, this);
        }
        lastReleaseTime = millis();
    }

    public void mouseWheel(MouseEvent event) {
        changeDistance(event.getCount()*20);
    }


    private void changeDistance(float amount) {
        distance += amount;
        if(distance<1) distance = 1; //clamp distance
        cam.setPolarPos(longitude, latitude, distance);
    }

    public void keyPressed(){
        if(key==8){
            makeGame(-1);
        }else if(48<key&&key<52){
            makeGame(key-48);
        }
    }

    private void makeGame(int diff){
        if(diff==-1) makeGame(prevDifficulty);
        else if(diff==1){
            game = new Minesweeper();
            prevDifficulty=1;
        }else if(diff==2){
            game = new Minesweeper(10,10,10);
            prevDifficulty=2;
        }else if(diff==3){
            game = new Minesweeper(20,20,20);
            prevDifficulty = 3;
        }
    }

    public static void main(String[] args) {
        System.out.println("cursed 3d minesweeper. backspace to reset or 1-3 to set difficulty\n" +
                "color key:\n" +
                "blue = 1, " +
                "green = 2, " +
                "red = 3, " +
                "pink = 4, " +
                "yellow = 5, " +
                "cyan = 6, " +
                "black = suffer");
        PApplet.main("minesweeper3d.Main");
    }

}

