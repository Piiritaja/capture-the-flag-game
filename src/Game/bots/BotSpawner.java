package Game.bots;

import Game.maps.Base;
import Game.maps.Object;
import javafx.scene.Group;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BotSpawner {

    private Map<Integer, Integer> botLocations = new HashMap<>();
    public List<Bot> botsOnMap = new ArrayList<>();
    private final int mapWidthInTiles = 40;
    private final int mapHeightInTiles = 25;

    /**
     * Bot spawner to add bots to the map.
     */
    public BotSpawner() {
    }

    /**
     * Spawns bots according to given inputs.
     *
     * @param limit       limit of bots to be added
     * @param stage        JavaFx stage that the bots are added to
     * @param group        JavaFx group that the bots are added to
     * @param bases        Bases that the bot can't be spawned into
     * @param objectsOnMap List of objects that the bots can not spawn into
     */
    public void spawnBots(int limit, Stage stage, Group group, List<Base> bases, List<Object> objectsOnMap) {
        double maxX = stage.widthProperty().get() - stage.widthProperty().get() / mapWidthInTiles;
        double maxY = stage.heightProperty().get() - stage.heightProperty().get() / mapHeightInTiles;
        for (int i = 0; i < limit; i++) {
            while (true) {
                Integer[] location = generateBotLocation(maxX, maxY);
                int locationX = location[0];
                int locationY = location[1];
                if (botLocations.containsKey(locationX)) {
                    if (botLocations.get(locationX) != locationY && notInBase(locationX, locationY, bases)) {
                        Bot bot = new Bot(locationX, locationY, 10, stage);
                        if (notColliding(bot, objectsOnMap)) {
                            group.getChildren().add(bot);
                            botsOnMap.add(bot);
                            botLocations.put(locationX, locationY);
                            bot.setBotId(botsOnMap.size());
                            break;
                        }
                    }
                } else {
                    if (notInBase(locationX, locationY, bases)) {
                        Bot bot = new Bot(locationX, locationY, 10, stage);
                        if (notColliding(bot, objectsOnMap)) {
                            botLocations.put(locationX, locationY);
                            botsOnMap.add(bot);
                            group.getChildren().add(bot);
                            bot.setBotId(botsOnMap.size());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param locationX X coordinate of the bot
     * @param locationY Y coordinate of the bot
     * @param bases     Bases on the map
     * @return true if bot is inside a base, false if not
     */
    public boolean notInBase(int locationX, int locationY, List<Base> bases) {
        for (Base base : bases) {
            if (locationX < base.getRightX() && locationX > base.getLeftX()) {
                return !(locationY > base.getTopY() && locationY < base.getBottomY());
            }
        }
        return true;
    }

    /**
     * @param bot     Bot that the collision is checked with
     * @param objects Objects thate the collision are checked with
     * @return if the bot collides with an object
     */
    private boolean notColliding(Bot bot, List<Object> objects) {
        for (Object object : objects) {
            if (object.collides(bot)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates bot locations according to the amount of bots already added to map.
     * Map divided into four sections, bots are added to each section one after another.
     * Maximum and minimum coordinates within the section are offset, to avoid bots spawning too close to each other.
     * @param stageMaxX Maximum X coordinate for bot spawning
     * @param stageMaxY Maximum Y coordinate for bot spawning
     * @return Integer[] with the X (array[0]) and Y (array[1]) coordinates
     */
    private Integer[] generateBotLocation(double stageMaxX, double stageMaxY) {
        final int offsetCoefficient = 10;
        final double xOffset = stageMaxX / offsetCoefficient;
        final double yOffset = stageMaxY / offsetCoefficient;
        double minX = 0 + xOffset;
        double minY = 0 + yOffset;
        double maxX = stageMaxX - xOffset;
        double maxY = stageMaxY - yOffset;

        int coordinateX, coordinateY;
        Integer[] location = new Integer[2];
        if (botsOnMap.size() % 4 == 0 || botsOnMap.isEmpty()) {
            maxX = stageMaxX / 2 - xOffset;
            maxY = stageMaxY / 2 - yOffset;
        } else if (botsOnMap.size() % 3 == 0) {
            maxX = stageMaxX / 2 - xOffset;
            minY = stageMaxY / 2 + yOffset;
        } else if (botsOnMap.size() % 2 == 0) {
            minX = stageMaxX / 2 + xOffset;
            maxY = stageMaxY / 2 - yOffset;
        } else {
            minX = stageMaxX / 2 + xOffset;
            minY = stageMaxY / 2 + yOffset;
        }
        double rangeX = ((maxX - minX) + 1);
        double rangeY = ((maxY - minY) + 1);

        //nt range = (max - min) + 1;
        //   return (int)(Math.random() * range) + min;
        coordinateX = (int) ((Math.random() * rangeX) + minX);
        coordinateY = (int) ((Math.random() * rangeY) + minY);
        location[0] = coordinateX;
        location[1] = coordinateY;
        return location;
    }

    /**
     * @return Bots that were added by this spawner
     */
    public List<Bot> getBotsOnMap() {
        return botsOnMap;
    }

}
