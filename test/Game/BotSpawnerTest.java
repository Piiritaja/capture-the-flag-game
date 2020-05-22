package Game;

import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.Battlefield;
import Game.maps.Object;
import Game.maps.MapLoad;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BotSpawnerTest extends ApplicationTest {

    private Stage testStage;
    private final double screenSize = 1000;
    private BotSpawner botSpawner;
    private AnchorPane sceneRoot = new AnchorPane();


    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(sceneRoot, screenSize, screenSize);
        stage.setScene(scene);
        stage.show();
        testStage = stage;
        botSpawner = new BotSpawner();
    }

    @Test
    void testCreateBotSpawner() {
        assertTrue(botSpawner.getBotsOnMap().isEmpty());
    }

    @Test
    void testSpawnBots() {
        AnchorPane rootGroup = new AnchorPane();
        final int botLimit = 4;
        List<Base> bases = createBases(rootGroup);
        List<Object> objectsOnMap = createObjects(rootGroup);

        botSpawner.spawnBots(botLimit, testStage, rootGroup, bases, objectsOnMap, true);

        List<Bot> botsOnMap = botSpawner.getBotsOnMap();
        assertEquals(botLimit, botsOnMap.size());

        for (Bot bot :botsOnMap) {
            Bounds botBounds = bot.getBoundsInParent();
            for (Base base : bases) {
                assertFalse(base.getBoundsInParent().intersects(botBounds));
            }
            for (Object object : objectsOnMap) {
                assertFalse(object.collides(bot));
            }
        }
    }

    private List<Base> createBases(AnchorPane root) {
        double stageHeight = testStage.heightProperty().get();
        double stageWidth = testStage.widthProperty().get();
        final int baseWidthRatio = 5;
        double baseWidth = stageWidth / baseWidthRatio;

        Base green = new Base(Base.baseColor.GREEN, baseWidth, stageHeight, 0, 0);
        Base red = new Base(Base.baseColor.RED, baseWidth, stageHeight, stageWidth - baseWidth, 0);

        root.getChildren().add(green);
        root.getChildren().add(red);
        return new ArrayList<>(Arrays.asList(green, red));
    }

    private List<Object> createObjects(AnchorPane root) {
        return Object.addObjectsToGroup(root, testStage, Battlefield.MAP1);
    }

    

}

