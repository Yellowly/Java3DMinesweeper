package minesweeper3d;

import processing.core.PApplet;

public class Timer {
    /** Use the processing command millis () */
    private int startTime;
    /** */
    private int stopTime;

    private PApplet p;
    public Timer(PApplet p) {
        startTime = -1;
        stopTime = -1;
        this.p = p;
    }

    /**
     *
     * @return
     */
    public int getTime () {
        return startTime == -1 ? 0 : stopTime == -1 ? (p.millis()-startTime)/1000 : (stopTime-startTime)/1000;
    }

    /**
     *
     */
    public int start () {
        return this.startTime = p.millis();
    }

    /**
     *
     */
    public int stop () {
        return this.stopTime = p.millis();
    }
}

