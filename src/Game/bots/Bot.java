package Game.bots;

import Game.player.Bullet;
import Game.player.GamePlayer;
import Game.player.Player;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.sqrt;

/**
 * Bot class
 */
public class Bot extends ImageView {

    //Constants for player size
    private static final int SHOOTING_LENGTH = 200;
    private double botWidth;
    private double botHeight;
    private static final Image BOT_STILL_IMAGE_DOWN = new Image("assets/bot/down.png");
    private static final Image BOT_WALK_IMAGE_1 = new Image("assets/bot/walk1.png");
    //private static final Image BOT_WALK_IMAGE_2 = new Image("assets/bot/walk2.png");
    private static final Image BOT_STILL_IMAGE_LEFT = new Image("assets/bot/left.png");
    private static final Image BOT_STILL_IMAGE_RIGHT = new Image("assets/bot/right.png");
    private static final Image BOT_STILL_IMAGE_UP = new Image("assets/bot/up.png");
    private int botId;
    double time = 0;
    Bullet bullet;
    boolean lethal;

    //Constants for bot model graphics

    public int dx, dy, x, y, lives;

    /**
     * Initializes bot.
     * Sets lives, size  and initial position.
     *
     * @param x     Initial x coordinate
     * @param y     Initial y coordinate
     * @param lives Health points of the bot
     */
    public Bot(int x, int y, int lives, Stage stage, boolean lethal) {
        final int mapWidthInTiles = 40;
        final int mapHeightInTiles = 25;
        botWidth = stage.widthProperty().get() / mapWidthInTiles * 1.5;
        botHeight = stage.heightProperty().get() / mapHeightInTiles * 1.5;
        this.setImage(BOT_STILL_IMAGE_DOWN);
        this.fitWidthProperty().set(botWidth);
        this.fitHeightProperty().set(botHeight);
        this.setX(x);
        this.setY(y);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.lives = lives;
        this.lethal = lethal;
    }

    /**
     * Creates invisible boundaries for the bot.
     * Used for collision detection.
     *
     * @return Boundaries as a JavaFx Rectangle
     */
    public Rectangle boundaries() {
        Rectangle objectBoundaries = new Rectangle();
        objectBoundaries.setX(this.getX() + 5 * botWidth / 16);
        objectBoundaries.setY(this.getY() + 5 * botWidth / 16);
        objectBoundaries.setWidth(this.getFitWidth() - 5 * botWidth / 8);
        objectBoundaries.setHeight(this.getFitHeight() - 5 * botWidth / 8);
        return objectBoundaries;
    }

    /**
     * Checks collision with a player object.
     *
     * @param player Player to check the collision with
     * @return If the bot collides with the player
     */
    public boolean collides(GamePlayer player) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(player.getX() + player.width / 4.0);
        playerBoundaries.setY(player.getY() + player.height / 4.0);
        playerBoundaries.setHeight(player.getHeight() - player.height / 2.0);
        playerBoundaries.setWidth(player.getWidth() - player.width / 2.0);
        return objectBoundaries.getBoundsInParent().intersects(playerBoundaries.getBoundsInParent());
    }

    /**
     * Checks collision with a bullet object.
     *
     * @param bullet bullet to check the collision with.
     * @return if the bot collides with the bullet.
     */
    public boolean collides(Bullet bullet) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(bullet.getCenterX() - bullet.getRadius());
        playerBoundaries.setY(bullet.getCenterY() - bullet.getRadius());
        playerBoundaries.setHeight(bullet.getRadius() * 2);
        playerBoundaries.setWidth(bullet.getRadius() * 2);
        return ((Path) Shape.intersect(bullet, objectBoundaries)).getElements().size() > 1;
    }

    /**
     * Shoots the player that`s in shooting length.
     *
     * @param player player that`s in radius.
     * @param root   main group.
     */
    public void botShooting(Player player, Group root) {
        time += 0.05;
        double lineStartingX = getX();
        double lineStartingY = getY();
        if (time > 5) {
            double distanceBetweenPlayerAndBotX = abs(getX() - player.getX());
            double distanceBetweenPlayerAndBotY = abs(getY() - player.getY());
            if (player.getX() <= getX() + SHOOTING_LENGTH && player.getX() >= getX() - SHOOTING_LENGTH &&
                    getY() - SHOOTING_LENGTH <= player.getY() && player.getY() <= getY() + SHOOTING_LENGTH) {
                if (getY() >= player.getY() && player.getX() >= getX() - distanceBetweenPlayerAndBotY
                        && player.getX() <= getX() + distanceBetweenPlayerAndBotY) {
                    lineStartingX = getX() + getBotWidth() - getBotWidth() / 3.5;
                    lineStartingY = getY() + getBotHeight() * 0.1;
                    this.setImage(BOT_STILL_IMAGE_UP);
                } else if (getY() < player.getY() && player.getX() >= getX() - distanceBetweenPlayerAndBotY
                        && player.getX() <= getX() + distanceBetweenPlayerAndBotY) {
                    lineStartingY = getY() + getBotHeight() * 0.9;
                    lineStartingX = getX() + getBotWidth() / 3.5;
                    this.setImage(BOT_STILL_IMAGE_DOWN);
                } else if (getX() < player.getX() && player.getY() >= getY() - distanceBetweenPlayerAndBotX
                        && player.getY() <= getY() + distanceBetweenPlayerAndBotX) {
                    lineStartingX = getX() + getBotWidth() * 0.9;
                    lineStartingY = getY() + getBotHeight() - getBotHeight() / 3.5;
                    this.setImage(BOT_STILL_IMAGE_RIGHT);
                } else if (getX() >= player.getX() && player.getY() >= getY() - distanceBetweenPlayerAndBotX
                        && player.getY() <= getY() + distanceBetweenPlayerAndBotX) {
                    lineStartingX = getX() + getBotWidth() * 0.1;
                    lineStartingY = getY() + getBotHeight() / 3.5;
                    this.setImage(BOT_STILL_IMAGE_LEFT);
                }
                Line line = new Line(lineStartingX, lineStartingY, player.getX() + player.getWidth() / 2,
                        player.getY() + player.getHeight() / 2);
                bullet = new Bullet((int) lineStartingX, (int) lineStartingY, 3, Color.BLUE, lethal);
                bullet.shoot(line, root, sqrt(Math.pow(player.getX() - getX(), 2) +
                        Math.pow((player.getY() - getY()), 2)), player.bullets);
                root.getChildren().add(bullet);
                player.bullets.add(bullet);
                if (time > 5) {
                    time = 0;
                }
            }
        }
    }

    /**
     * Set the height of this object.
     *
     * @param botHeight The value that the height will be set to
     */
    public void setBotHeight(double botHeight) {
        this.botHeight = botHeight;
        this.setFitHeight(botHeight);
    }

    /**
     * Set the width of this object.
     *
     * @param botWidth The value that the width will be set to
     */
    public void setBotWidth(double botWidth) {
        this.botWidth = botWidth;
        this.setFitWidth(botWidth);
    }

    /**
     * @return The height of this bot
     */
    public double getBotHeight() {
        return botHeight;
    }

    /**
     * @return The width of this bot
     */
    public double getBotWidth() {
        return botWidth;
    }

    /**
     * @return The health points of this bot.
     */
    public int getBotLives() {
        return lives;
    }

    /**
     * Set the unique ID number for this bot object.
     *
     * @param id Value that is set as ID
     */
    public void setBotId(int id) {
        this.botId = id;
    }

    /**
     * @return the id of this bot
     */
    public int getBotId() {
        return botId;
    }
}
