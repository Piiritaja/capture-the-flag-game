package Game.player;

import Game.maps.Object;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.Objects;

import static java.lang.StrictMath.abs;


public class Player extends ImageView {

    //Constants for player size
    private static final int PLAYER_WIDTH = 32;
    private static final int PLAYER_HEIGHT = 32;

    //Constants for player model graphics
    private static final String RED_PLAYER_MAIN_IMAGE = "assets/player/red/still.png";
    private static final String GREEN_PLAYER_MAIN_IMAGE = "assets/player/green/still.png";
    private Group root;

    // shooting coordinates
    double shootingRightX;
    double shootingRightY;
    double shootingUpX;
    double shootingUpY;
    double shootingDownX;
    double shootingDownY;
    double shootingLeftX;
    double shootingLeftY;

    Bullet bullet;
    int step = 2;

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
                this.setX(this.x -= dx);
                this.setY(this.y -= dy);
            }
        }
    }

    public EventHandler<MouseEvent> shooting = mouseEvent -> {
        getGunCoordinates();
        double mouseY = mouseEvent.getY();
        double mouseX = mouseEvent.getX();
        Line lineRight = new Line(shootingRightX, shootingRightY, Math.min(shootingRightX + 500, mouseX), mouseY);
        Line lineLeft = new Line(shootingLeftX, shootingLeftY, Math.max(shootingLeftX - 500, mouseX), mouseY);
        Line lineDown = new Line(shootingDownX, shootingDownY, mouseX, Math.min(shootingDownY + 500, mouseY));
        Line lineUp = new Line(shootingUpX, shootingUpY, mouseX, Math.max(shootingUpY - 500, mouseY));
        if (Objects.equals(mouseEvent.getEventType(), MouseEvent.MOUSE_CLICKED)) {
            double allowedLengthX = abs(getX() - mouseX);
            double allowedLengthY = abs(getY() - mouseY);
            if (getY() >= mouseY && mouseX >= getX() - allowedLengthY && mouseX <= getX() + allowedLengthY) {
                bullet = new Bullet((int) shootingUpX, (int) shootingUpY, 3, Color.YELLOW);
                bullet.shoot(lineUp, root, Math.min(500, shootingUpY - mouseY));
            } else if (getY() < mouseY && mouseX >= getX() - allowedLengthY && mouseX <= getX() + allowedLengthY) {
                bullet = new Bullet((int) shootingDownX, (int) shootingDownY, 3, Color.YELLOW);
                bullet.shoot(lineDown, root, Math.min(500, mouseY - shootingDownY));
            } else if (getX() < mouseX && mouseY >= getY() - allowedLengthX && mouseY <= getY() + allowedLengthX) {
                bullet = new Bullet((int) shootingRightX, (int) shootingRightY, 3, Color.YELLOW);
                bullet.shoot(lineRight, root, Math.min(500, mouseX - shootingRightX));
            } else if (getX() >= mouseX && mouseY >= getY() - allowedLengthX && mouseY <= getY() + allowedLengthX) {
                bullet = new Bullet((int) shootingLeftX, (int) shootingLeftY, 3, Color.YELLOW);
                bullet.shoot(lineLeft, root, Math.min(500, shootingLeftX - mouseX));
            }
            root.getChildren().add(bullet);
        }
    };

    // From where bullets come out
    public void getGunCoordinates() {
        shootingRightX = getX() + getWidth();
        shootingRightY = getY() + getHeight();
        shootingUpX = getX() + getWidth();
        shootingUpY = getY();
        shootingDownX = getX();
        shootingDownY = getY() + getHeight();
        shootingLeftX = getX();
        shootingLeftY = getY();
    }

    // Player movement keyPressed
    public EventHandler<KeyEvent> pressed = keyEvent -> {
        if (keyEvent.getCode().equals(KeyCode.W)) {
            setDy(-step);
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            setDy(step);
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            setDx(step);
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            setDx(-step);
        }
    };

    // Player movement keyReleased
    public EventHandler<KeyEvent> released = keyEvent -> {
        if (keyEvent.getCode().equals(KeyCode.W)) {
            setDy(0);
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            setDy(0);
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            setDx(0);
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            setDx(0);
        }
    };


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

    public void setRoot(Group root) {
        this.root = root;
    }
}
