package minesweeper3d;

import processing.core.PApplet;

public class Gameboard {

    private Tile[][][] board;
    private int[][][] adjecentRevealed;
    private int[][][] adjecentFlags;

    private boolean gameStarted;
    private int numBombs;
    private int numRevealedTiles;

    private int colors[] = {255,-16776961,-16711936,-65536,-65281,-256,-16711681,1,20,40,60,80,100,120,140,160};

    public Gameboard(int dep, int rows, int cols, int bombs) {
        board = new Tile[dep][rows][cols];
        adjecentRevealed = new int[dep][rows][cols];
        adjecentFlags = new int[dep][rows][cols];
        for(int d = 0; d<dep; d++) {
            for(int r = 0; r<rows; r++) {
                for(int c = 0; c<cols; c++) {
                    board[d][r][c] = new Tile();
                    adjecentRevealed[d][r][c] = 0;
                    if(c==0||c==cols-1) adjecentRevealed[d][r][c] += (9-(float)adjecentRevealed[d][r][c]/3.0f);
                    if(r==0||r==rows-1) adjecentRevealed[d][r][c] += (9-(float)adjecentRevealed[d][r][c]/3.0f);
                    if(d==0||d==dep-1) adjecentRevealed[d][r][c] += (9-(float)adjecentRevealed[d][r][c]/3.0f);
                }
            }
        }
        gameStarted = false;
        numBombs = bombs;
        numRevealedTiles = 0;
    }

    public int depth() {
        return board.length;
    }
    public int rows() {
        return board[0].length;
    }
    public int cols() {
        return board[0][0].length;
    }

    public void draw(PApplet p, int x, int y, int z, int tileLen, Camera cam) {
        float camLongitude = cam.getPolarLongitude();
        float camLatitude = cam.getPolarLatitude();
        p.textAlign(PApplet.CENTER,PApplet.CENTER);
        p.translate(x+tileLen/2, y+tileLen/2, z+tileLen/2);
        for(int d = 0; d<depth(); d++) {
            for(int r = 0; r<rows(); r++) {
                for(int c = 0; c<cols(); c++) {
                    drawTile(p,board[d][r][c],tileLen,d+r+c,adjecentFlags[d][r][c],adjecentRevealed[d][r][c],camLongitude,camLatitude);
                    p.translate(0, 0, tileLen);
                }
                p.translate(0, tileLen, -tileLen*(cols()));
            }
            p.translate(tileLen, -tileLen*(rows()),0);
        }
        p.resetMatrix();
    }
    /**
     * draws a tile, assuming that the position was already set by p.translate
     * @param p - papplet instance
     * @param tile - tile instance
     * @param tileLen - side length of tile cube
     * @param iter - what # tile this is, only used to create a checkerboard pattern
     * @param adjRevealed - number of adjacent tiles that have been revealed, used to hide tiles you already "solved"
     */
    private void drawTile(PApplet p, Tile tile, int tileLen, int iter, int adjFlags, int adjRevealed, float camrotlong, float camrotlat) {
        if(tile.isHidden()) {
            p.noStroke();
            if (iter%2==0) p.fill(60); else p.fill(80);
            p.box(tileLen);
        }else if(tile.isRevealed()&&tile.getHint()>0&&(tile.getHint()!=adjFlags||26-tile.getHint()!=adjRevealed)) { //tile.getHint()!=adjFlags
            p.noStroke();
            p.fill(colors[tile.getHint()]);
            p.box(tileLen/3);
        }else if(tile.isFlagged()) {
            p.noStroke();
            p.fill(200,0,0);
            p.box(tileLen);
        }else if(tile.isBomb()) {
            p.noStroke();
            p.fill(0);
            p.box(tileLen);
        }
    }
    public boolean tileNotDrawn(int dep, int row, int col) {
        return board[dep][row][col].isRevealed() && (board[dep][row][col].getHint()==0 || (board[dep][row][col].getHint()==adjecentFlags[dep][row][col]&&26-board[dep][row][col].getHint()==adjecentRevealed[dep][row][col]));
    }
    public boolean tileIsRevealed(int dep, int row, int col) {
        return board[dep][row][col].isRevealed();
    }
    public boolean tileCollidable(int dep, int row, int col, boolean ignoreHints) {
        return !board[dep][row][col].isRevealed() ||  !ignoreHints && !(board[dep][row][col].getHint()==0 || (board[dep][row][col].getHint()==adjecentFlags[dep][row][col]&&26-board[dep][row][col].getHint()==adjecentRevealed[dep][row][col]));
    }


    private void placeBombs(int bombs, int dep, int row, int col) {
        int bombsPlaced = 0;
        int iter = 0;
        int numTiles = depth()*rows()*cols();
        while (bombsPlaced < bombs) {
            int randdep = (int)(Math.random()*depth());
            int randrow = (int)(Math.random()*rows());
            int randcol = (int)(Math.random()*cols());
            if(!(Math.abs(randdep-dep)<2 && Math.abs(randrow-row)<2 && Math.abs(randcol-col)<2)) {
                boolean placed = placeBomb(randdep,randrow,randcol);
                if(placed) bombsPlaced++;
            }
            iter++;
            if(iter>2*numTiles) {
                System.err.println("Could not place all tiles");
                return;
            }
        }
    }
    /**
     * places a bomb at a position on the board
     * @param dep - depth to place the bomb at
     * @param row - row the place the bomb at
     * @param col - column to place the bomb at
     * @return
     */
    private boolean placeBomb(int dep, int row, int col) {
        if(board[dep][row][col].isBomb()||board[dep][row][col].isRevealed()) return false;
        board[dep][row][col] = new Tile(true);
        for(int d = dep-1; d<=dep+1; d++) {
            for(int r = row-1; r<=row+1; r++) {
                for(int c = col-1; c<=col+1; c++) {
                    if(0<=d&&d<depth() && 0<=r&&r<rows() && 0<=c&&c<cols() && !(d==dep&&r==row&&c==col)) board[d][r][c].setHint(board[d][r][c].getHint()+1);
                }
            }
        }
        return true;
    }

    public void leftClick(int dep, int row, int col) {
        if(!gameStarted) {
            gameStarted = true;
            placeBombs(numBombs,dep,row,col);
        }
        revealTile(dep,row,col);
    }

    public void rightClick(int dep, int row, int col) {
        toggleFlag(dep,row,col);
    }

    public void centerClick(int dep, int row, int col) {
        if(board[dep][row][col].isRevealed() && adjecentFlags[dep][row][col]==board[dep][row][col].getHint())
            for(int d = dep-1; d<=dep+1; d++)
                for(int r = row-1; r<=row+1; r++)
                    for(int c = col-1; c<=col+1; c++)
                        if(0<=d&&d<depth() && 0<=r&&r<rows() && 0<=c&&c<cols() && !(d==dep&&r==row&&c==col)) revealTile(d,r,c);
    }

    public boolean isGameOver() {
        return isGameWon() || isGameLost();
    }

    public boolean isGameWon() {
        return depth()*rows()*cols()-numRevealedTiles==numBombs;
    }

    public boolean isGameLost() {
        return false;
    }

    public boolean hasGameStarted() {
        return gameStarted;
    }


    private boolean toggleFlag(int dep, int row, int col) {
        Tile t = board[dep][row][col];
        if(t.isRevealed()) return false;
        t.toggleFlag();
        for(int d = dep-1; d<=dep+1; d++)
            for(int r = row-1; r<=row+1; r++)
                for(int c = col-1; c<=col+1; c++)
                    if(0<=d&&d<depth() && 0<=r&&r<rows() && 0<=c&&c<cols() && !(d==dep&&r==row&&c==col)) adjecentFlags[d][r][c]+=t.isFlagged() ? 1 : -1;
        return true;
    }

    private boolean revealTile(int dep, int row, int col) {
        Tile t = board[dep][row][col];
        if(t.isFlagged()||t.isRevealed()) return false;
        t.reveal();
        if(t.isBomb()) System.out.println("you lost, backspace to reset");
        else numRevealedTiles++;
        if(t.getHint()==0) {
            for(int d = dep-1; d<=dep+1; d++)
                for(int r = row-1; r<=row+1; r++)
                    for(int c = col-1; c<=col+1; c++)
                        if(0<=d&&d<depth() && 0<=r&&r<rows() && 0<=c&&c<cols() && !(d==dep&&r==row&&c==col)) {
                            revealTile(d,r,c);
                            adjecentRevealed[d][r][c]+=1;
                        }
            return true;
        }
        for(int d = dep-1; d<=dep+1; d++)
            for(int r = row-1; r<=row+1; r++)
                for(int c = col-1; c<=col+1; c++)
                    if(0<=d&&d<depth() && 0<=r&&r<rows() && 0<=c&&c<cols() && !(d==dep&&r==row&&c==col))
                        adjecentRevealed[d][r][c]+=1;
        return true;
    }
}
