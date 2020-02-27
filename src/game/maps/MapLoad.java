package game.maps;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MapLoad extends Application {

    final double ASPECT_RATIO = 1.6;

    @Override
    public void start(Stage stage) {

        /*StackPane root = new StackPane();
        root.setId("pane");

        scene = new Scene(root, width, height);
        scene.getStylesheets().add("assets/map/2teams/map1/map1.css");

        stage.setTitle("MapLoad");
        //Adding scene to the stage
        stage.setScene(scene);
        stage.setResizable(false);

        //Displaying the contents of the stage
        stage.show();*/
        // Scene map = loadMap1();
        Scene map = loadMap2();
        stage.setScene(map);
        stage.minWidthProperty().bind(map.heightProperty().multiply(ASPECT_RATIO));
        stage.minHeightProperty().bind(map.widthProperty().divide(ASPECT_RATIO));
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public static void main(String args[]) {
        launch(args);

    }

    public Scene loadMap2() {
        StackPane root = new StackPane();

        MapLayer floor = new MapLayer("assets/map/2teams/map2/floor.png");
        // floor.png is 1280x800
        floor.addToPane(root);

        MapLayer colors = new MapLayer("assets/map/2teams/map2/colors.png");
        colors.addToPane(root);

        MapLayer walls = new MapLayer("assets/map/2teams/map2/walls.png");
        walls.addToPane(root);
        return new Scene(root);
    }

    public Scene loadMap1() {
        StackPane root = new StackPane();
        root.setId("pane");

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add("assets/map/2teams/map1/map1.css");
        return scene;
    }
}