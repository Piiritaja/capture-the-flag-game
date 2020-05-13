package Game.player;

import Game.maps.Base;
import Game.maps.MapLoad;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
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
    static final String RED_PLAYER_MAIN_IMAGE = "/player/red/still.png";
    static final String GREEN_PLAYER_MAIN_IMAGE = "/player/green/still.png";
    public Image image;
    public Image walkingRightImage;
    public Image walkingLeftImage;
    public Image walkingUpImage;
    public Image walkingDownImage;
    AnchorPane root;
    public int lives;
    boolean dead = false;
    public Timeline playerDead = new Timeline();

    // shooting coordinates
    public double shootingRightX;
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
    Flag pickedUpFlag = null;
    Stage stage;


    public Player(int x, int y, int dx, int dy, GamePlayer.playerColor color, Stage stage) {
        if (color.equals(GamePlayer.playerColor.GREEN)) {
            image = new Image(Player.class.getResourceAsStream(GREEN_PLAYER_MAIN_IMAGE));
            walkingRightImage = new Image(Player.class.getResourceAsStream("/player/green/walkingRight.png"));
            walkingLeftImage = new Image(Player.class.getResourceAsStream("/player/green/walkingLeft.png"));
            walkingUpImage = new Image(Player.class.getResourceAsStream("/player/green/walkingUp.png"));
            walkingDownImage = new Image(Player.class.getResourceAsStream("/player/green/walkingDown.png"));
        } else if (color.equals(GamePlayer.playerColor.RED)) {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
            walkingRightImage = new Image(Player.class.getResourceAsStream("/player/red/walkingRight.png"));
            walkingLeftImage = new Image(Player.class.getResourceAsStream("/player/red/walkingLeft.png"));
            walkingUpImage = new Image(Player.class.getResourceAsStream("/player/red/walkingUp.png"));
            walkingDownImage = new Image(Player.class.getResourceAsStream("/player/red/walkingDown.png"));
        } else {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
        }
        this.stage = stage;
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
     * Checks if player is dead or not.
     *
     * @return dead
     */
    /*public boolean isDead() {
        return dead;
    }*/

    /**
     * Sets boolean dead.
     *
     * @param dead is player dead or not.
     */
    public void setDead(boolean dead) {
        this.dead = dead;
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

    public int getDx() {
        return this.dx;
    }

    public int getDy() {
        return this.dy;
    }

    public AnchorPane getRoot() {
        return this.root;
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
    public void setRoot(AnchorPane root) {
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

    public void pickupFlag(Flag flag) {
        this.pickedUpFlag = flag;
        flag.pickUp();
    }

    public void dropPickedUpFlag() {
        if (pickedUpFlag != null) {
            pickedUpFlag.drop();
        }
        this.pickedUpFlag = null;
    }

    public Flag getPickedUpFlag() {
        return pickedUpFlag;
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
        this.setImage(walkingUpImage);
        setDy(-step);
        animation.play();

    }

    public void moveDown() {
        this.setImage(walkingDownImage);
        setDy(step);
        animation.play();

    }

    public void moveRight() {
        this.setImage(walkingRightImage);
        setDx(step);
        animation.play();

    }

    public void moveLeft() {
        this.setImage(walkingLeftImage);
        setDx(-step);
        animation.play();

    }

    public void stopMovementY() {
        setDy(0);
        animation.pause();
    }

    public void stopPlayerMovementX() {
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
            return Math.max((int) greenBase.getLeftX() + 100, (positionPicker.nextInt((int)
                    greenBase.getRightX() - 100)));
        } else if (color.equals(GamePlayer.playerColor.RED)) {
            return Math.min((int) redBase.getRightX() - 100, (positionPicker.nextInt((int)
                    redBase.getLeftX() + 100)));
        }
        return 90;
    }

    /**
     * Sets player x coordinate when game or new round starts.
     *
     * @param greenBase base where to put green players
     * @param redBase   base where to put red players
     */
    public abstract void setPlayerXStartingPosition(Base greenBase, Base redBase);

    /**
     * Sets player y coordinate when game or new round starts.
     *
     * @param greenBase base where to put green players
     * @param redBase   base where to put red players
     */
    public abstract void setPlayerYStartingPosition(Base greenBase, Base redBase);

    public static double calcPlayerYStartingPosition(Base greenBase, Base redBase, GamePlayer.playerColor color) {
        Random positionPicker = new Random();
        if (color.equals(GamePlayer.playerColor.GREEN)) {
            return Math.max((int) greenBase.getTopY() + 100, (positionPicker.nextInt((int)
                    greenBase.getBottomY() - 100)));
        } else if (color.equals(GamePlayer.playerColor.RED)) {
            return Math.max((int) redBase.getTopY() + 100, (positionPicker.nextInt((int)
                    redBase.getBottomY() - 100)));
        }
        return 90;
    }

    /**
     * If player is killed, player respawns after 5 seconds.
     *
     * @param mapLoad     map loading
     * @param players     alive players
     * @param deadPlayers dead players
     */
    public void reSpawn(MapLoad mapLoad, List<Player> players, List<Player> deadPlayers) {
        playerDead = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> this.setPlayerXStartingPosition(mapLoad.getBaseByColor(Base.baseColor.GREEN), mapLoad.getBaseByColor(Base.baseColor.RED))),
                new KeyFrame(Duration.seconds(5), event -> this.setPlayerYStartingPosition(mapLoad.getBaseByColor(Base.baseColor.GREEN), mapLoad.getBaseByColor(Base.baseColor.RED))),
                new KeyFrame(Duration.seconds(5), event -> this.setLives(10)),
                new KeyFrame(Duration.seconds(5), event -> players.add(this)),
                new KeyFrame(Duration.seconds(5), event -> root.getChildren().add(this)),
                new KeyFrame(Duration.seconds(5), event -> deadPlayers.remove(this)),
                new KeyFrame(Duration.seconds(5), event -> this.setDead(false))
        );
        playerDead.play();
    }

    public void shoot(double x, double y, boolean lethal) {
        getGunCoordinates();
        Line lineRight = new Line(shootingRightX, shootingRightY, Math.min(shootingRightX + 500, x), y);
        Line lineLeft = new Line(shootingLeftX, shootingLeftY, Math.max(shootingLeftX - 500, x), y);
        Line lineDown = new Line(shootingDownX, shootingDownY, x, Math.min(shootingDownY + 500, y));
        Line lineUp = new Line(shootingUpX, shootingUpY, x, Math.max(shootingUpY - 500, y));
        double allowedLengthX = abs(getX() - x);
        double allowedLengthY = abs(getY() - y);
        animation.pause();
        if (getY() >= y && x >= getX() - allowedLengthY && x <= getX() + allowedLengthY) {
            this.setImage(walkingUpImage);
            bullet = new Bullet((int) shootingUpX, (int) shootingUpY, 3, Color.YELLOW, lethal);
            bullet.shoot(lineUp, root, Math.min(500, shootingUpY - y), bullets);
        } else if (getY() < y && x >= getX() - allowedLengthY && x <= getX() + allowedLengthY) {
            this.setImage(walkingDownImage);
            bullet = new Bullet((int) shootingDownX, (int) shootingDownY, 3, Color.YELLOW, lethal);
            bullet.shoot(lineDown, root, Math.min(500, y - shootingDownY), bullets);
        } else if (getX() < x && y >= getY() - allowedLengthX && y <= getY() + allowedLengthX) {
            this.setImage(walkingRightImage);
            bullet = new Bullet((int) shootingRightX, (int) shootingRightY, 3, Color.YELLOW, lethal);
            bullet.shoot(lineRight, root, Math.min(500, x - shootingRightX), bullets);
        } else if (getX() >= x && y >= getY() - allowedLengthX && y <= getY() + allowedLengthX) {
            this.setImage(walkingLeftImage);
            bullet = new Bullet((int) shootingLeftX, (int) shootingLeftY, 3, Color.YELLOW, lethal);
            bullet.shoot(lineLeft, root, Math.min(500, shootingLeftX - x), bullets);
        }
        animation.play();
        root.getChildren().add(bullet);
        bullets.add(bullet);

    }


}
