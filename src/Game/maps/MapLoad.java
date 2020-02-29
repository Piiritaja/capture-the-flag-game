package Game.maps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MapLoad extends Application {

    @Override
    public void start(Stage stage) {

    }

    public static void main(String[] args) {
        launch(args);

    }

    public StackPane loadMap2() {
        StackPane root = new StackPane();

        MapLayer floor = new MapLayer("assets/map/2teams/map2/floor.png");
        // floor.png is 1280x800
        floor.addToPane(root);

        MapLayer colors = new MapLayer("assets/map/2teams/map2/colors.png");
        colors.addToPane(root);

        MapLayer walls = new MapLayer("assets/map/2teams/map2/walls.png");
        walls.addToPane(root);
        return root;
    }

    public StackPane loadMap1() {
        StackPane root = new StackPane();
        root.setId("pane");
        MapLayer map = new MapLayer("assets/map/2teams/map1/testmap1.png");
        map.addToPane(root);
        return root;
    }
}