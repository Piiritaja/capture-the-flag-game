package Game;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Bullet extends Rectangle {

    int x, y, width, height;

    public Bullet(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.width = width;
        this.height = height;
        this.setFill(color);
    }

    // shooting bullets
    public void shoot(Line line, Group root, double distance) {
        PathTransition transition = new PathTransition();
        transition.setNode(this);
        transition.setDuration(Duration.seconds(distance / 400));
        transition.setPath(line);
        Timeline playTime = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> transition.play()),
                new KeyFrame(Duration.seconds((distance / 400) * 0.80), event -> root.getChildren().remove(this))
        );
        playTime.play();
    }
}
