package Game;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public class Player extends Rectangle {

    public int dx, dy, x, y, width, height;


    public Player(int x, int y, int width, int height, int dx, int dy) {
        super(x, y, width, height);
        this.setFill(Paint.valueOf("RED"));
        this.width = width;
        this.height = height;
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
