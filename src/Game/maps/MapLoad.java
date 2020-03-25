package Game.maps;

import Game.Screen;
import Game.player.Flag;
import javafx.scene.Group;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class MapLoad {

    public List<Base> bases = new ArrayList<>();
    private List<Object> objectsOnMap = new ArrayList<>();
    private Flag redFlag;
    private Flag greenFlag;
    private static final int FLAG_WIDTH = 10;
    private static final int FLAG_HEIGHT = 10;
    private final int FLAG_TO_TILE_RATIO_WIDTH = 128;
    private final int FLAG_TO_TILE_RATIO_HEIGHT = 80;

    private String floorImagePath;
    private Battlefield mapToLoad;
    private final double MAP_HEIGHT_IN_TILES = Screen.getMAP_HEIGHT_IN_TILES();
    private final double MAP_WIDTH_IN_TILES = Screen.getMAP_WIDTH_IN_TILES();
    private int baseHeightInTiles;
    private double baseWidthInTiles;
    private MapLayer floor;

    public List<Object> getObjectsOnMap() {
        return objectsOnMap;
    }

    private void loadMap(Group root, Stage stage) {
        floor = new MapLayer(floorImagePath);
        // floor.png is 1280x800
        floor.addToGroup(root);
        if (!stage.isFullScreen()) {
            stage.setWidth(floor.getLayerWidth());
            stage.setHeight(floor.getLayerHeight());
        }

        // old walls image
        /*
        MapLayer walls = new MapLayer("assets/map/2teams/map2/walls.png");
        walls.addToPane(rootPane);
        */

        //base loader
        double stageWidth = stage.widthProperty().get();
        // heightratio : map consists of 25 tiles in height
        // baseheightintiles : base height is 23 tiles
        baseHeightInTiles = 23;
        final double heightWallEdges = stage.heightProperty().get() / MAP_HEIGHT_IN_TILES * 2;
        // walls are originally 32px tall and the map height is 25*32px rectangles.
        // it is needed to remove the wall heights (top of map and bottom of map) from the base height

        baseWidthInTiles = 8;
        final double baseWidth = stageWidth / baseWidthInTiles;
        final double baseHeight = stage.heightProperty().get() / MAP_HEIGHT_IN_TILES * baseHeightInTiles;
        /* Map2 base width is 1/8 of the whole map width.
         */

        // map consists of 40 32px rectangles (width).
        final double baseStartY = heightWallEdges / 2;

        final double redBaseStartX = stageWidth / MAP_WIDTH_IN_TILES;
        Base redBase = new Base(Base.baseColor.RED, baseWidth, baseHeight, redBaseStartX, baseStartY);
        //1088 1280-160-32
        final double greenBaseStartX = stageWidth - baseWidth;
        Base greenBase = new Base(Base.baseColor.GREEN, baseWidth, baseHeight, greenBaseStartX, baseStartY);


        root.getChildren().add(redBase);
        root.getChildren().add(greenBase);
        bases.add(redBase);
        bases.add(greenBase);

        // add objects to map
        objectsOnMap = Object.addObjectsToGroup(root, stage, mapToLoad);

        //both flags
        redFlag = new Flag(
                (int) (greenBase.getRightX() - 50),
                (int) (greenBase.getBottomY() / 2 - FLAG_HEIGHT),
                FLAG_WIDTH,
                FLAG_HEIGHT,
                Flag.flagColor.RED);

        greenFlag = new Flag(
                (int) redBase.getLeftX() + 50,
                (int) redBase.getBottomY() / 2,
                FLAG_WIDTH,
                FLAG_HEIGHT,
                Flag.flagColor.GREEN);
        root.getChildren().add(redFlag);
        root.getChildren().add(greenFlag);

    }


    public void loadMap2(Group root, Stage stage) {
        floorImagePath = "assets/map/2teams/map2/floor.png";
        // floor.png is 1280x800
        mapToLoad = Battlefield.MAP2;
        loadMap(root, stage);

    }

    public void updateScaleMap(Stage stage) {
        // initialize
        final double initialWidth = stage.widthProperty().get();
        final double initialHeight = stage.heightProperty().get();
        floor.setFitWidth(initialWidth);
        floor.setFitHeight(initialHeight);
        for (Base base : getBases()) {
            // width for base is 6x32 px for original size which is stage width / 8
            double initialBaseWidth = initialWidth / baseWidthInTiles;
            double initialXOffsetFromWalls = initialWidth / MAP_WIDTH_IN_TILES;
            base.setWidth(initialBaseWidth);
            if (base.getBaseColor() == Base.baseColor.GREEN) {
                base.setX(initialWidth
                        - initialBaseWidth
                        - initialWidth / MAP_WIDTH_IN_TILES);
            } else {
                base.setX(initialXOffsetFromWalls);
            }
            base.setHeight(initialHeight / MAP_HEIGHT_IN_TILES * baseHeightInTiles);
            base.setY(initialHeight / MAP_HEIGHT_IN_TILES);
        }

        // initalize flags

        redFlag.setWidth(initialWidth / FLAG_TO_TILE_RATIO_WIDTH);
        redFlag.setHeight(initialHeight / FLAG_TO_TILE_RATIO_HEIGHT);
        greenFlag.setWidth(initialWidth / FLAG_TO_TILE_RATIO_WIDTH);
        greenFlag.setHeight(initialHeight / FLAG_TO_TILE_RATIO_HEIGHT);

        // initialize objects
        for (Object object : objectsOnMap) {
            object.setFitWidth(initialWidth / MAP_WIDTH_IN_TILES);
            object.setX(initialWidth / MAP_WIDTH_IN_TILES * object.getColumn());
            object.setFitHeight(initialHeight / MAP_HEIGHT_IN_TILES);
            object.setY(initialHeight / MAP_HEIGHT_IN_TILES * object.getRow());
        }

        // listener for constant updates
        stage.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            //set floor width
            floor.setFitWidth((double) newWidth);

            redFlag.setWidth((double) newWidth / FLAG_TO_TILE_RATIO_WIDTH);
            greenFlag.setWidth((double) newWidth / FLAG_TO_TILE_RATIO_WIDTH);

            // set bases widths
            for (Base base : getBases()) {
                // width for base is 6x32 px for original size which is stage width / 8
                final double newBaseWidth = (double) newWidth / baseWidthInTiles;
                final double xOffsetFromWalls = (double) newWidth / MAP_WIDTH_IN_TILES;
                base.setWidth(newBaseWidth);
                if (base.getBaseColor() == Base.baseColor.GREEN) {
                    base.setX((double) newWidth
                            - newBaseWidth
                            - (double) newWidth / MAP_WIDTH_IN_TILES);
                } else {
                    base.setX(xOffsetFromWalls);
                }
            }

            // set objects widths
            for (Object object : objectsOnMap) {
                object.setFitWidth((double) newWidth / MAP_WIDTH_IN_TILES);
                object.setX((double) newWidth / MAP_WIDTH_IN_TILES * object.getColumn());
            }
        });

        stage.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            // set floor height
            floor.setFitHeight((double) newHeight);

            redFlag.setHeight((double) newHeight / FLAG_TO_TILE_RATIO_HEIGHT);
            greenFlag.setHeight((double) newHeight / FLAG_TO_TILE_RATIO_HEIGHT);


            // set base heights
            for (Base base : getBases()) {
                // 25 tiles makes the height of the screen; base height for this map is 23 tiles
                base.setHeight((double) newHeight / MAP_HEIGHT_IN_TILES * baseHeightInTiles);
                base.setY((double) newHeight / MAP_HEIGHT_IN_TILES);
            }

            // set objects heights
            for (Object object : objectsOnMap) {
                object.setFitHeight((double) newHeight / MAP_HEIGHT_IN_TILES);
                object.setY((double) newHeight / MAP_HEIGHT_IN_TILES * object.getRow());

            }
        });
    }


    public void loadMap1(Group root, Stage stage) {
        floorImagePath = "assets/map/2teams/map1/floor.png";
        mapToLoad = Battlefield.MAP1;
        loadMap(root, stage);
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

    public Flag getRedFlag() {
        return redFlag;
    }

    public Flag getGreenFlag() {
        return greenFlag;
    }
}
