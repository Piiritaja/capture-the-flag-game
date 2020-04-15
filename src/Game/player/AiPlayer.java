package Game.player;

import Game.Screen;
import Game.bots.Bot;
import Game.maps.Base;
import Game.maps.Object;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.StrictMath.abs;

/**
 * Player class.
 */
public class AiPlayer extends Player {


    String[][] objectPlacement;

    private Flag flag;
    private AnimationTimer timer;
    private Circle collisionBoundary = new Circle();
    private Circle shootingBoundary = new Circle();

    //Movement variables
    boolean left = true;
    boolean right = true;
    boolean up = true;
    boolean down = true;
    int primaryX = 1;
    int primaryY = 1;
    boolean leftFree = true;
    boolean rightFree = true;
    double shootingUpdateTimer = 0;

    private Base base;


    /**
     * Initializes player.
     * Sets size, color, image depending of the color, initial position.
     *
     * @param x     Initial x coordinate
     * @param y     Initial y coordinate
     * @param dx    Movement x change
     * @param dy    Movement y change
     * @param color Player color
     */
    public AiPlayer(int x, int y, int dx, int dy, GamePlayer.playerColor color, Flag flag, Group root, Base base) {
        super(x, y, dx, dy, color);
        this.base = base;
        this.flag = flag;
        //objectPlacement = Object.getObjectPlacements();
        //System.out.println(Arrays.deepToString(objectPlacement));

        this.root = root;

        collisionBoundary.setCenterX(this.getX() + this.getWidth() / 2);
        collisionBoundary.setCenterY(this.getY() + this.getHeight() / 2);
        root.getChildren().add(collisionBoundary);
        collisionBoundary.setFill(Color.YELLOW);
        collisionBoundary.setOpacity(0.3);

        shootingBoundary.setCenterX(this.getX() + this.getWidth() / 2);
        shootingBoundary.setCenterY(this.getY() + this.getHeight() / 2);
        root.getChildren().add(shootingBoundary);
        shootingBoundary.setFill(Color.BLUE);
        shootingBoundary.setOpacity(0.2);
    }

    /**
     * Player´s movement.
     * Collision with objects on map and bot object.
     *
     * @param objectsOnMap Objects to check the collision with.
     * @param botsOnMap    Bots to check the collision with.
     */
    public void tick(List<Object> objectsOnMap, List<Bot> botsOnMap, Stage stage, List<Player> players) {
        animation.pause();
        this.setImage(image);
        double x = this.getX();
        double y = this.getY();
        this.setFitWidth(stage.widthProperty().get() / Screen.getMAP_WIDTH_IN_TILES() * 1.5);
        this.setFitHeight(stage.heightProperty().get() / Screen.getMAP_HEIGHT_IN_TILES() * 1.5);

        double boundaryCenterX = x + this.getWidth() / 2;
        double boundaryRadius = this.getHeight() / 2;
        double boundaryCenterY = y + this.getHeight() / 2;
        setBoundaries(boundaryCenterX, boundaryCenterY, boundaryRadius);

        calculateDirection(x, y);
        final double shootingTimerStep = 0.05;
        shootingUpdateTimer += shootingTimerStep;
        move(objectsOnMap, x, y, boundaryCenterX, boundaryCenterY, primaryX, primaryY, leftFree, rightFree);
        for (Bot bot : botsOnMap) {
            if (bot.getBoundsInParent().intersects(shootingBoundary.getBoundsInParent()) && shootingUpdateTimer >= 1) {
                shootBot(bot);
                shootingUpdateTimer = 0.0;
            }
        }
        for (Player player : players) {
            if ((!player.equals(this)) && (!player.getColor().equals(this.color)) && shootingUpdateTimer >= 1 &&
            player.getBoundsInParent().intersects(shootingBoundary.getBoundsInParent())) {
                shootPlayer(player);
                shootingUpdateTimer = 0.0;
            }
        }

    }

    /**
     * Calculates the direction of movement for Ai.
     * @param x current X coordinate of Ai
     * @param y current Y coordinate of Ai.
     */
    private void calculateDirection(double x, double y) {
        double destinationX;
        double destinationY;
        primaryX = 1;
        primaryY = 1;
        leftFree = true;
        rightFree = true;

        if (!flag.isPickedUp()) {
            destinationX = flag.getX();
            destinationY = flag.getY();
        } else {
            destinationX = base.getRightX() - base.getWidth() / 2;
            destinationY = base.getTopY() + base.getHeight() / 2;
            catchTheFlag();
        }
        final int offset = 2;
        if (destinationX < x - offset) {
            primaryX = -1;
            left = true;
            right = false;
        } else if (destinationX > x + offset) {
            left = false;
            right = true;
        //else if (destinationX <= x + offset && destinationX >= x - offset)
        } else {
            left = false;
            right = false;
        }
        if (destinationY < y - offset) {
            primaryY = -1;
            up = true;
            down = false;
        } else if (destinationY > y + offset) {
            down = true;
            up = false;
        } else if (!left && !right) {
            up = false;
            down = false;
            if (!flag.isPickedUp()) {
                flag.pickUp();
                System.out.println("pickup");
            }
        }
    }

    /**
     * Moves to the given destination.
     * Checks collision.
     *
     * @param objectsOnMap    list of objects (walls) on map
     * @param x               current X coordinate
     * @param y               current Y coordinate
     * @param boundaryCenterX center X coordinate of the collision boundary
     * @param boundaryCenterY center Y coordinate of the collision boundary
     * @param primaryX        primary desired movement direction on X axis
     * @param primaryY        primary desired movemenet direction on Y axis
     * @param leftFree        boolean true if aiPlayer can move left
     * @param rightFree       boolean true if aiPlayer can move right
     */
    private void move(List<Object> objectsOnMap, double x, double y,
                      double boundaryCenterX, double boundaryCenterY,
                      int primaryX, int primaryY,
                      boolean leftFree, boolean rightFree) {
        boolean downFree;
        boolean upFree;
        for (Object object : objectsOnMap) {
            if (left) {
                collisionBoundary.setCenterX(boundaryCenterX - step);
                if (object.collides(collisionBoundary)) {
                    leftFree = false;
                    primaryX = 0;
                    down = true;
                    primaryY = 1;
                }
                collisionBoundary.setCenterX(boundaryCenterX);
            }
            if (right) {
                collisionBoundary.setCenterX(boundaryCenterX + step);
                if (object.collides(collisionBoundary)) {
                    rightFree = false;
                    primaryX = 0;
                    down = true;
                    primaryY = 1;
                }
                collisionBoundary.setCenterX(boundaryCenterX);
            }
            if (down) {
                collisionBoundary.setCenterY(boundaryCenterY + step);
                if (object.collides(collisionBoundary)) {
                    downFree = false;
                    primaryY = -1;
                    up = true;
                }
                collisionBoundary.setCenterY(boundaryCenterY);
            }
            if (up) {
                collisionBoundary.setCenterY(boundaryCenterY - step);
                if (object.collides(collisionBoundary)) {
                    upFree = false;
                    primaryY = 0;
                }
                collisionBoundary.setCenterY(boundaryCenterY);
            }
        }
        if ((left && leftFree) || (right && rightFree)) {
            this.setX(x + step * primaryX);
            if (primaryX == -1) {
                this.setImage(walkingLeftImage);
            } else {
                this.setImage(walkingRightImage);
            }
        } else {
            this.setY(y + step * primaryY);
            if (primaryY == -1) {
                this.setImage(walkingUpImage);
            } else {
                this.setImage(walkingDownImage);
            }
        }
        animation.play();
    }


    /**
     * Set the collision and bot/player/shooting boundary sizes and positions.
     * @param centerX center of the player X / center of the boundary X
     * @param centerY center of the player Y / center of the boundary Y
     * @param radius radius of the collision boundary
     */
    private void setBoundaries(double centerX, double centerY, double radius) {
        collisionBoundary.setCenterX(centerX);
        collisionBoundary.setCenterY(centerY);
        collisionBoundary.setRadius(radius);
        shootingBoundary.setCenterX(centerX);
        shootingBoundary.setCenterY(centerY);
        shootingBoundary.setRadius(radius * 4);

    }

    /**
     * Player can catch the enemy team`s flag if intersects with it and bring to his base.
     * If enemy team`s flag is brought to own base then the next round starts.
     */
    public void catchTheFlag() {
        if (collisionBoundary.getBoundsInParent().intersects(flag.getBoundsInParent())) {
            flag.setX(this.getX());
            flag.setY(this.getY());
        }
    }

    /**
     * Calculates which way to shoot(UP, DOWN, RIGHT or LEFT).
     */
    public void shootBot(Bot bot) {
        getGunCoordinates();
        double y = bot.getY() + bot.getBotHeight() / 2;
        double x = bot.getX() + bot.getBotWidth() / 2;
        shoot(x, y);

    }

    /**
     * Calculates which way to shoot(UP, DOWN, RIGHT or LEFT).
     */
    public void shootPlayer(Player player) {
        getGunCoordinates();
        double y = player.getY() + player.getHeight() / 2;
        double x = player.getX() + player.getWidth() / 2;
        shoot(x, y);
    }

    /**
     * If called, makes new bullet and adds it to the root and bullets list.
     * Sets player image in the same direction with bullets.
     */
    public void shoot(double x, double y) {
        getGunCoordinates();
        Line lineRight = new Line(shootingRightX, shootingRightY, Math.min(shootingRightX + 500, x), y);
        Line lineLeft = new Line(shootingLeftX, shootingLeftY, Math.max(shootingLeftX - 500, x), y);
        Line lineDown = new Line(shootingDownX, shootingDownY, x, Math.min(shootingDownY + 500, y));
        Line lineUp = new Line(shootingUpX, shootingUpY, x, Math.max(shootingUpY - 500, y));
        double allowedLengthX = abs(getX() - x);
        double allowedLengthY = abs(getY() - y);
        animation.pause();
        if (getY() >= y && x >= getX() - allowedLengthY && x <= getX() + allowedLengthY) {
            this.setImage(walkingUpImage);
            bullet = new Bullet((int) shootingUpX, (int) shootingUpY, 3, Color.YELLOW);
            bullet.shoot(lineUp, root, Math.min(500, shootingUpY - y), bullets);
        } else if (getY() < y && x >= getX() - allowedLengthY && x <= getX() + allowedLengthY) {
            this.setImage(walkingDownImage);
            bullet = new Bullet((int) shootingDownX, (int) shootingDownY, 3, Color.YELLOW);
            bullet.shoot(lineDown, root, Math.min(500, y - shootingDownY), bullets);
        } else if (getX() < x && y >= getY() - allowedLengthX && y <= getY() + allowedLengthX) {
            this.setImage(walkingRightImage);
            bullet = new Bullet((int) shootingRightX, (int) shootingRightY, 3, Color.YELLOW);
            bullet.shoot(lineRight, root, Math.min(500, x - shootingRightX), bullets);
        } else if (getX() >= x && y >= getY() - allowedLengthX && y <= getY() + allowedLengthX) {
            this.setImage(walkingLeftImage);
            bullet = new Bullet((int) shootingLeftX, (int) shootingLeftY, 3, Color.YELLOW);
            bullet.shoot(lineLeft, root, Math.min(500, shootingLeftX - x), bullets);
        }
        animation.play();
        root.getChildren().add(bullet);
        bullets.add(bullet);
    }




    /*
    private Map<String, Integer> getPosInTiles(Stage stage) {
        Map<String, Integer> positions = new HashMap<>();
        double stageWidth = stage.widthProperty().get();
        double stageHeight = stage.heightProperty().get();
        int xPos;
        int yPos;

        xPos = (int) ((this.getX() * Screen.getMAP_WIDTH_IN_TILES()) / stageWidth);
        yPos = (int) (this.getY() * Screen.getMAP_HEIGHT_IN_TILES() / stageHeight);
        positions.put("x", xPos);
        positions.put("y", yPos);
        return positions;
    }

    private Map<String, Integer> getNextPos(Map<String, Integer> currentPos) {
        int currentX = currentPos.get("x");
        int currentY = currentPos.get("y");
        double flagX = flag.getX();
        double flagY = flag.getY();
        double aiX = this.getX();
        double aiY = this.getY();
        Map<String, Integer> nextPos = new HashMap<>();
        nextPos.put("x", currentX);
        nextPos.put("y", currentY);
        if (!flag.isPickedUp()) {
            if (flagX > aiX) {
                nextPos.put("x", currentX + 1);
            } else if (flagX < aiX) {
                nextPos.put("x", currentX - 1);
            }
            if (flagY > aiY) {
                nextPos.put("y", currentY + 1);
            } else if (flagY < aiY) {
                nextPos.put("y", currentY - 1);
            }
            if (nextPos.get("y") == currentY && nextPos.get("x") == currentX) {
                flag.pickUp();
            }
        }
        return nextPos;
    }*/
}
