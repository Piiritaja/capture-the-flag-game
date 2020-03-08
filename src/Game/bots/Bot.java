package Game.bots;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bot extends ImageView {

    //Constants for player size
    private static double BOT_WIDTH = 80;
    private static double BOT_HEIGHT = 80;
    private static final Image BOT_STILL_IMAGE = new Image("assets/bot/still.png");
    private static final Image BOT_WALK_IMAGE_1 = new Image("assets/bot/walk1.png");
    private static final Image BOT_WALK_IMAGE_2 = new Image("assets/bot/walk2.png");

    //Constants for bot model graphics

    public int dx, dy, x, y;

    public Bot (int x, int y, int dx, int dy) {
        this.setImage(BOT_STILL_IMAGE);
        this.fitWidthProperty().set(BOT_WIDTH);
        this.fitHeightProperty().set(BOT_HEIGHT);
        this.dx = dx;
        this.dy = dy;
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.setX(x);
        this.setY(y);
    }

    public void tick() {
        this.setX(this.x += dx);
        this.setY(this.y += dy);
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setBotHeight(double botHeight) {
        BOT_HEIGHT = botHeight;
        this.fitHeightProperty().set(BOT_HEIGHT);
    }

    public void setBotWidth(double botWidth) {
        BOT_WIDTH = botWidth;
        this.fitWidthProperty().set(BOT_WIDTH);
    }

    public double getBotHeight() {
        return BOT_HEIGHT;
    }

    public double getBotWidth() {
        return BOT_WIDTH;
    }
}
