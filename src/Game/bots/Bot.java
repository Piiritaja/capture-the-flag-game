package Game.bots;

import Game.player.Bullet;
import Game.player.Player;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Bot extends ImageView {

    //Constants for player size
    private static double BOT_WIDTH = 80;
    private static double BOT_HEIGHT = 80;
    private static final Image BOT_STILL_IMAGE = new Image("assets/bot/still.png");
    private static final Image BOT_WALK_IMAGE_1 = new Image("assets/bot/walk1.png");
    private static final Image BOT_WALK_IMAGE_2 = new Image("assets/bot/walk2.png");
    private int botId;

    //Constants for bot model graphics

    public int dx, dy, x, y, lives;

    public Bot(int x, int y, int dx, int dy, int lives) {
        this.setImage(BOT_STILL_IMAGE);
        this.fitWidthProperty().set(BOT_WIDTH);
        this.fitHeightProperty().set(BOT_HEIGHT);
        this.dx = dx;
        this.dy = dy;
        this.setX(x);
        this.setY(y);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.lives = lives;
    }

    public void tick() {
        this.setX(this.x += dx);
        this.setY(this.y += dy);
    }

    private Rectangle boundaries() {
        Rectangle objectBoundaries = new Rectangle();
        objectBoundaries.setX(this.getX() + 5 * BOT_WIDTH / 16);
        objectBoundaries.setY(this.getY() + 5 * BOT_WIDTH / 16);
        objectBoundaries.setWidth(this.getFitWidth() - 5 * BOT_WIDTH / 8);
        objectBoundaries.setHeight(this.getFitHeight() - 5 * BOT_WIDTH / 8);
        return objectBoundaries;
    }

    public boolean collides(Player player) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(player.getX());
        playerBoundaries.setY(player.getY());
        playerBoundaries.setHeight(player.getHeight());
        playerBoundaries.setWidth(player.getWidth());
        return objectBoundaries.getBoundsInParent().intersects(playerBoundaries.getBoundsInParent());
    }

    public boolean collides(Bullet bullet) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(bullet.getCenterX() - bullet.getRadius());
        playerBoundaries.setY(bullet.getCenterY() - bullet.getRadius());
        playerBoundaries.setHeight(bullet.getRadius() * 2);
        playerBoundaries.setWidth(bullet.getRadius() * 2);
        return ((Path) Shape.intersect(bullet, objectBoundaries)).getElements().size() > 1;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setBotHeight(double botHeight) {
        BOT_HEIGHT = botHeight;
        this.setFitHeight(BOT_HEIGHT);
    }

    public void setBotWidth(double botWidth) {
        BOT_WIDTH = botWidth;
        this.setFitWidth(BOT_WIDTH);
    }

    public double getBotHeight() {
        return BOT_HEIGHT;
    }

    public double getBotWidth() {
        return BOT_WIDTH;
    }

    public int getBotLives() {
        return lives;
    }

    public void setBotId(int id) {
        this.botId = id;
    }

    public int getBotId() {
        return botId;
    }
}
