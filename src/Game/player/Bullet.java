package Game.player;

import Game.Screen;
import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.Object;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Client;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import networking.ServerClient;
import networking.packets.Packet009BotHit;
import networking.packets.Packet013PlayerHit;

import javax.swing.text.html.ImageView;
import java.util.Iterator;
import java.util.List;

/**
 * Bullet class.
 */
public class Bullet extends Circle {

    int x, y, radius;
    Color color;

    /**
     * Initializes bullet.
     * Sets radius, color and initial position.
     *
     * @param x      Initial x coordinate
     * @param y      Initial y coordinate
     * @param radius Bullet radius
     * @param color  Bullet color
     */
    public Bullet(int x, int y, int radius, Color color) {
        super(x, y, radius);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.setFill(color);
    }

    /**
     * Sets transition for bullet.
     * Calculates distance.
     * Plays bullet transition and removes bullet from root and bullets list.
     *
     * @param line     Bullet path.
     * @param root     Root to add bullet.
     * @param distance How far bullet travels.
     * @param bullets  List of bullets.
     */
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

    /**
     * Detects collision between bullet and object on map or bullet and bot on map.
     * Removes bullet from root and bullets list after collision.
     * Removes one life from bot if the collision is detected.
     *
     * @param player       Player that shoots bullet.
     * @param objectsOnMap Objects that are displayed on map.
     * @param root         Group from where to remove bullets.
     * @param botSpawner   Calculates bots on map.
     * @param client       Client that shoots the bullet.
     */
    public void bulletCollision(Player player, List<Object> objectsOnMap, Group root, BotSpawner botSpawner, Client client) {
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
                    Packet009BotHit botHit = new Packet009BotHit();
                    botHit.lives = bot.lives;
                    botHit.botId = bot.getBotId();
                    client.sendUDP(botHit);
                    if (bot.getBotLives() <= 0) {
                        root.getChildren().remove(bot);
                        botSpawner.botsOnMap.remove(bot);
                        i--;
                    }
                }
            }
            if (bullet.color == Color.BLUE) {
                if (player.collides(bullet)) {
                    Packet013PlayerHit playerHit = new Packet013PlayerHit();
                    playerHit.playerID = player.getId();
                    playerHit.playerLives = player.lives - 1;
                    client.sendUDP(playerHit);
                    root.getChildren().remove(bullet);
                    bullets.remove();
                    player.lives -= 1;
                    if (player.lives <= 0) {
                        player.x = 0;
                        player.y = 0;
                        root.getChildren().remove(player);
                    }
                }
            }
        }
    }

    public Color getColor() {
        return this.color;
    }

}

