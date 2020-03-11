package Game;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

public class Flag extends Rectangle {

    int x, y, width, height;
    private flagColor color;
    public enum flagColor {
        RED(Color.RED),
        GREEN(Color.GREEN);

        public final Color color;

        flagColor(Color color) { this.color = color; }
    }

    public Flag(int x, int y, int width, int height, flagColor color) {
        super(x, y, width, height);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.width = width;
        this.height = height;
        this.color = color;
        this.setFill(color.color);
    }

    public flagColor getColor() {
        return this.color;
    }
}
