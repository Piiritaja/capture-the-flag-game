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
import javafx.stage.Stage;
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
public class Player extends ImageView {

    //Constants for player size
    private static final int PLAYER_WIDTH = 60;
    private static final int PLAYER_HEIGHT = 60;
    private static final int PLAYER_FRAME_WIDTH = 282;
    private static final int PLAYER_FRAME_HEIGHT = 282;
    private static final int COLUMNS = 4;
    private static final int COUNT = 4;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    SpriteAnimation animation;


    //Constants for player model graphics
    private static final String RED_PLAYER_MAIN_IMAGE = "assets/player/red/still.png";
    private static final String GREEN_PLAYER_MAIN_IMAGE = "assets/player/green/still.png";
    private Image image;
    private Image walkingRightImage;
    private Image walkingLeftImage;
    private Image walkingUpImage;
    private Image walkingDownImage;
    private Group root;

    // shooting coordinates
    double shootingRightX;
    double shootingRightY;
    double shootingUpX;
    double shootingUpY;
    double shootingDownX;
    double shootingDownY;
    double shootingLeftX;
    double shootingLeftY;


    public List<Bullet> bullets = new ArrayList<>();
    int step = 2;
    public int dx, dy, x, y, width, height;
    private playerColor color;
    private double playerLocationXInTiles;
    private double playerLocationYInTiles;
    public int lives;
    Bullet bullet;

    private Client client;

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
    public Player(int x, int y, int dx, int dy, playerColor color, Client client) {
        this.client = client;
        if (color.equals(playerColor.GREEN)) {
            image = new Image(GREEN_PLAYER_MAIN_IMAGE);
            walkingRightImage = new Image("assets/player/green/walkingRight.png");
            walkingLeftImage = new Image("assets/player/green/walkingLeft.png");
            walkingUpImage = new Image("assets/player/green/walkingUp.png");
            walkingDownImage = new Image("assets/player/green/walkingDown.png");
        } else if (color.equals(playerColor.RED)) {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
            walkingRightImage = new Image("assets/player/red/walkingRight.png");
            walkingLeftImage = new Image("assets/player/red/walkingLeft.png");
            walkingUpImage = new Image("assets/player/red/walkingUp.png");
            walkingDownImage = new Image("assets/player/red/walkingDown.png");
        } else {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
        }
        this.setImage(image);
        this.width = PLAYER_WIDTH;
        this.height = PLAYER_HEIGHT;
        this.setFitWidth(PLAYER_WIDTH);
        this.setFitHeight(PLAYER_HEIGHT);
        this.setX(x);
        this.setY(y);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.dx = dx;
        this.dy = dy;
        this.color = color;
        this.lives = 10;
        this.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT));
        animation = new SpriteAnimation(
                this,
                Duration.millis(700),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                PLAYER_FRAME_WIDTH, PLAYER_FRAME_HEIGHT
        );
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
     * Calculates which way to shoot(UP, DOWN, RIGHT or LEFT).
     * If mouse is clicked, makes new bullet and adds it to the root and bullets list.
     * Sets player image in the same direction with bullets.
     */
    public EventHandler<MouseEvent> shooting = mouseEvent -> {
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
    };

    /**
     * Calculates gun X and Y position to know where the bullets come out of.
     */
    public void getGunCoordinates() {
        shootingRightX = getX() + getWidth();
        shootingRightY = getY() + getHeight() / 1.63;
        shootingUpX = getX() + getWidth() / 1.63;
        shootingUpY = getY();
        shootingDownX = getX() + getWidth() - getWidth() / 1.63;
        shootingDownY = getY() + getHeight();
        shootingLeftX = getX();
        shootingLeftY = getY() + getHeight() - getHeight() / 1.63;
    }

    public void moveUp() {
        System.out.println("Moving up");
        this.setImage(walkingUpImage);
        setDy(-step);
        animation.play();

    }

    public void moveDown() {
        System.out.println("Moving down");
        this.setImage(walkingDownImage);
        setDy(step);
        animation.play();

    }

    public void moveRight() {
        System.out.println("Moving right");
        this.setImage(walkingRightImage);
        setDx(step);
        animation.play();

    }

    public void moveLeft() {
        System.out.println("Moving left");
        this.setImage(walkingLeftImage);
        setDx(-step);
        animation.play();

    }

    public void stopMovementY() {
        System.out.println("Stopped movement Y");
        setDy(0);
        animation.pause();
    }

    public void stopPlayerMovementX() {
        System.out.println("Stopped movement X");
        setDx(0);
        animation.pause();
    }


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

    /**
     * Sets movement x change.
     *
     * @param dx Change of the movement.
     */
    public void setDx(int dx) {
        this.dx = dx;
    }

    /**
     * Sets movement y change.
     *
     * @param dy Change of the movement.
     */
    public void setDy(int dy) {
        this.dy = dy;
    }

    /**
     * @return The width of this player.
     */
    public double getWidth() {
        return this.getFitWidth();
    }

    /**
     * @return The height of this player.
     */
    public double getHeight() {
        return this.getFitHeight();
    }

    /**
     * @return The color of this player.
     */
    public playerColor getColor() {
        return color;
    }

    /**
     * Set group for this player.
     *
     * @param root The group for this player.
     */
    public void setRoot(Group root) {
        this.root = root;
    }

    public int getLives() {
        return this.lives;
    }

    public void setPlayerLocationXInTiles(double x) {
        this.playerLocationXInTiles = x;
    }

    public void setPlayerLocationYInTiles(double y) {
        this.playerLocationYInTiles = y;
    }

    public double getPlayerLocationXInTiles() {
        return playerLocationXInTiles;
    }

    public double getPlayerLocationYInTiles() {
        return playerLocationYInTiles;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    private Rectangle boundaries() {
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(getX() + width / 4.0);
        playerBoundaries.setY(getY() + height / 4.0);
        playerBoundaries.setHeight(getHeight() - height / 2.0);
        playerBoundaries.setWidth(getWidth() - width / 2.0);
        return playerBoundaries;
    }

    public boolean collides(Bullet bullet) {
        Rectangle playerBoundaries = boundaries();
        Rectangle bulletBoundaries = new Rectangle();
        bulletBoundaries.setX(bullet.getCenterX() - bullet.getRadius());
        bulletBoundaries.setY(bullet.getCenterY() - bullet.getRadius());
        bulletBoundaries.setHeight(bullet.getRadius() * 2);
        bulletBoundaries.setWidth(bullet.getRadius() * 2);
        return ((Path) Shape.intersect(bullet, playerBoundaries)).getElements().size() > 1;
    }

    public boolean collides(Player player) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(player.getX());
        playerBoundaries.setY(player.getY() + player.height / 4.0);
        playerBoundaries.setHeight(player.getHeight() - player.height / 2.0);
        playerBoundaries.setWidth(player.getWidth());
        return objectBoundaries.getBoundsInParent().intersects(playerBoundaries.getBoundsInParent());
    }


    public static double calcPlayerXStartingPosition(Base greenBase, Base redBase, playerColor color) {
        Random positionPicker = new Random();
        if (color.equals(Player.playerColor.GREEN)) {
            return Math.max((int) greenBase.getLeftX() + 40, (positionPicker.nextInt((int)
                    greenBase.getRightX() - 40)));
        } else if (color.equals(Player.playerColor.RED)) {
            return Math.min((int) redBase.getRightX() - 40, (positionPicker.nextInt((int)
                    redBase.getLeftX() + 40)));
        }
        return 90;
    }

    /**
     * Sets player x coordinate when game or new round starts.
     *
     * @param greenBase base where to put green players
     * @param redBase   base where to put red players
     */
    public void setPlayerXStartingPosition(Base greenBase, Base redBase) {
        this.setX(calcPlayerXStartingPosition(greenBase, redBase, color));
    }

    /**
     * Sets player y coordinate when game or new round starts.
     *
     * @param greenBase base where to put green players
     * @param redBase   base where to put red players
     */
    public void setPlayerYStartingPosition(Base greenBase, Base redBase) {
        this.setY(calcPlayerYStartingPosition(greenBase, redBase, color));
    }

    public static double calcPlayerYStartingPosition(Base greenBase, Base redBase, playerColor color) {
        Random positionPicker = new Random();
        if (color.equals(Player.playerColor.GREEN)) {
            return Math.max((int) greenBase.getTopY() + 40, (positionPicker.nextInt((int)
                    greenBase.getBottomY() - 40)));
        } else if (color.equals(Player.playerColor.RED)) {
            return Math.max((int) redBase.getTopY() + 40, (positionPicker.nextInt((int)
                    redBase.getBottomY() - 40)));
        }
        return 90;
    }

    /**
     * Returns player color in Color type.
     *
     * @return Color
     */
    public Color getColorTypeColor() {
        if (color == playerColor.RED) {
            return Color.RED;
        } else {
            return Color.GREEN;
        }
    }
}
