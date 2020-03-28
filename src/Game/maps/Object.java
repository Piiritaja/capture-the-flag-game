package Game.maps;

import Game.player.Bullet;
import Game.player.Player;
import Game.bots.Bot;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Objects that are displayed on the map.
 * Has collision detector.
 * Map consists of 25 rows and 40 columns of object slots.
 */
public class Object extends ImageView {

    private int width = 32;
    private int height = 32;
    private int row = 1;
    private int column = 1;
    public static final String WOOD_TEXTURE = "assets/map/objects/wooden.png";
    public static final String BRICK_TEXTURE = "assets/map/objects/brick2.png";

    public Object(String texture) {
        this.setFitWidth(width);
        this.setFitHeight(height);
        this.setImage(new Image(texture));
    }

    /**
     * Set objects row on the map (25 rows x 40 columns).
     *
     * @param row: Row to set the object to.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Set objects column on the map (25 rows x 40 columns).
     *
     * @param column: Row to set the object to.
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * @return The row that the object is on.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column that the object is on.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Creates invisible boundaries for the object to be used in collision detection.
     *
     * @return Rectangle: The rectangle is used for collision detection.
     */
    private Rectangle boundaries() {
        Rectangle objectBoundaries = new Rectangle();
        objectBoundaries.setX(this.getX());
        objectBoundaries.setY(this.getY());
        objectBoundaries.setWidth(this.getFitWidth());
        objectBoundaries.setHeight(this.getFitHeight());
        return objectBoundaries;
    }

    /**
     * Checks if this object collides with a player.
     *
     * @param player Player object to be checked collision with.
     * @return true of false - according to if the two objects collide or not.
     */
    public boolean collides(Player player) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(player.getX() + player.width / 4.0);
        playerBoundaries.setY(player.getY() + player.height / 4.0);
        playerBoundaries.setHeight(player.getHeight() - player.height / 2.0);
        playerBoundaries.setWidth(player.getWidth() - player.width / 2.0);
        return objectBoundaries.getBoundsInLocal().intersects(playerBoundaries.getBoundsInLocal());
    }

    /**
     * Checks if this object collides with a bot.
     *
     * @param bot Bot object to be checked collision with.
     * @return true of false - according to if the two objects collide or not.
     */
    public boolean collides(Bot bot) {
        Rectangle objectBoundaries = boundaries();
        Rectangle botBoundaries = new Rectangle();
        botBoundaries.setX(bot.getX());
        botBoundaries.setY(bot.getY());
        botBoundaries.setHeight(bot.getBotHeight() * 1.5);
        botBoundaries.setWidth(bot.getBotWidth() * 1.5);
        return objectBoundaries.getBoundsInLocal().intersects(botBoundaries.getBoundsInLocal());
    }

    /**
     * Checks if this object collides with a bullet.
     *
     * @param bullet Bot object to be checked collision with.
     * @return true of false - according to if the two objects collide or not.
     */
    public boolean collides(Bullet bullet) {
        Rectangle objectBoundaries = boundaries();
        Rectangle bulletBoundaries = new Rectangle();
        bulletBoundaries.setX(bullet.getCenterX() - bullet.getRadius());
        bulletBoundaries.setY(bullet.getCenterY() - bullet.getRadius());
        bulletBoundaries.setHeight(bullet.getRadius() * 2);
        bulletBoundaries.setWidth(bullet.getRadius() * 2);
        return ((Path) Shape.intersect(bullet, objectBoundaries)).getElements().size() > 1;
    }

    /**
     * Adds objects to the map.
     * Object locations are described in .csv files.
     *
     * @param root  Game Window stage root that the objects are in.
     * @param stage Game Window stage
     * @param map   Objects are loaded according to the chosen map
     * @return List of objects added to the map
     */
    public static List<Object> addObjectsToGroup(Group root, Stage stage, Battlefield map) {
        String line;
        String objectCsv = setCsv(map);
        int row = 0;
        int column;
        String[] field;
        List<Object> walls = new ArrayList<>();
        final int mapWidthInTiles = 40;
        final int mapHeightInTiles = 25;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(objectCsv));
            while ((line = reader.readLine()) != null) {
                column = 0;
                field = line.split(",");
                for (String character : field) {
                    if (character.equals("0") || character.equals("2")) {
                        Object tile = new Object(Object.BRICK_TEXTURE);
                        tile.setRow(row);
                        tile.setColumn(column);
                        //tile.setX(stage.widthProperty().get() / mapWidthInTiles * column);
                        tile.setFitWidth(stage.widthProperty().get() / mapWidthInTiles);
                        tile.setFitHeight(stage.heightProperty().get() / mapHeightInTiles);
                        tile.setX(column * mapWidthInTiles);
                        tile.setY(row * mapHeightInTiles);
                        root.getChildren().add(tile);
                        walls.add(tile);
                    } else if (character.equals("34") || character.equals("1")) {
                        Object tile = new Object(Object.WOOD_TEXTURE);
                        tile.setRow(row);
                        tile.setColumn(column);
                        //tile.setX(stage.widthProperty().get() / mapWidthInTiles * column);
                        tile.setX(column * mapWidthInTiles);
                        tile.setY(row * mapHeightInTiles);
                        tile.setFitWidth(stage.widthProperty().get() / mapWidthInTiles);
                        tile.setFitHeight(stage.heightProperty().get() / mapHeightInTiles);
                        root.getChildren().add(tile);
                        walls.add(tile);
                    }
                    column++;
                }
                row++;
            }
        } catch (IOException e) {
            System.out.println("Error: add objects to group");
        }
        return walls;
    }


    /**
     * Method used to set the correct .csv file for object loading.
     *
     * @param map Map which the objects are loaded onto
     * @return Path to the correct .csv file
     */
    private static String setCsv(Battlefield map) {
        switch (map) {
            case MAP1:
                return "src/assets/map/objects/map1walls.csv";
            case MAP2:
                return "src/assets/map/objects/map2walls.csv";
        }
        return null;
    }
}
