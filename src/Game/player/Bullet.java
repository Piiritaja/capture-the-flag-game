package Game.player;

import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.Object;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;
import java.util.Iterator;
import java.util.List;

public class Bullet extends Circle {

    int x, y, radius;
    Color color;

    public Bullet(int x, int y, int radius, Color color) {
        super(x, y, radius);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.setFill(color);
    }


    // shooting bullets
    public void shoot(Line line, Group root, double distance, List<Bullet> bullets) {
        PathTransition transition = new PathTransition();
        transition.setNode(this);
        if (distance > 0) {
            transition.setDuration(Duration.seconds((distance) / 400));
        } else {
            root.getChildren().remove(this);
        }
        transition.setPath(line);
        Timeline playTime = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> transition.play()),
                new KeyFrame(Duration.seconds(((distance + 50) / 400) * 0.8),
                        event -> root.getChildren().remove(this)),
                new KeyFrame(Duration.seconds(((distance + 50) / 400) * 0.8),
                        event -> bullets.remove(this)));
        playTime.play();
    }

    public void bulletCollision(Player player, List<Object> objectsOnMap, Group root, BotSpawner botSpawner) {
        Iterator<Bullet> bullets = player.bullets.iterator();
        while (bullets.hasNext()) {
            Bullet bullet = bullets.next();
            for (Object object : objectsOnMap) {
                if (object.collides(bullet)) {
                    root.getChildren().remove(bullet);
                    bullets.remove();
                    if (bullets.hasNext()) {
                        bullet = bullets.next();
                    } else {
                        break;
                    }
                }
            }
            for (int i = 0; i < botSpawner.botsOnMap.size(); i++) {
                Bot bot = botSpawner.botsOnMap.get(i);
                if (bot.collides(bullet)) {
                    root.getChildren().remove(bullet);
                    bullets.remove();
                    bot.lives -= 1;
                    if (bot.getBotLives() <= 0) {
                        root.getChildren().remove(bot);
                        botSpawner.botsOnMap.remove(bot);
                        i--;
                    }
                }
            }
        }
    }

}
