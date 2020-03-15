package Game.player;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Bullet extends Circle {

    int x, y, width, height, radius;

    public Bullet(int x, int y, int radius, Color color) {
        super(x, y, radius);
        this.x = x;
        this.y = y;
        this.radius = radius;
        //this.width = this.get;
        //this.height = height;
        this.setFill(color);
    }

    // shooting bullets
    public void shoot(Line line, Group root, double distance) {
        PathTransition transition = new PathTransition();
        transition.setNode(this);
        transition.setDuration(Duration.seconds((distance) / 400));
        transition.setPath(line);
        Timeline playTime = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> transition.play()),
                new KeyFrame(Duration.seconds(((distance + 50) / 400) * 0.8), event -> root.getChildren().remove(this))
        );
        playTime.play();
    }
}
