package Game.maps;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Base extends Rectangle {

    private baseColor color;
    public enum baseColor {
        RED(Color.RED),
        GREEN(Color.GREEN),
        YELLOW(Color.YELLOW);

        public final Color color;

        private baseColor(Color color) {
            this.color = color;
        }

    }

    public Base(baseColor color, double width, double height, double x, double y) {
        // x and y are starting cordinates for the rectangle (top left corner)
        this.color = color;
        this.setFill(color.color);
        this.setWidth(width);
        this.setHeight(height);
        this.setX(x);
        this.setY(y);
        this.setOpacity(0.4);
    }

    public double getLeftX() {
        return this.getX();
    }

    public double getRightX() {
        return this.getX() + this.getWidth();
    }

    public double getTopY() {
        return this.getY();
    }

    public double getBottomY() {
        return this.getY() + this.getHeight();
    }

    public baseColor getBaseColor() {
        return this.color;
    }
}
