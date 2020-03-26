package Game.maps;

import Game.player.Bullet;
import Game.player.Player;
import Game.bots.Bot;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
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
        playerBoundaries.setX(player.getX() + player.width / 4.0);
        playerBoundaries.setY(player.getY() + player.height / 4.0);
        playerBoundaries.setHeight(player.getHeight() - player.height / 2.0);
        playerBoundaries.setWidth(player.getWidth() - player.width / 2.0);
        return objectBoundaries.getBoundsInLocal().intersects(playerBoundaries.getBoundsInLocal());
    }

    public boolean collides(Bot bot) {
        Rectangle objectBoundaries = boundaries();
        Rectangle botBoundaries = new Rectangle();
        botBoundaries.setX(bot.getX());
        botBoundaries.setY(bot.getY());
        botBoundaries.setHeight(bot.fitHeightProperty().get() * 1.5);
        botBoundaries.setWidth(bot.fitWidthProperty().get() * 1.5);
        return objectBoundaries.getBoundsInLocal().intersects(botBoundaries.getBoundsInLocal());
    }

    public boolean collides(Bullet bullet) {
        Rectangle objectBoundaries = boundaries();
        Rectangle bulletBoundaries = new Rectangle();
        bulletBoundaries.setX(bullet.getCenterX() - bullet.getRadius());
        bulletBoundaries.setY(bullet.getCenterY() - bullet.getRadius());
        bulletBoundaries.setHeight(bullet.getRadius() * 2);
        bulletBoundaries.setWidth(bullet.getRadius() * 2);
        return ((Path) Shape.intersect(bullet, objectBoundaries)).getElements().size() > 1;
    }

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
                    } else if (character.equals("34")) {
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
