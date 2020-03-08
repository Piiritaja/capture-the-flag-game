package Game;

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


public class Screen extends Application {
    Player player;
    Flag flag;
    Group root;
    MapLoad mapLoad;
    Bullet bullet;

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

    public Screen() {
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
        root = new Group();
        System.out.println(stage.widthProperty());

        mapLoad = new MapLoad();

        // loadMap2(), for map 2;
        // loadMap1(), for map 1;
        mapLoad.loadMap2(root, stage);

        // bases for collision detection
        List<Base> bases = mapLoad.getBases();

        // for loop can be used to loop through bases and check collision

        root.getChildren().add(player);
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
        Line lineRight = new Line(player.getX(), player.getY(), player.getX() + 500, player.getY());
        Line lineLeft = new Line(player.getX(), player.getY(), player.getX() - 500, player.getY());
        //Line lineDown = new Line(player.getX(), player.getY(), player.getX(), player.getY() + 500);
        //Line lineUp = new Line(player.getX(), player.getY(), player.getX(), player.getY() - 500);
        bullet = new Bullet((int) player.getX(), (int) player.getY(), 5, 5, Color.YELLOW);
        //System.out.println(mouseEvent.getX()); System.out.println(mouseEvent.getY());
        if (Objects.equals(mouseEvent.getEventType(), MouseEvent.MOUSE_CLICKED)) {
            //double mouseY = mouseEvent.getY();
            //double mouseX = mouseEvent.getX();
            //double correctY = player.getY() - mouseY;
            //double correctX = player.getX() - mouseX;
            root.getChildren().add(bullet);
            //System.out.println(-mouseY);
            if (mouseEvent.getX() > bullet.getX()) {
                bullet.shoot(lineRight, root);
            } else if (mouseEvent.getX() <= bullet.getX()) {
                bullet.shoot(lineLeft, root);
            }
        }
    };


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
