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

    private Random random = new Random();
    private Map<Integer, Integer> botLocations = new HashMap<>();
    public List<Bot> botsOnMap = new ArrayList<>();

    public BotSpawner() {
    }

    public void spawnBots(int amount, Stage stage, Group group, List<Base> bases, List<Object> objectsOnMap) {
        for (int i = 0; i < amount; i++) {
            double maxX = stage.widthProperty().get();
            double maxY = stage.heightProperty().get();
            double min = 0;
            while (true) {
                int locationX = random.nextInt((int) (((maxX - min) + 1) + min));
                int locationY = random.nextInt((int) (((maxY - min) + 1) + min));
                if (botLocations.containsKey(locationX)) {
                    if (botLocations.get(locationX) != locationY && notInBase(locationX, locationY, bases)) {
                        Bot bot = new Bot(locationX, locationY, 0, 0, 10);
                        if (notColliding(bot, objectsOnMap)) {
                            group.getChildren().add(bot);
                            botsOnMap.add(bot);
                            botLocations.put(locationX, locationY);
                            break;
                        }
                    }
                } else {
                    if (notInBase(locationX, locationY, bases)) {
                        Bot bot = new Bot(locationX, locationY, 0, 0, 10);
                        if (notColliding(bot, objectsOnMap)) {
                            botLocations.put(locationX, locationY);
                            botsOnMap.add(bot);
                            group.getChildren().add(bot);
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean notInBase(int locationX, int locationY, List<Base> bases) {
        for (Base base : bases) {
            if (locationX < base.getRightX() && locationX > base.getLeftX()) {
                return !(locationY > base.getTopY() && locationY < base.getBottomY());
            }
        }
        return true;
    }

    private boolean notColliding(Bot bot, List<Object> objects) {
        for (Object object : objects) {
            if (object.collides(bot)) {
                return false;
            }
        }
        return true;
    }

}
