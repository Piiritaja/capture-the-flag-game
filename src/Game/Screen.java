package Game;

import Game.maps.MapLoad;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;



public class Screen extends Application {
    Player player;
    Flag flag;

    //Constants for player object
    private static final int PLAYER_X_STARTING_POSITION = 20;
    private static final int PLAYER_Y_STARTING_POSITION = 20;

    //Constants for flag object
    private static final int FLAG_X_STARTING_POSITION = 350;
    private static final int FLAG_Y_STARTING_POSITION = 350;
    private static final int FLAG_WIDTH = 10;
    private static final int FLAG_HEIGHT = 10;

    private static final double ASPECT_RATIO = 1.6;

    int step = 2;

    public Screen(){
        this.player = new Player(
                PLAYER_X_STARTING_POSITION,
                PLAYER_Y_STARTING_POSITION,
                0,
                0
        );

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

    @Override
    public void start(Stage stage) {
        Group root = new Group();

        MapLoad mapLoad = new MapLoad();

        // loadMap2(), for map 2;
        // loadMap1(), for map 1;
        StackPane map = mapLoad.loadMap2();

        map.prefWidthProperty().bind(stage.getScene().widthProperty());
        map.prefHeightProperty().bind(stage.getScene().heightProperty());

        root.getChildren().add(map);
        root.getChildren().add(player);
        root.getChildren().add(flag);
        stage.getScene().setRoot(root);

        stage.minWidthProperty().bind(map.heightProperty().multiply(ASPECT_RATIO));
        stage.minHeightProperty().bind(map.widthProperty().divide(ASPECT_RATIO));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                player.tick();
                catchTheFlag();
                player.setOnKeyPressed(pressed);
                player.setOnKeyReleased(released);
                player.setFocusTraversable(true);
            }
        };
        timer.start();
        stage.show();
    }

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
