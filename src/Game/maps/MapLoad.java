package Game.maps;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class MapLoad extends Application {

    public List<Base> bases = new ArrayList<>();

    @Override
    public void start(Stage stage) {

    }

    public static void main(String[] args) {
        launch(args);

    }

    public void loadMap2(Group root, Stage stage) {
        StackPane rootPane = new StackPane();

        final double ASPECT_RATIO = 1.6;

        MapLayer floor = new MapLayer("assets/map/2teams/map2/floor.png");
        // floor.png is 1280x800
        floor.addToPane(rootPane);
        root.getChildren().add(rootPane);

        MapLayer walls = new MapLayer("assets/map/2teams/map2/walls.png");
        walls.addToPane(rootPane);

        // bind image size to stage size
        rootPane.prefWidthProperty().bind(stage.getScene().widthProperty());
        rootPane.prefHeightProperty().bind(stage.getScene().heightProperty());

        stage.minWidthProperty().bind(rootPane.heightProperty().multiply(ASPECT_RATIO));
        stage.minHeightProperty().bind(rootPane.widthProperty().divide(ASPECT_RATIO));

        //base loader

        double stageWidth = stage.widthProperty().get();
        final double heightRatio = 25;
        final double heightWallEdges = stage.heightProperty().get() / heightRatio * 2;
        // walls are originally 32px tall and the map height is 25*32px rectangles.
        // it is needed to remove the wall heights (top of map and bottom of map) from the base height

        final double baseWidthRatio = 8;
        final double baseWidth = stageWidth / baseWidthRatio;
        final double baseHeight = stage.heightProperty().get() - heightWallEdges;
        /* Map2 base width is 1/8 of the whole map width.
            base height is equal to the map height
         */

        // map consists of 40 32px rectangles.
        final double widthRatio = 40;
        final double baseStartY = heightWallEdges / 2;

        final double redBaseStartX = stageWidth / widthRatio;
        Base red = new Base(Base.baseColor.RED, baseWidth, baseHeight, redBaseStartX, baseStartY);
        //1088 1280-160-32
        final double greenBaseStartX = stageWidth - baseWidth;
        Base green = new Base(Base.baseColor.GREEN, baseWidth, baseHeight, greenBaseStartX, baseStartY);


        root.getChildren().add(red);
        root.getChildren().add(green);
        bases.add(red);
        bases.add(green);

        stage.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            for (Base base : getBases()) {
                // width for base is 6x32 px for original size which is stage width / 8
                final double newBaseWidth = (double) newWidth / baseWidthRatio;
                final double xOffsetFromWalls = (double) newWidth / widthRatio;
                base.setWidth(newBaseWidth);
                if (base.getBaseColor() == Base.baseColor.GREEN) {
                    base.setX((double) newWidth
                            - newBaseWidth
                            - (double) newWidth / widthRatio);
                } else {
                    base.setX(xOffsetFromWalls);
                }
            }
        });

        stage.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            for (Base base : getBases()) {
                base.setHeight((double) newHeight);
                base.setY((double) newHeight / heightRatio);
            }
        });


    }


    public StackPane loadMap1() {
        StackPane root = new StackPane();
        root.setId("pane");
        MapLayer map = new MapLayer("assets/map/2teams/map1/testmap1.png");
        map.addToPane(root);
        return root;
    }

    public List<Base> getBases() {
        return this.bases;
    }
}
