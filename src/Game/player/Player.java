package Game.player;

import Game.bots.Bot;
import Game.maps.Object;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public Player(int x, int y, int dx, int dy, playerColor color) {
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
     * @param botsOnMap Bots to check the collision with.
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

    /**
     * Set image depending which key is pressed.
     * Set dy or dx some value to move player depending which key is pressed.
     * Play animation after key is pressed.
     */
    public EventHandler<KeyEvent> pressed = keyEvent -> {
        if (keyEvent.getCode().equals(KeyCode.W)) {
            this.setImage(walkingUpImage);
            setDy(-step);
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            this.setImage(walkingDownImage);
            setDy(step);
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            this.setImage(walkingRightImage);
            setDx(step);
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            this.setImage(walkingLeftImage);
            setDx(-step);
        }
        animation.play();
    };

    /**
     * Set Dy or Dx 0 depending of the key released.
     * Pause animation after key is released.
     */
    public EventHandler<KeyEvent> released = keyEvent -> {
        if (keyEvent.getCode().equals(KeyCode.W)) {
            setDy(0);
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            setDy(0);
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            setDx(0);
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            setDx(0);
        }
        animation.pause();
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

    public void setLives(int lives) {
        this.lives = lives;
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

    /**
     * Sets player x coordinate when game or new round starts.
     *
     * @param stage main stage
     */
    public void setPlayerXStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.x = (int) stage.widthProperty().get() - 100;
        } else if (color.equals(Player.playerColor.RED)) {
            this.x = 100;
        }
    }

    /**
     * Sets player y coordinate when game or new round starts.
     *
     * @param stage main stage
     */
    public void setPlayerYStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.y = (int) stage.heightProperty().get() / 2;
        } else if (color.equals(Player.playerColor.RED)) {
            this.y = (int) stage.heightProperty().get() / 2;
        }
    }

    /**
     * Returns player color in Color type.
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
