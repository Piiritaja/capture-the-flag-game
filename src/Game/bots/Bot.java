package Game.bots;

import Game.player.Bullet;
import Game.player.Player;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * Bot class
 */
public class Bot extends ImageView {

    //Constants for player size
    private double botWidth;
    private double botHeight;
    private static final Image BOT_STILL_IMAGE = new Image("assets/bot/still.png");
    private static final Image BOT_WALK_IMAGE_1 = new Image("assets/bot/walk1.png");
    private static final Image BOT_WALK_IMAGE_2 = new Image("assets/bot/walk2.png");
    private int botId;

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
    public Bot(int x, int y, int lives, Stage stage) {
        final int mapWidthInTiles = 40;
        final int mapHeightInTiles = 25;
        botWidth = stage.widthProperty().get() / mapWidthInTiles * 1.5;
        botHeight = stage.heightProperty().get() / mapHeightInTiles * 1.5;
        this.setImage(BOT_STILL_IMAGE);
        this.fitWidthProperty().set(botWidth);
        this.fitHeightProperty().set(botHeight);
        this.setX(x);
        this.setY(y);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.lives = lives;
    }

    /**
     * Creates invisible boundaries for the bot.
     * Used for collision detection.
     *
     * @return Boundaries as a JavaFx Rectangle
     */
    private Rectangle boundaries() {
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
    public boolean collides(Player player) {
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
