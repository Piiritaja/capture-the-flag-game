package Game.player;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Flag class.
 */
public class Flag extends Rectangle {

    int x, y, width, height;
    private flagColor color;
    private boolean pickedUp = false;

    /**
     * Flag`s possible colors.
     */
    public enum flagColor {
        RED(Color.RED),
        GREEN(Color.GREEN);

        public final Color color;

        flagColor(Color color) { this.color = color; }
    }

    /**
     * Initializes Flag.
     *
     * @param x         Initialize x coordinate
     * @param y         Initialize y coordinate
     * @param width     Width of the flag
     * @param height    Height of the flag
     * @param color     Color of the flag
     */
    public Flag(int x, int y, int width, int height, flagColor color) {
        super(x, y, width, height);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.width = width;
        this.height = height;
        this.color = color;
        this.setFill(color.color);
    }

    /**
     * @return Color of this flag.
     */
    public flagColor getColor() {
        return this.color;
    }

    /**
     * Sets boolean pickedUp true if flag has been picked up by player.
     */
    public void pickUp() {
        this.pickedUp = true;
    }

    /**
     * @return boolean pickedUp.
     */
    public boolean isPickedUp() {
        return pickedUp;
    }
}
