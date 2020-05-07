package Game.player;

import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.MapLoad;
import Game.maps.Object;
import com.esotericsoftware.kryonet.Client;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import networking.packets.Packet009BotHit;
import networking.packets.Packet013PlayerHit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Bullet class.
 */
public class Bullet extends Circle {

    int x, y, radius;
    Color color;
    private boolean lethal;

    /**
     * Initializes bullet.
     * Sets radius, color and initial position.
     *
     * @param x      Initial x coordinate
     * @param y      Initial y coordinate
     * @param radius Bullet radius
     * @param color  Bullet color
     */
    public Bullet(int x, int y, int radius, Color color, boolean lethal) {
        super(x, y, radius);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.setFill(color);
        this.lethal = lethal;
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
    public void shoot(Line line, AnchorPane root, double distance, List<Bullet> bullets) {
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
    public void bulletCollision(List<Player> players, List<Object> objectsOnMap, AnchorPane root, BotSpawner botSpawner,
                                Client client, Player player, List<Player> deadPlayers, MapLoad mapLoad, int a) {
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
                    if (bullet.lethal) {
                        bullets.remove();
                        bot.lives -= 1;
                        Packet009BotHit botHit = new Packet009BotHit();
                        botHit.lives = bot.lives;
                        botHit.botId = bot.getBotId();
                        client.sendUDP(botHit);
                    }

                    if (bot.getBotLives() <= 0) {
                        root.getChildren().remove(bot);
                        botSpawner.botsOnMap.remove(bot);
                        i--;
                    }
                }
            }
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                if (bullet.getColor() != p.getColorTypeColor()) {
                    if (p.collides(bullet)) {
                        root.getChildren().remove(bullet);
                        bullets.remove();
                        if (bullet.lethal) {
                            Packet013PlayerHit playerHit = new Packet013PlayerHit();
                            playerHit.playerID = p.getId();
                            playerHit.playerLives = p.lives - 1;
                            client.sendUDP(playerHit);
                            p.lives -= 1;
                        }

                        if (p.lives <= 0) {
                            if (p.getPickedUpFlag() != null) {
                                p.dropPickedUpFlag();
                            }
                            p.reSpawn(mapLoad, players, deadPlayers);
                            deadPlayers.add(p);
                            root.getChildren().remove(p);
                            i--;
                            a--;
                            players.remove(p);
                        }
                    }
                }
            }
        }
    }

    /**
     * @return the color of this bullet.
     */
    public Color getColor() {
        return this.color;
    }
}

