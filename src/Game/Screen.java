package Game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Line;



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

        Line horizontalLine = new Line();
        horizontalLine.setStartX(0); horizontalLine.setStartY(50);
        horizontalLine.setEndX(50); horizontalLine.setEndY(50);

        Line verticalLine = new Line();
        verticalLine.setStartX(50); verticalLine.setStartY(0);
        verticalLine.setEndX(50); verticalLine.setEndY(50);


        root.getChildren().add(player);
        root.getChildren().add(flag);
        root.getChildren().addAll(horizontalLine, verticalLine);
        stage.getScene().setRoot(root);

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
