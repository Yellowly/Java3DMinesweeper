package minesweeper3d;

public class Tile {

    private enum State{
        HIDDEN,
        FLAGGED,
        REVEALED
    }
    /** value of this tile, -1 = bomb, 0..8 = number of adjacent bombs. the hint cannot be set if this tile is a bomb */
    private int value;

    private State state; // HIDDEN, FLAGGED, REVEALED


    /** constructor for a non-bomb tile */
    public Tile() {
        value = 0;
        state = State.HIDDEN;
    }
    /** constructor for a bomb tile, the value of a bomb tile is immutable and always -1 */
    public Tile(boolean isBomb) {
        value = isBomb? -1 : 0;
        state=State.HIDDEN;
    }
    /** sets and returns the hint of this tile, -1 if it's a bomb and the hint can't be set */
    public int setHint(int hint) {
        return value = (value == -1) ? -1 : hint;
    }

    /** returns the value of this tile, -1 if it's a bomb */
    public int getHint() {
        return value;
    }

    public boolean isRevealed() {
        return state == State.REVEALED;
    }

    public boolean isBomb() {
        return value == -1;
    }

    public boolean isHidden() {
        return state == State.HIDDEN;
    }

    boolean isFlagged() {
        return state==State.FLAGGED;
    }

    public boolean isExploded() {
        return isBomb() && isRevealed();
    }

    /**
     * reveals the tile--return T if
     * it was hidden return
     * F otherwise
     */
    public boolean reveal() {
        if(state == State.HIDDEN) {
            state = State.REVEALED;
            return true;
        }
        return false;
    }

    /**
     * HIDDEN -> FLAGGED FLAGGED -> HIDDEN REVEALED -> REVEALED
     */
    public boolean toggleFlag() {
        if(state==State.REVEALED)
            return false;
        state = (state==State.HIDDEN) ? State.FLAGGED : State.HIDDEN;
        return true;
    }


}
