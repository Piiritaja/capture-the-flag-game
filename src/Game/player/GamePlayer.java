package Game.player;

import Game.bots.Bot;
import Game.maps.Base;
import Game.maps.Object;
import com.esotericsoftware.kryonet.Client;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import networking.packets.Packet010PlayerMovement;
import networking.packets.Packet011PlayerMovementStop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.lang.StrictMath.abs;

/**
 * Player class.
 */
public class GamePlayer extends Player {


    private Client client;
    private boolean dead = false;

    /**
     * Player`s possible colors.
     */
    public enum playerColor {
        RED(Color.RED),
        GREEN(Color.GREEN);

        public final Color color;

        playerColor(Color color) {
            this.color = color;
        }
    }

    public Client getClient(){
        return this.client;
    }

    /**
     * Initializes player.
     * Sets size, color, image depending of the color, initial position.
     *
     * @param x     Initial x coordinate
     * @param y     Initial y coordinate
     * @param dx    Movement x change
     * @param dy    Movement y change
     * @param color Player color
     */
    public GamePlayer(int x, int y, int dx, int dy, playerColor color, Client client) {
        super(x, y, dx, dy, color);
        this.client = client;

    }

    /**
     * Player´s movement.
     * Collision with objects on map and bot object.
     *
     * @param objectsOnMap Objects to check the collision with.
     * @param botsOnMap    Bots to check the collision with.
     */
    public void tick(List<Object> objectsOnMap, List<Bot> botsOnMap, List<Player> players) {
        double x = this.getX();
        double y = this.getY();
        this.setX(this.x += dx);
        this.setY(this.y += dy);
        for (Object object : objectsOnMap) {
            if (object.collides(this)) {
                this.setX(x);
                this.setY(y);
                this.setX(this.x -= dx);
                this.setY(this.y -= dy);
            }
        }
        for (Bot bot : botsOnMap) {
            if (bot.collides(this)) {
                this.setX(x);
                this.setY(y);
                this.setX(this.x -= dx);
                this.setY(this.y -= dy);
            }
        }
        for (Player player : players) {
            if (player != this) {
                if (player.collides(this)) {
                    this.setX(x);
                    this.setY(y);
                    this.setX(this.x -= dx);
                    this.setY(this.y -= dy);
                }
            }
        }
    }

    /**
     * Checks if player is dead or not.
     * @return dead
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Sets boolean dead.
     * @param dead is player dead or not.
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Calculates which way to shoot(UP, DOWN, RIGHT or LEFT).
     * If mouse is clicked, makes new bullet and adds it to the root and bullets list.
     * Sets player image in the same direction with bullets.
     */
    public EventHandler<MouseEvent> shooting = mouseEvent -> {
        if (!this.isDead()) {
            getGunCoordinates();
            double mouseY = mouseEvent.getY();
            double mouseX = mouseEvent.getX();
            Line lineRight = new Line(shootingRightX, shootingRightY, Math.min(shootingRightX + 500, mouseX), mouseY);
            Line lineLeft = new Line(shootingLeftX, shootingLeftY, Math.max(shootingLeftX - 500, mouseX), mouseY);
            Line lineDown = new Line(shootingDownX, shootingDownY, mouseX, Math.min(shootingDownY + 500, mouseY));
            Line lineUp = new Line(shootingUpX, shootingUpY, mouseX, Math.max(shootingUpY - 500, mouseY));
            if (Objects.equals(mouseEvent.getEventType(), MouseEvent.MOUSE_CLICKED)) {
                double allowedLengthX = abs(getX() - mouseX);
                double allowedLengthY = abs(getY() - mouseY);
                animation.pause();
                if (getY() >= mouseY && mouseX >= getX() - allowedLengthY && mouseX <= getX() + allowedLengthY) {
                    this.setImage(walkingUpImage);
                    bullet = new Bullet((int) shootingUpX, (int) shootingUpY, 3, getColorTypeColor());
                    bullet.shoot(lineUp, root, Math.min(500, shootingUpY - mouseY), bullets);
                } else if (getY() < mouseY && mouseX >= getX() - allowedLengthY && mouseX <= getX() + allowedLengthY) {
                    this.setImage(walkingDownImage);
                    bullet = new Bullet((int) shootingDownX, (int) shootingDownY, 3, getColorTypeColor());
                    bullet.shoot(lineDown, root, Math.min(500, mouseY - shootingDownY), bullets);
                } else if (getX() < mouseX && mouseY >= getY() - allowedLengthX && mouseY <= getY() + allowedLengthX) {
                    this.setImage(walkingRightImage);
                    bullet = new Bullet((int) shootingRightX, (int) shootingRightY, 3, getColorTypeColor());
                    bullet.shoot(lineRight, root, Math.min(500, mouseX - shootingRightX), bullets);
                } else if (getX() >= mouseX && mouseY >= getY() - allowedLengthX && mouseY <= getY() + allowedLengthX) {
                    this.setImage(walkingLeftImage);
                    bullet = new Bullet((int) shootingLeftX, (int) shootingLeftY, 3, getColorTypeColor());
                    bullet.shoot(lineLeft, root, Math.min(500, shootingLeftX - mouseX), bullets);
                }
                animation.play();
                root.getChildren().add(bullet);
                bullets.add(bullet);
            }
        }
    };


    /**
     * Set image depending which key is pressed.
     * Set dy or dx some value to move player depending which key is pressed.
     * Play animation after key is pressed.
     */
    public EventHandler<KeyEvent> pressed = keyEvent -> {
        Packet010PlayerMovement movement = new Packet010PlayerMovement();
        movement.playerId = this.getId();
        if (keyEvent.getCode().equals(KeyCode.W)) {
            movement.direction = 1;
            client.sendUDP(movement);
            moveUp();
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            movement.direction = 2;
            client.sendUDP(movement);
            moveDown();
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            movement.direction = 3;
            client.sendUDP(movement);
            moveRight();
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            movement.direction = 4;
            client.sendUDP(movement);
            moveLeft();
        }
    };

    /**
     * Set Dy or Dx 0 depending of the key released.
     * Pause animation after key is released.
     */
    public EventHandler<KeyEvent> released = keyEvent -> {
        Packet011PlayerMovementStop packet = new Packet011PlayerMovementStop();
        packet.playerID = this.getId();
        if (keyEvent.getCode().equals(KeyCode.W) || keyEvent.getCode().equals(KeyCode.S)) {
            packet.direction = 'y';
            client.sendUDP(packet);
            stopMovementY();
        } else if (keyEvent.getCode().equals(KeyCode.D) || keyEvent.getCode().equals(KeyCode.A)) {
            packet.direction = 'x';
            client.sendUDP(packet);
            stopPlayerMovementX();
        }
    };

}
