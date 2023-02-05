package com.company;

public class Cell {
    private boolean isOpen = false;
    private boolean isMine = false;
    private boolean isFlag = false;
    private int surroundingMines = 0;

    public void open() {
        isOpen = true;
    }

    public void setSurroundingMines(int surroundingMines) {
        this.surroundingMines = surroundingMines;
    }

    public int getSurroundingMines() {
        return surroundingMines;
    }

    public void placeFlag() {
        isFlag = true;
    }

    public void placeMine() {
        isMine = true;
    }

    public void toggleFlag() {
        isFlag = !isFlag;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
