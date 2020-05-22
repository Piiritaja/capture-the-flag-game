package Game;

import Game.bots.Bot;
import Game.maps.Battlefield;
import Game.maps.Object;
import Game.player.Bullet;
import Game.player.GamePlayer;
import Game.player.Player;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectSpawnerTest extends ApplicationTest {

    private Stage testStage;
    private AnchorPane sceneRoot = new AnchorPane();

    @Override
    public void start(Stage stage) {
        double screenSize = 1000;
        Scene scene = new Scene(sceneRoot, screenSize, screenSize);
        stage.setScene(scene);
        stage.show();
        testStage = stage;
    }

    @Test
    void testSpawnObjects() {
        //map2 222 total objects defined in map2walls.csv
        //map1 240 total objects defined in map1walls.csv
        final int map1CorrectWallAmount = 240;
        final int map2CorrectWallAmount = 222;
        AnchorPane root = new AnchorPane();

        List<Object> map1Objects = Object.addObjectsToGroup(root, testStage, Battlefield.MAP1);

        assertEquals(map1CorrectWallAmount, map1Objects.size());

        List<Object> map2Objects = Object.addObjectsToGroup(root, testStage, Battlefield.MAP2);
        assertEquals(map2CorrectWallAmount, map2Objects.size());
    }

}

