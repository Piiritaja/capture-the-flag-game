package Game;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

public class Flag extends Rectangle {

    int x, y, width, height;

    public Flag(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setFill(color);
    }
}
