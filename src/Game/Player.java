package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Player extends ImageView {

    //Constants for player size
    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 32;

    //Constants for player model graphics
    private static final String PLAYER_MAIN_IMAGE = "assets/player/green/still.png";

    public int dx, dy, x, y, width, height;


    public Player(int x, int y, int dx, int dy) {
        Image image = new Image(PLAYER_MAIN_IMAGE);
        this.setImage(image);
        this.width = PLAYER_WIDTH;
        this.height = PLAYER_HEIGHT;
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.dx = dx;
        this.dy = dy;
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
}
