package Game.player;

import Game.maps.Base;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.StrictMath.abs;

public abstract class Player extends ImageView {
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
    static final String RED_PLAYER_MAIN_IMAGE = "assets/player/red/still.png";
    static final String GREEN_PLAYER_MAIN_IMAGE = "assets/player/green/still.png";
    Image image;
    Image walkingRightImage;
    Image walkingLeftImage;
    Image walkingUpImage;
    Image walkingDownImage;
    Group root;

    public int lives;

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
    GamePlayer.playerColor color;
    private double playerLocationXInTiles;
    private double playerLocationYInTiles;
    Bullet bullet;


    public Player(int x, int y, int dx, int dy, GamePlayer.playerColor color) {
        if (color.equals(GamePlayer.playerColor.GREEN)) {
            image = new Image(GREEN_PLAYER_MAIN_IMAGE);
            walkingRightImage = new Image("assets/player/green/walkingRight.png");
            walkingLeftImage = new Image("assets/player/green/walkingLeft.png");
            walkingUpImage = new Image("assets/player/green/walkingUp.png");
            walkingDownImage = new Image("assets/player/green/walkingDown.png");
        } else if (color.equals(GamePlayer.playerColor.RED)) {
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
    public GamePlayer.playerColor getColor() {
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

    /**
     * Returns player color in Color type.
     *
     * @return Color
     */
    public Color getColorTypeColor() {
        if (color == GamePlayer.playerColor.RED) {
            return Color.RED;
        } else {
            return Color.GREEN;
        }
    }

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

    public Rectangle boundaries() {
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

    public boolean collides(GamePlayer player) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(player.getX());
        playerBoundaries.setY(player.getY() + player.height / 4.0);
        playerBoundaries.setHeight(player.getHeight() - player.height / 2.0);
        playerBoundaries.setWidth(player.getWidth());
        return objectBoundaries.getBoundsInParent().intersects(playerBoundaries.getBoundsInParent());
    }

    public static double calcPlayerXStartingPosition(Base greenBase, Base redBase, GamePlayer.playerColor color) {
        Random positionPicker = new Random();
        if (color.equals(GamePlayer.playerColor.GREEN)) {
            return Math.max((int) greenBase.getLeftX() + 40, (positionPicker.nextInt((int)
                    greenBase.getRightX() - 40)));
        } else if (color.equals(GamePlayer.playerColor.RED)) {
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

    public static double calcPlayerYStartingPosition(Base greenBase, Base redBase, GamePlayer.playerColor color) {
        Random positionPicker = new Random();
        if (color.equals(GamePlayer.playerColor.GREEN)) {
            return Math.max((int) greenBase.getTopY() + 40, (positionPicker.nextInt((int)
                    greenBase.getBottomY() - 40)));
        } else if (color.equals(GamePlayer.playerColor.RED)) {
            return Math.max((int) redBase.getTopY() + 40, (positionPicker.nextInt((int)
                    redBase.getBottomY() - 40)));
        }
        return 90;
    }

}
