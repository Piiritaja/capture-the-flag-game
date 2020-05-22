package Game;

import Game.bots.Bot;
import Game.maps.Base;
import Game.player.Bullet;
import Game.player.GamePlayer;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTest extends ApplicationTest {

    private Stage testStage;


    @Override
    public void start(Stage stage) {
        Group sceneRoot = new Group();
        double screenSize = 1000;
        Scene scene = new Scene(sceneRoot, screenSize, screenSize);
        stage.setScene(scene);
        stage.show();
        testStage = stage;
    }

    @Test
    void testCreateBase() {
        double stageWidth = testStage.widthProperty().get();
        double stageHeight = testStage.heightProperty().get();
        final double baseWidthRatio = 6;
        double baseWidth = stageWidth / baseWidthRatio;
        double baseHeight = stageHeight;
        final double baseOpacity = 0.4;

        final double greenBaseX = 0;
        final double redBaseX = stageWidth - baseWidth;
        final double baseY = 0;
        Base green = new Base(Base.baseColor.GREEN, baseWidth, baseHeight, greenBaseX, baseY);
        Base red = new Base(Base.baseColor.RED, baseWidth, baseHeight, redBaseX, baseY);

        assertEquals(Base.baseColor.GREEN, green.getBaseColor());
        assertEquals(Base.baseColor.RED, red.getBaseColor());

        assertEquals(baseOpacity, green.getOpacity());
        assertEquals(baseOpacity, red.getOpacity());

        assertEquals(greenBaseX, green.getLeftX());
        assertEquals(greenBaseX + baseWidth, green.getRightX());
        assertEquals(baseY, green.getTopY());
        assertEquals(baseY + baseHeight, green.getBottomY());

        assertEquals(redBaseX, red.getLeftX());
        assertEquals(redBaseX + baseWidth, red.getRightX());
        assertEquals(baseY, red.getTopY());
        assertEquals(baseY + baseHeight, red.getBottomY());

    }



}

