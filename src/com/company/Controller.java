package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;

public class Controller {
    private static final int SIZE_OF_SQUARE = 61;
    private static final int UNIT_X = 9;
    private static final int UNIT_Y = 9;
    private static final int RESOLUTION_X = UNIT_X * SIZE_OF_SQUARE;
    private static final int RESOLUTION_Y = UNIT_Y * SIZE_OF_SQUARE;
    private static final int NUMBER_OF_MINES = 5;
    private static final String PATH = "src/textures/";

    private View view;
    private Graphics graphics;
    private Cell[][] cells = new Cell[UNIT_X][UNIT_Y];
    private boolean isGameOver = false;

    public void start() {
        view.create(RESOLUTION_X, RESOLUTION_Y);
        fillField();
        generateMines();
        setNumberOfMines();
        renderImage();
    }

    private void fillField() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                cells[x][y] = new Cell();
            }
        }
    }

    private void setNumberOfMines() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                cells[x][y].setSurroundingMines(findMines(x, y));
            }
        }
    }

    private boolean isLocationValid(int x, int y) {
        return x >= 0 && x < UNIT_X && y >= 0 && y < UNIT_Y;
    }

    private boolean isWin() {
        return Arrays.stream(cells).flatMap(Arrays::stream).noneMatch(cell -> !cell.isMine() && !cell.isOpen());
    }

    private int count(int x, int y, Predicate<Cell> predicate) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isLocationValid(i, j) && predicate.test(cells[i][j])) {
                    count++;
                }
            }
        }
        return count;
    }

    private int findMines(int x, int y) {
        return count(x, y, Cell::isMine);
    }

    private int countFlags(int x, int y) {
        return count(x, y, Cell::isFlag);
    }

    private void openArea(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (isLocationValid(i, j) && !cells[i][j].isOpen() && !cells[i][j].isMine()) {
                    open(i, j);
                }
            }
        }
    }

    private void open(int x, int y) {
        cells[x][y].open();
        if (cells[x][y].isMine()) {
            isGameOver = true;
            showMines();
            System.out.println("You lost");
            return;
        }
        if (isWin()) {
            isGameOver = true;
            setFlags();
            System.out.println("You won");
            return;
        }
        if (cells[x][y].getSurroundingMines() == 0) {
            openArea(x, y);
        }
    }

    private void showMines() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                if (cells[x][y].isMine()) {
                    cells[x][y].open();
                }
            }
        }
    }

    private void setFlags() {
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                if (cells[x][y].isMine()) {
                    cells[x][y].placeFlag();
                }
            }
        }
    }

    private boolean isCorrectForAutoOpen(int x, int y) {
        System.out.println(countFlags(x, y) + " " + cells[x][y].getSurroundingMines());
        return countFlags(x, y) == cells[x][y].getSurroundingMines();
    }

    public void handleMouseClick(int mouseX, int mouseY, boolean isLeftMouseButton) {
        if (isGameOver) {
            return;
        }
        int x = mouseX / SIZE_OF_SQUARE;
        int y = mouseY / SIZE_OF_SQUARE;
        Cell cell = cells[x][y];
        if (isLeftMouseButton) {
            if (cell.isFlag()) {
                return;
            }
            open(x, y);
        } else {
            if (cell.getSurroundingMines() > 0 && isCorrectForAutoOpen(x, y) && cell.isOpen()) {
                openArea(x, y);
            } else {
                if (!cell.isOpen()) {
                    cell.toggleFlag();
                }
            }
        }
        renderImage();
    }

    private void placeMine() {
        int x;
        int y;
        do {
            x = random(UNIT_X);
            y = random(UNIT_Y);
        } while (cells[x][y].isMine());
        cells[x][y].placeMine();
    }

    private void generateMines() {
        for (int i = 0; i < NUMBER_OF_MINES; i++) {
            placeMine();
        }
    }

    private int random(int max) {
        return (int) (Math.random() * max);
    }

    private BufferedImage loadImage(String fileName) {
        try {
            return ImageIO.read(new File(PATH + fileName + ".png"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private void draw(int x, int y, BufferedImage image) {
        graphics.drawImage(image, x * SIZE_OF_SQUARE, y * SIZE_OF_SQUARE, null);
    }

    private void drawField() {
        String name;
        for (int x = 0; x < UNIT_X; x++) {
            for (int y = 0; y < UNIT_Y; y++) {
                Cell cell = cells[x][y];
                if (cell.isOpen()) {
                    if (cell.isMine()) {
                        name = "Mine";
                    } else {
                        name = "open" + cell.getSurroundingMines();
                    }
                } else {
                    if (cell.isFlag()) {
                        name = "Flag";
                    } else {
                        name = "Closed";
                    }
                }
                draw(x, y, loadImage(name));
            }
        }
    }

    private void renderImage() {
        BufferedImage image = new BufferedImage(RESOLUTION_X, RESOLUTION_Y, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        drawField();
        view.setImage(image);
    }

    public void setView(View view) {
        this.view = view;
    }
}
