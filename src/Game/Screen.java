package Game;

import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.Battlefield;
import Game.maps.MapLoad;
import Game.maps.Object;
import Game.player.Bullet;
import Game.player.Flag;
import Game.player.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;

public class Screen extends Application {
    Player player;
    Group root;
    MapLoad mapLoad;
    Base greenBase;
    Base redBase;
    AnimationTimer timer;
    Stage stage;
    List<Object> objectsOnMap;
    BotSpawner botSpawner;
    Bullet bullet = new Bullet(0, 0, 3, Color.GREEN);


    // teams scores
    int redTeamScore = 0;
    int greenTeamScore = 0;

    Battlefield chosenMap = Battlefield.MAP1;
    Player.playerColor color = Player.playerColor.RED;


    //Constants for player object
    private int playerXStartingPosition = 20;
    private int playerYStartingPosition = 20;

    //Constants for flag object
    private Flag greenFlag;
    private Flag redFlag;


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
            this.playerXStartingPosition = (int) stage.widthProperty().get() - 100;
        } else if (color.equals(Player.playerColor.RED)) {
            this.playerXStartingPosition = 40;
        }
    }

    public void setPlayerYStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.playerYStartingPosition = (int) stage.heightProperty().get() - 500;
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
        player.setRoot(root);
    }

    public void setMap(int mapIndex) {
        if (mapIndex == 0) {
            chosenMap = Battlefield.MAP1;
        } else if (mapIndex == 1) {
            chosenMap = Battlefield.MAP2;
        }

    }


    @Override
    public void start(Stage stage) {
        boolean fullScreen = stage.isFullScreen();
        root = new Group();
        this.stage = stage;

        mapLoad = new MapLoad();


        if (chosenMap == Battlefield.MAP1) {
            mapLoad.loadMap1(root, stage);
        } else if (chosenMap == Battlefield.MAP2) {
            mapLoad.loadMap2(root, stage);

        }
        stage.getScene().setRoot(root);

        // both bases
        greenBase = mapLoad.getBaseByColor(Base.baseColor.GREEN);
        redBase = mapLoad.getBaseByColor(Base.baseColor.RED);

        // bases for collision detection
        List<Base> bases = mapLoad.getBases();
        botSpawner = new BotSpawner();
        botSpawner.spawnBots(4, stage, root, bases, mapLoad.getObjectsOnMap());

        setPlayerYStartingPosition(stage);
        setPlayerXStartingPosition(stage);

        createPlayer();

        root.getChildren().add(player);

        redFlag = mapLoad.getRedFlag();
        greenFlag = mapLoad.getGreenFlag();

        objectsOnMap = mapLoad.getObjectsOnMap();
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                player.tick(objectsOnMap, botSpawner.botsOnMap);
                catchTheFlag();
                scoreBoard();
                bullet.bulletCollision(player, objectsOnMap, root, botSpawner);
                player.setOnKeyPressed(player.pressed);
                player.setOnKeyReleased(player.released);
                root.setOnMouseClicked(player.shooting);
                player.setFocusTraversable(true);
            }
        };

        stage.setFullScreen(fullScreen);
        timer.start();
        stage.show();
    }

    // Player can take flag and release it in base
    public void catchTheFlag() {
        if (player.getColor() == Player.playerColor.RED) {
            if (player.getBoundsInParent().intersects(redFlag.getBoundsInParent())) {
                if (player.getX() > redBase.getRightX() - redBase.getRightX() / 5) {
                    redFlag.relocate(player.getX() + 10, player.getY() + 10);
                } else {
                    redFlag.relocate(redBase.getLeftX() + 50, redBase.getBottomY() / 2 - greenFlag.getHeight());
                    redTeamScore += 1;
                    timer.stop();
                    start(stage);
                }
            }
        } else {
            if (player.getBoundsInParent().intersects(greenFlag.getBoundsInParent())) {
                if (player.getX() < greenBase.getLeftX()) {
                    greenFlag.relocate(player.getX() + 10, player.getY() + 10);
                } else {
                    greenFlag.relocate(greenBase.getRightX() - 50,
                            greenBase.getBottomY() / 2);
                    greenTeamScore += 1;
                    timer.stop();
                    start(stage);
                }
            }
        }
    }

    // Scoreboard on screen
    public void scoreBoard() {
        Rectangle scoreBoard = new Rectangle(500, 40);
        scoreBoard.setFill(Color.LIGHTGRAY);
        Text redTeam = new Text("Red Team " + redTeamScore);
        Text greenTeam = new Text("Green Team " + greenTeamScore);
        redTeam.setFill(Color.RED);
        redTeam.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
        greenTeam.setFill(Color.GREEN);
        greenTeam.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
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
