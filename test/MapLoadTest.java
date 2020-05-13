import Game.bots.Bot;
import Game.maps.Base;
import Game.maps.MapLoad;
import Game.player.Bullet;
import Game.player.Flag;
import Game.player.GamePlayer;
import Game.player.Player;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapLoadTest extends ApplicationTest {

    private Stage testStage;


    @Override
    public void start(Stage stage) {
        Group sceneRoot = new Group();
        final double screenSize = 1000;
        Scene scene = new Scene(sceneRoot, screenSize, screenSize);
        stage.setScene(scene);
        stage.show();
        testStage = stage;
    }

    @Test
    void testEmptyMapLoad() {
        MapLoad mapLoad = new MapLoad();

        assertTrue(mapLoad.getObjectsOnMap().isEmpty());
        assertNull(mapLoad.getBaseByColor(Base.baseColor.RED));
        assertNull(mapLoad.getBaseByColor(Base.baseColor.GREEN));
        assertTrue(mapLoad.getBases().isEmpty());
        assertNull(mapLoad.getGreenFlag());
        assertNull(mapLoad.getRedFlag());
    }

    @Nested
    class LoadMap {
        @Test
        void testLoadMap1() {
            MapLoad mapLoad = new MapLoad();
            AnchorPane rootPane = new AnchorPane();
            mapLoad.loadMap1(rootPane, testStage);

            assertEquals(2, mapLoad.getBases().size());

            Base greenBase = mapLoad.getBaseByColor(Base.baseColor.GREEN);
            Base redBase = mapLoad.getBaseByColor(Base.baseColor.RED);
            assertTrue(mapLoad.getBases().contains(greenBase));
            assertTrue(mapLoad.getBases().contains(redBase));

            Flag greenFlag = mapLoad.getGreenFlag();
            Flag redFlag = mapLoad.getRedFlag();
            assertTrue(greenFlag.getBoundsInParent().intersects(redBase.getBoundsInParent()));
            assertTrue(redFlag.getBoundsInParent().intersects(greenBase.getBoundsInParent()));

            //map1 240 total objects defined in map1walls.csv
            final int map1CorrectWallAmount = 240;
            assertEquals(map1CorrectWallAmount, mapLoad.getObjectsOnMap().size());
        }

        @Test
        void testLoadMap2() {
            MapLoad mapLoad = new MapLoad();
            AnchorPane rootPane = new AnchorPane();
            mapLoad.loadMap2(rootPane, testStage);

            assertEquals(2, mapLoad.getBases().size());

            Base greenBase = mapLoad.getBaseByColor(Base.baseColor.GREEN);
            Base redBase = mapLoad.getBaseByColor(Base.baseColor.RED);
            assertTrue(mapLoad.getBases().contains(greenBase));
            assertTrue(mapLoad.getBases().contains(redBase));

            Flag greenFlag = mapLoad.getGreenFlag();
            Flag redFlag = mapLoad.getRedFlag();
            assertTrue(greenFlag.getBoundsInParent().intersects(redBase.getBoundsInParent()));
            assertTrue(redFlag.getBoundsInParent().intersects(greenBase.getBoundsInParent()));

            //map2 222 total objects defined in map2walls.csv
            final int map1CorrectWallAmount = 222;
            assertEquals(map1CorrectWallAmount, mapLoad.getObjectsOnMap().size());
        }
    }

}

