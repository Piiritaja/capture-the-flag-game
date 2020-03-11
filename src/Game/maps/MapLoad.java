package Game.maps;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MapLoad extends Application {

    public List<Base> bases = new ArrayList<>();
    private List<Object> objectsOnMap = new ArrayList<>();

    public List<Object> getObjectsOnMap() {
        return objectsOnMap;
    }


    @Override
    public void start(Stage stage) {

    }

    public static void main(String[] args) {
        launch(args);

    }

    public void loadMap2(Group root, Stage stage) {
        MapLayer floor = new MapLayer("assets/map/2teams/map2/floor.png");
        // floor.png is 1280x800
        floor.addToGroup(root);

        // old walls image
        /*
        MapLayer walls = new MapLayer("assets/map/2teams/map2/walls.png");
        walls.addToPane(rootPane);
        */

        //base loader
        double stageWidth = stage.widthProperty().get();
        // heightratio : map consists of 25 tiles in height
        // baseheightintiles : base height is 23 tiles
        final double heightRatio = 25;
        final int baseHeightInTiles = 23;
        final double heightWallEdges = stage.heightProperty().get() / heightRatio * 2;
        // walls are originally 32px tall and the map height is 25*32px rectangles.
        // it is needed to remove the wall heights (top of map and bottom of map) from the base height

        final double baseWidthRatio = 8;
        final double baseWidth = stageWidth / baseWidthRatio;
        final double baseHeight = stage.heightProperty().get() / heightRatio * baseHeightInTiles;
        /* Map2 base width is 1/8 of the whole map width.
         */

        // map consists of 40 32px rectangles (width).
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

        // add objects to map
        objectsOnMap = Object.addObjectsToGroup(root, stage);
        /*System.out.println(objectsOnMap
        .stream()
        .map(Object::getColumn)
        .collect(Collectors.toList()));*/

        stage.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            //set floor width
            floor.setFitWidth((double) newWidth);

            // set bases widths
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

            // set objects widths
            for (Object object : objectsOnMap) {
                object.setFitWidth((double) newWidth / widthRatio);
                object.setX((double) newWidth / widthRatio * object.getColumn());
            }
        });

        stage.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            // set floor height
            floor.setFitHeight((double) newHeight);

            // set base heights
            for (Base base : getBases()) {
                // 25 tiles makes the height of the screen; base height for this map is 23 tiles
                base.setHeight((double) newHeight / heightRatio * baseHeightInTiles);
                base.setY((double) newHeight / heightRatio);
            }

            // set objects heights
            for (Object object : objectsOnMap) {
                object.setFitHeight((double) newHeight / heightRatio);
                object.setY((double) newHeight / heightRatio * object.getRow());

            }
        });
    }


    public void loadMap1(Group root, Stage stage) {
        MapLayer map = new MapLayer("assets/map/2teams/map1/testmap1.png");
        map.addToGroup(root);
    }

    public Base getBaseByColor(Base.baseColor color) {
        for (Base base : bases) {
            if (base.getBaseColor() == color) {
                return base;
            }
        }
        return null;
    }

    public List<Base> getBases() {
        return this.bases;
    }
}
