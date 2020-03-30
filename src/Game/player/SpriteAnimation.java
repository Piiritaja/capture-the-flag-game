package Game.player;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Animation class for player movement.
 */
public class SpriteAnimation extends Transition {

    private final ImageView imageView;
    private final int count;
    private final int columns;
    private final int offsetX;
    private final int offsetY;
    private final int frameWidth;
    private final int frameHeight;
    private int lastIndex;

    /**
     * Player sprite animation.
     *
     * @param imageView ImageView to animate.
     * @param duration  Duration of one cycle.
     * @param count     Amount of the pictures on the sprite sheet.
     * @param columns   Columns amount on the sprite sheet.
     * @param offsetX   Picture offset x.
     * @param offsetY   Picture offset y.
     * @param width     Picture width on sprite sheet.
     * @param height    Picture height on sprite sheet.
     */
    public SpriteAnimation(
            ImageView imageView,
            Duration duration,
            int count,   int columns,
            int offsetX, int offsetY,
            int width,   int height) {
        this.imageView = imageView;
        this.count = count;
        this.columns = columns;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.frameWidth = width;
        this.frameHeight = height;
        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }

    /**
     * Iterates through the sprites.
     * @param k Specifies the current position.
     */
    protected void interpolate(double k) {
        final int index = Math.min((int) Math.floor(k * count), count - 1);
        if (index != lastIndex) {
            final int x = (index % columns) * frameWidth + offsetX;
            final int y = (index / columns) * frameHeight + offsetY;
            imageView.setViewport(new Rectangle2D(x, y, frameWidth, frameHeight));
            lastIndex = index;
        }
    }
}
