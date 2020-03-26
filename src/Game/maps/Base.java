package Game.maps;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The class of team bases displayed on the map.
 */
public class Base extends Rectangle {

    private baseColor color;

    public enum baseColor {
        RED(Color.RED),
        GREEN(Color.GREEN);

        public final Color color;

        baseColor(Color color) {
            this.color = color;
        }

    }

    /**
     * Sets the base color, size and location.
     *
     * @param color  Base color
     * @param width  Base width
     * @param height Base Height
     * @param x      Base top left x coordinate
     * @param y      Base top left y coordinate
     */
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

    /**
     * @return The leftmost X coordinate of the base
     */
    public double getLeftX() {
        return this.getX();
    }

    /**
     * @return The rightmost X coordinate of the base
     */
    public double getRightX() {
        return this.getX() + this.getWidth();
    }

    /**
     * @return The top Y coordinate of the base.
     */
    public double getTopY() {
        return this.getY();
    }

    /**
     * @return The bottom Y coordinate of the base
     */
    public double getBottomY() {
        return this.getY() + this.getHeight();
    }

    /**
     * @return The color of the base
     */
    public baseColor getBaseColor() {
        return this.color;
    }
}
