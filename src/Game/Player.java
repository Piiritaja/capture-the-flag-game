package Game;

import Game.maps.Object;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;


public class Player extends ImageView {

    //Constants for player size
    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 32;

    //Constants for player model graphics
    private static final String RED_PLAYER_MAIN_IMAGE = "assets/player/red/still.png";
    private static final String GREEN_PLAYER_MAIN_IMAGE = "assets/player/green/still.png";

    public int dx, dy, x, y, width, height;

    public enum playerColor {
        RED, GREEN
    }


    public Player(int x, int y, int dx, int dy, playerColor color) {
        Image image;
        if (color.equals(playerColor.GREEN)) {
            image = new Image(GREEN_PLAYER_MAIN_IMAGE);

        } else if (color.equals(playerColor.RED)) {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
        } else {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
        }
        this.setImage(image);
        this.width = PLAYER_WIDTH;
        this.height = PLAYER_HEIGHT;
        this.setX(x);
        this.setY(y);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.dx = dx;
        this.dy = dy;
    }

    public void tick(List<Object> objectsOnMap) {
        double x = this.getX();
        double y = this.getY();
        this.setX(this.x += dx);
        this.setY(this.y += dy);
        for (Object object : objectsOnMap) {
            if (object.collides(this)) {
                this.setX(x);
                this.setY(y);
                dx = 0;
                dy = 0;
            }
        }
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
