package Game.maps;

import Game.Player;
import Game.bots.Bot;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }


    private Rectangle boundaries() {
        Rectangle objectBoundaries = new Rectangle();
        objectBoundaries.setX(this.getX());
        objectBoundaries.setY(this.getY());
        objectBoundaries.setWidth(this.getFitWidth());
        objectBoundaries.setHeight(this.getFitHeight());
        return objectBoundaries;
    }

    public boolean collides(Player player) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(player.getX());
        playerBoundaries.setY(player.getY());
        playerBoundaries.setHeight(player.getHeight());
        playerBoundaries.setWidth(player.getWidth());
        return objectBoundaries.getBoundsInLocal().intersects(playerBoundaries.getBoundsInLocal());
    }

    public boolean collides(Bot bot) {
        Rectangle objectBoundaries = boundaries();
        Rectangle playerBoundaries = new Rectangle();
        playerBoundaries.setX(bot.getX());
        playerBoundaries.setY(bot.getY());
        playerBoundaries.setHeight(bot.fitHeightProperty().get());
        playerBoundaries.setWidth(bot.fitWidthProperty().get());
        return objectBoundaries.getBoundsInLocal().intersects(playerBoundaries.getBoundsInLocal());
    }

    public static List<Object> addObjectsToGroup(Group root, Stage stage) {
        String line;
        int row = 0;
        int column;
        String[] field;
        List<Object> walls = new ArrayList<>();
        final int mapWidthInTiles = 40;
        final int mapHeightInTiles = 25;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/assets/map/objects/map2walls.csv"));
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                column = 0;
                field = line.split(",");
                for (String character : field) {
                    if (character.equals("0")) {
                        Object tile = new Object(Object.BRICK_TEXTURE);
                        tile.setRow(row);
                        tile.setColumn(column);
                        tile.setX(stage.widthProperty().get() / mapWidthInTiles * column);
                        tile.setY(row * mapHeightInTiles);
                        root.getChildren().add(tile);
                        walls.add(tile);
                    } else if (character.equals("34")) {
                        Object tile = new Object(Object.WOOD_TEXTURE);
                        tile.setRow(row);
                        tile.setColumn(column);
                        tile.setX(stage.widthProperty().get() / mapWidthInTiles * column);
                        tile.setY(row * mapHeightInTiles);
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
}
