package Game.player;

import Game.Screen;
import Game.bots.Bot;
import Game.maps.Base;
import Game.maps.Object;
import com.esotericsoftware.kryonet.Client;
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
import networking.packets.Packet010PlayerMovement;
import networking.packets.Packet017GamePlayerShoot;

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
    public Circle collisionBoundary = new Circle();
    private Circle shootingBoundary = new Circle();
    private Client client;
    private boolean master;

    //Movement variables
    boolean left = true;
    boolean right = true;
    boolean up = true;
    boolean down = true;
    int primaryX = 1;
    int primaryY = 1;
    private PrimaryMovementDirection primaryMovementDirection;
    boolean leftFree = true;
    boolean rightFree = true;
    double shootingRateTimer = 0;
    private int shootAndMoveTimer = 0;

    private Base base;

    public enum PrimaryMovementDirection {UP, DOWN}


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
    public AiPlayer(int x, int y, int dx, int dy, GamePlayer.playerColor color, Flag flag, Group root, Base base, Client client, boolean master) {
        super(x, y, dx, dy, color);
        this.base = base;
        this.flag = flag;
        //objectPlacement = Object.getObjectPlacements();
        //System.out.println(Arrays.deepToString(objectPlacement));
        this.client = client;
        this.master = master;

        final int randomMovementRange = 150;
        double directionRandomRange = ((randomMovementRange - 1) + 1);
        int randomDir = (int) (Math.random() * directionRandomRange) + 1;
        if (randomDir % 2 == 0) {
            primaryMovementDirection = PrimaryMovementDirection.UP;
        } else {
            primaryMovementDirection = PrimaryMovementDirection.DOWN;
        }

        this.root = root;

        collisionBoundary.setCenterX(this.getX() + this.getWidth() / 2);
        collisionBoundary.setCenterY(this.getY() + this.getHeight() / 2);

        shootingBoundary.setCenterX(this.getX() + this.getWidth() / 2);
        shootingBoundary.setCenterY(this.getY() + this.getHeight() / 2);

        root.getChildren().add(collisionBoundary);
        root.getChildren().add(shootingBoundary);

    }

    /**
     * Player´s movement.
     * Collision with objects on map and bot object.
     *
     * @param objectsOnMap Objects to check the collision with.
     * @param botsOnMap    Bots to check the collision with.
     */
    public void tick(List<Object> objectsOnMap, List<Bot> botsOnMap, Stage stage, List<Player> players,
                     List<Player> deadPlayers) {
        boolean shot = false;
        shootAndMoveTimer += 5;
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
        if (shootingRateTimer == 0.5) {
            collisionBoundary.setOpacity(0);
            shootingBoundary.setOpacity(0);
        }

        if (shootingRateTimer >= 1) {
            for (Bot bot : botsOnMap) {
                if (shootingBoundary.getBoundsInParent().intersects(bot.boundaries().getBoundsInParent())) {
                    shootBot(bot);
                    shootingRateTimer = 0.0;
                    shot = true;
                }
            }
            for (Player player : players) {
                if ((!player.equals(this))
                        && (!player.getColor().equals(this.color))
                        && !deadPlayers.contains(player)
                        && shootingBoundary.getBoundsInParent().intersects(player.boundaries().getBoundsInParent())) {
                    shootPlayer(player);
                    shootingRateTimer = 0.0;
                    shot = true;
                }
            }
        }

        calculateDirection(x, y);
        final double shootingRateStep = 0.05;
        shootingRateTimer += shootingRateStep;
        if (shot) {
            shootAndMoveTimer = 0;
            animation.play();
        }

        if (shootAndMoveTimer > 100) {
            move(objectsOnMap, x, y, boundaryCenterX, boundaryCenterY, primaryX, primaryY, leftFree, rightFree);
        }


    }

    /**
     * Calculates the direction of movement for Ai.
     *
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
        double baseMiddleX = base.getRightX() - base.getWidth() / 2;

        // If the flag is already picked up and the AI is in it's home base, it moves up and down in it's home base
        final double baseMovementOffset = 80;
        if (collisionBoundary.getBoundsInParent().intersects(base.getBoundsInParent())
                && (x >= baseMiddleX - baseMovementOffset && x <= baseMiddleX + baseMovementOffset)
                && flag.isPickedUp()) {
            if (primaryMovementDirection.equals(PrimaryMovementDirection.UP) && y <= base.getTopY() + baseMovementOffset) {
                down = true;
                primaryY = 1;
                up = false;
                primaryMovementDirection = PrimaryMovementDirection.DOWN;
            } else if (primaryMovementDirection.equals(PrimaryMovementDirection.DOWN)
                    && y >= base.getBottomY() - baseMovementOffset) {
                up = true;
                down = false;
                primaryY = -1;
                primaryMovementDirection = PrimaryMovementDirection.UP;
            } else {
                if (primaryMovementDirection.equals(PrimaryMovementDirection.UP)) {
                    primaryY = -1;
                    up = true;
                    down = false;
                } else {
                    primaryY = 1;
                    primaryMovementDirection = PrimaryMovementDirection.DOWN;
                    down = true;
                    up = false;
                }
            }
            return;
        }

        if (!flag.isPickedUp()) {
            destinationX = flag.getX();
            destinationY = flag.getY();
        } else {
            destinationX = baseMiddleX;
            destinationY = base.getTopY() + base.getHeight() / 2;
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
            primaryY = 1;
            down = true;
            up = false;
        } else if (!left && !right) {
            up = false;
            down = false;
            if (!flag.isPickedUp()) {
                flag.pickUp();
                flag.relocate(this.getX(), this.getY());
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
                }
                if (primaryMovementDirection.equals(PrimaryMovementDirection.DOWN)) {
                    down = true;
                    up = false;
                    primaryY = 1;
                } else {
                    down = false;
                    up = true;
                    primaryY = -1;
                }
                collisionBoundary.setCenterX(boundaryCenterX);
            }
            if (right) {
                collisionBoundary.setCenterX(boundaryCenterX + step);
                if (object.collides(collisionBoundary)) {
                    rightFree = false;
                    primaryX = 0;
                    if (primaryMovementDirection.equals(PrimaryMovementDirection.DOWN)) {
                        down = true;
                        up = false;
                        primaryY = 1;
                    } else {
                        down = false;
                        up = true;
                        primaryY = -1;
                    }
                }
                collisionBoundary.setCenterX(boundaryCenterX);
            }
            if (down) {
                collisionBoundary.setCenterY(boundaryCenterY + step);
                if (object.collides(collisionBoundary)) {
                    downFree = false;
                    primaryY = 0;
                    up = true;
                }
                collisionBoundary.setCenterY(boundaryCenterY);
            }
            if (up) {
                collisionBoundary.setCenterY(boundaryCenterY - step);
                if (object.collides(collisionBoundary)) {
                    upFree = false;
                    primaryY = 0;
                    down = true;
                }
                collisionBoundary.setCenterY(boundaryCenterY);
            }
        }
        Packet010PlayerMovement playerMovement = new Packet010PlayerMovement();
        playerMovement.playerId = this.getId();

        if ((left && leftFree) || (right && rightFree)) {
            this.setX(x + step * primaryX);
            if (primaryX == -1) {
                playerMovement.direction = 4;
                movementPositionLeft();
            } else if (primaryX == 1) {
                playerMovement.direction = 3;
                movementPositionRight();
            }
        } else {
            this.setY(y + step * primaryY);
            if (primaryY == -1) {
                playerMovement.direction = 1;
                movementPositionUp();
            } else if (primaryY == 1) {
                playerMovement.direction = 2;
                movementPositionDown();
            }
        }
        client.sendUDP(playerMovement);
    }

    public void movementPositionUp() {
        this.setImage(walkingUpImage);
        animation.play();
    }

    public void movementPositionDown() {
        this.setImage(walkingDownImage);
        animation.play();
    }

    public void movementPositionLeft() {
        this.setImage(walkingLeftImage);
        animation.play();
    }

    public void movementPositionRight() {
        this.setImage(walkingRightImage);
        animation.play();
    }


    /**
     * Set the collision and bot/player/shooting boundary sizes and positions.
     *
     * @param centerX center of the player X / center of the boundary X
     * @param centerY center of the player Y / center of the boundary Y
     * @param radius  radius of the collision boundary
     */
    private void setBoundaries(double centerX, double centerY, double radius) {
        collisionBoundary.setCenterX(centerX);
        collisionBoundary.setCenterY(centerY);
        collisionBoundary.setRadius(radius);
        shootingBoundary.setCenterX(centerX);
        shootingBoundary.setCenterY(centerY);
        shootingBoundary.setRadius(radius * 4);

        shootingBoundary.setFill(Color.BLUE);

        collisionBoundary.setFill(Color.YELLOW);

        if (shootingRateTimer == 0) {
            collisionBoundary.setOpacity(0.3);
            shootingBoundary.setOpacity(0.2);
        }

    }


    /**
     * Calculates which way to shoot(UP, DOWN, RIGHT or LEFT).
     */
    public void shootBot(Bot bot) {
        Packet017GamePlayerShoot gamePlayerShoot = new Packet017GamePlayerShoot();
        getGunCoordinates();
        double y = bot.getY() + bot.getBotHeight() / 2;
        double x = bot.getX() + bot.getBotWidth() / 2;
        gamePlayerShoot.playerId = this.getId();
        gamePlayerShoot.mouseX = x;
        gamePlayerShoot.mouseY = y;
        client.sendUDP(gamePlayerShoot);
        shoot(x, y, true);

    }

    /**
     * Calculates which way to shoot(UP, DOWN, RIGHT or LEFT).
     */
    public void shootPlayer(Player player) {
        Packet017GamePlayerShoot gamePlayerShoot = new Packet017GamePlayerShoot();
        getGunCoordinates();
        double y = player.getY() + player.getHeight() / 2;
        double x = player.getX() + player.getWidth() / 2;
        gamePlayerShoot.playerId = this.getId();
        gamePlayerShoot.mouseX = x;
        gamePlayerShoot.mouseY = y;
        client.sendUDP(gamePlayerShoot);
        shoot(x, y, true);
    }

    @Override
    public void setPlayerXStartingPosition(Base greenBase, Base redBase) {
        this.setX((int) calcPlayerXStartingPosition(greenBase, redBase, color));
    }

    @Override
    public void setPlayerYStartingPosition(Base greenBase, Base redBase) {
        this.setY((int) calcPlayerYStartingPosition(greenBase, redBase, color));
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
