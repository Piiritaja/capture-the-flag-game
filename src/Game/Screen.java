package Game;

import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.MapLoad;
import Game.maps.Object;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.Math.*;


import java.util.List;
import java.util.Objects;

import static java.lang.StrictMath.abs;


public class Screen extends Application {
    Player player;
    Flag redFlag;
    Flag greenFlag;
    Group root;
    MapLoad mapLoad;
    Bullet bullet;
    Base greenBase;
    Base redBase;
    AnimationTimer timer;
    Stage stage;

    // teams scores
    int redTeamScore = 0;
    int greenTeamScore = 0;

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
        player = new Player(
                PLAYER_X_STARTING_POSITION,
                PLAYER_Y_STARTING_POSITION,
                0,
                0
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
            System.out.println("green");
            this.playerXStartingPosition = (int) stage.widthProperty().get() - 80;
            System.out.println(this.playerXStartingPosition);
        } else if (color.equals(Player.playerColor.RED)) {
            this.playerXStartingPosition = 40;
        }
    }

    public void setPlayerYStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.playerYStartingPosition = (int) stage.heightProperty().get() - 40;
            System.out.println(this.playerYStartingPosition);
        } else if (color.equals(Player.playerColor.RED)) {
            this.playerYStartingPosition = 40;
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
        boolean fullScreen = stage.isFullScreen();
        root = new Group();
        System.out.println(stage.widthProperty());
        this.stage = stage;

        mapLoad = new MapLoad();


        if (chosenMap == battlefield.MAP1) {
            mapLoad.loadMap1(root, stage);
        } else if (chosenMap == battlefield.MAP2) {
            mapLoad.loadMap2(root, stage);

        }
        stage.setScene(new Scene(root));

        // both bases
        greenBase = mapLoad.getBaseByColor(Base.baseColor.GREEN);
        redBase = mapLoad.getBaseByColor(Base.baseColor.RED);

        // bases for collision detection
        List<Base> bases = mapLoad.getBases();
        BotSpawner botSpawner = new BotSpawner();
        botSpawner.spawnBots(4, stage, root, bases, mapLoad.getObjectsOnMap());

        setPlayerYStartingPosition(stage);
        setPlayerXStartingPosition(stage);

        createPlayer();


        //both flags
        redFlag = new Flag(
                (int) (greenBase.getRightX() - 50),
                (int) (greenBase.getBottomY() / 2),
                FLAG_WIDTH,
                FLAG_HEIGHT,
                Flag.flagColor.RED);

        greenFlag = new Flag(
                (int) redBase.getRightX() - 50,
                (int) redBase.getBottomY() / 2,
                FLAG_WIDTH,
                FLAG_HEIGHT,
                Flag.flagColor.GREEN);


        // for loop can be used to loop through bases and check collision
        root.getChildren().add(player);
        //root.getChildren().add(new Bot(200, 200, 0, 0));
        root.getChildren().add(redFlag);
        stage.getScene().setRoot(root);




        List<Object> objectsOnMap = mapLoad.getObjectsOnMap();
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                player.tick(objectsOnMap);
                catchTheFlag();
                scoresCount();
                scoreBoard();
                player.setOnKeyPressed(pressed);
                player.setOnKeyReleased(released);
                root.setOnMouseClicked(shooting);
                player.setFocusTraversable(true);
            }
        };
        stage.setFullScreen(fullScreen);
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
            double allowedLengthX = abs(player.getX() - mouseX);
            double allowedLengthY = abs(player.getY() - mouseY);
            if (player.getY() >= mouseY && mouseX >= player.getX() - allowedLengthY && mouseX <= player.getX() + allowedLengthY) {
                bullet = new Bullet((int) shootingUpX, (int) shootingUpY, 3, Color.YELLOW);
                bullet.shoot(lineUp, root, Math.min(500, shootingUpY - mouseY));
            } else if (player.getY() < mouseY && mouseX >= player.getX() - allowedLengthY && mouseX <= player.getX() + allowedLengthY) {
                bullet = new Bullet((int) shootingDownX, (int) shootingDownY, 3, Color.YELLOW);
                bullet.shoot(lineDown, root, Math.min(500, mouseY - shootingDownY));
            } else if (player.getX() < mouseX && mouseY >= player.getY() - allowedLengthX && mouseY <= player.getY() + allowedLengthX) {
                bullet = new Bullet((int) shootingRightX, (int) shootingRightY, 3, Color.YELLOW);
                bullet.shoot(lineRight, root, Math.min(500, mouseX - shootingRightX));
            } else if (player.getX() >= mouseX && mouseY >= player.getY() - allowedLengthX && mouseY <= player.getY() + allowedLengthX) {
                bullet = new Bullet((int) shootingLeftX, (int) shootingLeftY, 3, Color.YELLOW);
                bullet.shoot(lineLeft, root, Math.min(500, shootingLeftX - mouseX));
            }
            root.getChildren().add(bullet);
        }
    };

    // From where bullets come out
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
        if (player.getBoundsInParent().intersects(redFlag.getBoundsInParent())) {
            if (!(player.getX() < redBase.getRightX())) {
                if (redFlag.getX() < 60 && redFlag.getY() < 60) {
                    redFlag.relocate(redBase.getLeftX() + 50, redBase.getBottomY() / 2);
                } else {
                    redFlag.relocate(player.getX(), player.getY());
                }
            } else {
                redFlag.relocate(redBase.getLeftX() + 50, redBase.getBottomY() / 2);
                timer.stop();
            }
        }
    }

    // Counts teams scores
    public void scoresCount() {
            if (redFlag.getBoundsInParent().intersects(redBase.getBoundsInParent())) {
                redTeamScore += 1;
            } else if (greenFlag.getBoundsInParent().intersects(greenBase.getBoundsInParent())) {
                greenTeamScore += 1;
        }
    }

    // Scoreboard on screen
    public void scoreBoard() {
        Rectangle scoreBoard = new Rectangle(500, 40);
        scoreBoard.setFill(Color.LIGHTGRAY);
        Text redTeam = new Text("Red Team " + redTeamScore / 2);
        Text greenTeam = new Text("Green Team " + greenTeamScore / 2);
        redTeam.setFill(Color.RED); redTeam.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
        greenTeam.setFill(Color.GREEN); greenTeam.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
        StackPane stack = new StackPane();
        GridPane scores = new GridPane();
        stack.setLayoutX(stage.widthProperty().get() / 2 - (scoreBoard.getWidth() / 2));
        scores.setHgap(40);
        stack.getChildren().add(scoreBoard);
        scores.add(redTeam, 1, 0);
        scores.add(greenTeam, 2, 0);
        stack.getChildren().add(scores);
        root.getChildren().add(stack);
    }
}
