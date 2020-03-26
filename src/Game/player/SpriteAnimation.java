package Game.player;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;

public class SpriteAnimation extends Transition {

    private final ImageView imageView;
    private final int count;
    private final int columns;
    private final int offsetX;
    private final int offsetY;
    private final int frameWidth;
    private final int frameHeight;

    private int lastIndex;

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
