package Game.bots;

import Game.maps.Base;
import javafx.scene.Group;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BotSpawner {

    private Random random = new Random();
    private Map<Integer, Integer> botLocations = new HashMap<>();

    public BotSpawner() {
    }

    public void spawnBots(int amount, Stage stage, Group group, List<Base> bases) {
        for (int i = 0; i < amount; i++) {
            System.out.println(botLocations);
            double maxX = stage.widthProperty().get();
            double maxY = stage.heightProperty().get();
            double min = 0;
            while (true) {
                int locationX = random.nextInt((int) (((maxX - min) + 1) + min));
                int locationY = random.nextInt((int) (((maxY - min) + 1) + min));
                if (botLocations.containsKey(locationX)) {
                    if (botLocations.get(locationX) != locationY && notInBase(locationX, bases)) {
                        botLocations.put(locationX, locationY);
                        group.getChildren().add(new Bot(locationX, locationY, locationX, locationY));
                        break;
                    }
                } else {
                    if (notInBase(locationX, bases)) {
                        botLocations.put(locationX, locationY);
                        group.getChildren().add(new Bot(locationX, locationY, 0, 0));
                        break;
                    }
                }
            }
        }
    }

    public boolean notInBase(int locationX, List<Base> bases) {
        for (Base base : bases) {
            if (!(locationX > base.getRightX() | locationX < base.getLeftX())) {
                return false;
            }
        }
        return true;
    }

}
