package Game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class Screen extends Application {
    Player player = new Player(50, 50, 10, 10, 0, 0);
    int step = 3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage stage = new Stage();
        stage.setTitle("Capture the flag");
        Group root = new Group();


        root.getChildren().add(player);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                player.tick();
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

}
