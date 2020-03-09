package Game;

import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.MapLoad;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

import static java.lang.StrictMath.abs;


public class Screen extends Application {
    Player player;
    Flag flag;
    Group root;
    MapLoad mapLoad;
    Bullet bullet;
    battlefield chosenMap = battlefield.MAP1;
    Player.playerColor color = Player.playerColor.RED;

    // shooting coordinates
    double shootingRightX;
    double shootingRightY;
    double shootingUpX;
    double shootingUpY;
    double shootingDownX;
    double shootingDownY;
    double shootingLeftX;
    double shootingLeftY;

    //Constants for player object
    private int playerXStartingPosition = 20;
    private int playerYStartingPosition = 20;

    //Constants for flag object
    private static final int FLAG_X_STARTING_POSITION = 350;
    private static final int FLAG_Y_STARTING_POSITION = 350;
    private static final int FLAG_WIDTH = 10;
    private static final int FLAG_HEIGHT = 10;

    //Shooting calculations
    double halfLengthX;
    double halfLengthY;

    private static final double ASPECT_RATIO = 1.6;

    int step = 2;

    public enum battlefield {
        MAP1, MAP2
    }

    public Screen() {

        this.flag = new Flag(
                FLAG_X_STARTING_POSITION,
                FLAG_Y_STARTING_POSITION,
                FLAG_WIDTH,
                FLAG_HEIGHT,
                Color.BLACK
        );
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void setPlayerColor(int colorIndex) {
        if (colorIndex == 0) {
            this.color = Player.playerColor.GREEN;
        } else if (colorIndex == 1) {
            this.color = Player.playerColor.RED;
        }
    }

    public void setPlayerXStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.playerXStartingPosition = 40;
        } else if (color.equals(Player.playerColor.RED)) {
            this.playerXStartingPosition = (int) (stage.getWidth() - 40);
        }
    }

        public void setPlayerYStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.playerYStartingPosition = 40;
        } else if (color.equals(Player.playerColor.RED)) {
            this.playerYStartingPosition = (int) (stage.getHeight() - 40);
        }
    }

    public void createPlayer() {
        this.player = new Player(
                playerXStartingPosition,
                playerYStartingPosition,
                0,
                0,
                color.equals(Player.playerColor.GREEN) ? Player.playerColor.GREEN : Player.playerColor.RED
        );
    }

    public void setMap(int mapIndex) {
        if (mapIndex == 0) {
            chosenMap = battlefield.MAP1;
        } else if (mapIndex == 1) {
            chosenMap = battlefield.MAP2;
        }

    }

    @Override
    public void start(Stage stage) {
        root = new Group();
        System.out.println(stage.widthProperty());

        mapLoad = new MapLoad();


        if (chosenMap == battlefield.MAP1) {
            mapLoad.loadMap1(root, stage);
        } else if (chosenMap == battlefield.MAP2) {
            mapLoad.loadMap2(root, stage);

        }
        setPlayerXStartingPosition(stage);
        setPlayerYStartingPosition(stage);
        createPlayer();

        // bases for collision detection
        List<Base> bases = mapLoad.getBases();

        // for loop can be used to loop through bases and check collision

        BotSpawner botSpawner = new BotSpawner();
        root.getChildren().add(player);
        botSpawner.spawnBots(3, stage, root, bases);
        //root.getChildren().add(new Bot(200, 200, 0, 0));
        root.getChildren().add(flag);
        stage.getScene().setRoot(root);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                player.tick();
                catchTheFlag();
                player.setOnKeyPressed(pressed);
                player.setOnKeyReleased(released);
                root.setOnMouseClicked(shooting);
                player.setFocusTraversable(true);
            }
        };
        timer.start();
        stage.show();
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
            calculations(mouseX, mouseY);
            if (player.getY() >= mouseY && mouseX >= player.getX() - halfLengthY && mouseX <= player.getX() + halfLengthY) {
                bullet = new Bullet((int) shootingUpX, (int) shootingUpY, 5, 5, Color.YELLOW);
                bullet.shoot(lineUp, root, Math.min(500, shootingUpY - mouseY));
            } else if (player.getY() < mouseY && mouseX >= player.getX() - halfLengthY && mouseX <= player.getX() + halfLengthY) {
                bullet = new Bullet((int) shootingDownX, (int) shootingDownY, 5, 5, Color.YELLOW);
                bullet.shoot(lineDown, root, Math.min(500, mouseY - shootingDownY));
            } else if (player.getX() < mouseX && mouseY >= player.getY() - halfLengthX && mouseY <= player.getY() + halfLengthX) {
                bullet = new Bullet((int) shootingRightX, (int) shootingRightY, 5, 5, Color.YELLOW);
                bullet.shoot(lineRight, root, Math.min(500, mouseX - shootingRightX));
            } else if (player.getX() >= mouseX && mouseY >= player.getY() - halfLengthX && mouseY <= player.getY() + halfLengthX) {
                bullet = new Bullet((int) shootingLeftX, (int) shootingLeftY, 5, 5, Color.YELLOW);
                bullet.shoot(lineLeft, root, Math.min(500, shootingLeftX - mouseX));
            }
            root.getChildren().add(bullet);
        }
    };

    public void getGunCoordinates() {
        shootingRightX = player.getX() + player.getWidth();
        shootingRightY = player.getY() + player.getHeight();
        shootingUpX = player.getX() + player.getWidth();
        shootingUpY = player.getY();
        shootingDownX = player.getX();
        shootingDownY = player.getY() + player.getHeight();
        shootingLeftX = player.getX();
        shootingLeftY = player.getY();
    }

    // Calculations, to know where to shoot(left, right, up or down) if clicked on map
    public void calculations(double mouseX, double mouseY) {
        double heightX = abs(player.getX() - mouseX);
        double lengthX = Math.sqrt(Math.pow(heightX, 2) + Math.pow(heightX, 2));
        double triangleSX = (lengthX * lengthX) / 2;
        double allowedLengthX = (triangleSX * 2) / heightX;
        halfLengthX = allowedLengthX / 2;
        double heightY = abs(player.getY() - mouseY);
        double lengthY = Math.sqrt(Math.pow(heightY, 2) + Math.pow(heightY, 2));
        double triangleSY = (lengthY * lengthY) / 2;
        double allowedLengthY = (triangleSY * 2) / heightY;
        halfLengthY = allowedLengthY / 2;
    }

    // Player movement keyPressed
    public EventHandler<KeyEvent> pressed = keyEvent -> {
        if (keyEvent.getCode().equals(KeyCode.W)) {
            player.setDy(-step);
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            player.setDy(step);
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            player.setDx(step);
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            player.setDx(-step);
        }
    };

    // Player movement keyReleased
    public EventHandler<KeyEvent> released = keyEvent -> {
        if (keyEvent.getCode().equals(KeyCode.W)) {
            player.setDy(0);
        } else if (keyEvent.getCode().equals(KeyCode.S)) {
            player.setDy(0);
        } else if (keyEvent.getCode().equals(KeyCode.D)) {
            player.setDx(0);
        } else if (keyEvent.getCode().equals(KeyCode.A)) {
            player.setDx(0);
        }
    };

    // Player can take flag and release it in base
    public void catchTheFlag() {
        if (player.getBoundsInParent().intersects(flag.getBoundsInParent())) {
            if (!(player.getX() < 40 && player.getY() < 40)) {
                if (flag.getX() < 60 && flag.getY() < 60) {
                    flag.relocate(20, 20);
                } else {
                    flag.relocate(player.getX(), player.getY());
                }
            } else {
                flag.relocate(10, 10);
            }
        }
    }
}
