package model;

public class Player {
    private Cave currentCave;
    private int arrows;

    // Constructor to initialize player with starting cave and arrow count
    public Player(Cave startCave, int arrowCount) {
        this.currentCave = startCave;
        this.arrows = arrowCount;
    }

    // Getter for current cave
    public Cave getCurrentCave() {
        return currentCave;
    }

    // Getter for number of arrows
    public int getArrows() {
        return arrows;
    }

    // Setter for current cave (if you prefer the 'setCurrentCave' method)
    public void setCurrentCave(Cave newCave) {
        this.currentCave = newCave;
    }

    // Move to a new cave (Alternative way to update current cave)
    public void moveToCave(Cave newCave) {
        this.currentCave = newCave;
    }

    // Shoot an arrow if there are arrows available
    public void shootArrow() {
        if (arrows > 0) arrows--;
    }
}
