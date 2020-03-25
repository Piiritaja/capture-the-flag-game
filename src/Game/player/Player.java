package Game.player;

import Game.bots.Bot;
import Game.maps.Object;
import javafx.animation.Animation;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static java.lang.StrictMath.abs;


public class Player extends ImageView {

    //Constants for player size
    private static final int PLAYER_WIDTH = 34;
    private static final int PLAYER_HEIGHT = 34;
    private static final int COLUMNS = 4;
    private static final int COUNT = 4;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    //public ImageView imageView;
    Image image;
    Animation animation;


    //Constants for player model graphics
    private static final String RED_PLAYER_MAIN_IMAGE = "assets/player/red/still.png";
    private static final String GREEN_PLAYER_MAIN_IMAGE = "assets/player/green/still.png";
    //private static final String RED_PLAYER_WALKING = "assets/player/red/walkingRight.png";
    private Image walkingRightImage;
    private Image walkingLeftImage;
    private Image walkingUpImage;
    private Image walkingDownImage;
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
    public List<Bullet> bullets = new ArrayList<>();
    int step = 2;

    public int dx, dy, x, y, width, height;
    private playerColor color;

    private double playerLocationXInTiles;
    private double playerLocationYInTiles;

    public enum playerColor {
        RED(Color.RED),
        GREEN(Color.GREEN);

        public final Color color;

        playerColor(Color color) {
            this.color = color;
        }
    }


    public Player(int x, int y, int dx, int dy, playerColor color) {
        if (color.equals(playerColor.GREEN)) {
            image = new Image(GREEN_PLAYER_MAIN_IMAGE);
        } else if (color.equals(playerColor.RED)) {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
            walkingRightImage = new Image("assets/player/red/walkingRight.png");
            walkingLeftImage = new Image("assets/player/red/walkingLeft.png");
            walkingUpImage = new Image("assets/player/red/walkingUp.png");
            walkingDownImage = new Image("assets/player/red/walkingDown.png");
        } else {
            image = new Image(RED_PLAYER_MAIN_IMAGE);
        }
        this.setImage(image);
        this.setFitWidth(PLAYER_WIDTH);
        this.setFitHeight(PLAYER_HEIGHT);
        this.setX(x);
        this.setY(y);
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        this.dx = dx;
        this.dy = dy;
        this.color = color;
        this.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, PLAYER_WIDTH, PLAYER_HEIGHT));
        animation = new SpriteAnimation(
                this,
                Duration.millis(700),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                PLAYER_WIDTH, PLAYER_HEIGHT
        );
    }

    public void tick(List<Object> objectsOnMap, List<Bot> botsOnMap) {
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
        for (Bot bot : botsOnMap) {
            if (bot.collides(this)) {
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
                this.setImage(walkingUpImage);
                bullet = new Bullet((int) shootingUpX, (int) shootingUpY, 3, Color.YELLOW);
                bullet.shoot(lineUp, root, Math.min(500, shootingUpY - mouseY), bullets);
            } else if (getY() < mouseY && mouseX >= getX() - allowedLengthY && mouseX <= getX() + allowedLengthY) {
                this.setImage(walkingDownImage);
                bullet = new Bullet((int) shootingDownX, (int) shootingDownY, 3, Color.YELLOW);
                bullet.shoot(lineDown, root, Math.min(500, mouseY - shootingDownY), bullets);
            } else if (getX() < mouseX && mouseY >= getY() - allowedLengthX && mouseY <= getY() + allowedLengthX) {
                this.setImage(walkingRightImage);
                bullet = new Bullet((int) shootingRightX, (int) shootingRightY, 3, Color.YELLOW);
                bullet.shoot(lineRight, root, Math.min(500, mouseX - shootingRightX), bullets);
            } else if (getX() >= mouseX && mouseY >= getY() - allowedLengthX && mouseY <= getY() + allowedLengthX) {
                this.setImage(walkingLeftImage);
                bullet = new Bullet((int) shootingLeftX, (int) shootingLeftY, 3, Color.YELLOW);
                bullet.shoot(lineLeft, root, Math.min(500, shootingLeftX - mouseX), bullets);
            }
            root.getChildren().add(bullet);
            bullets.add(bullet);
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
            this.setImage(walkingUpImage);
            setDy(-step);
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            this.setImage(walkingDownImage);
            setDy(step);
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            this.setImage(walkingRightImage);
            setDx(step);
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            this.setImage(walkingLeftImage);
            setDx(-step);
        }
        animation.play();
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
        animation.pause();
    };


    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public double getWidth() {
        return this.getFitWidth();
    }

    public double getHeight() {
        return this.getFitHeight();
    }

    public playerColor getColor() {
        return color;
    }

    public void setRoot(Group root) {
        this.root = root;
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
}
