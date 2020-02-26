package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


public class Player extends ImageView {

    public int dx, dy, x, y, width, height;


    public Player(int x, int y, int width, int height, int dx, int dy, Color color) {
        Image image = new Image("assets/player/green/still.png");
        this.setImage(image);
        this.width = 32;
        this.height = 32;
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
