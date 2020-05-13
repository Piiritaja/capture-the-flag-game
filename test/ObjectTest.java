import Game.bots.Bot;
import Game.maps.Object;
import Game.player.Bullet;
import Game.player.GamePlayer;
import Game.player.Player;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectTest extends ApplicationTest {

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
    void testCreateObjectWithTexture() {
        String texture = "/map/objects/textures/wood5.png";
        Object object = new Object(texture);
        final int rowCol = 1;
        final int widthHeight = 32;

        assertEquals(rowCol, object.getColumn());
        assertEquals(rowCol, object.getRow());
        assertEquals(widthHeight, object.getFitWidth());
        assertEquals(widthHeight, object.getFitHeight());
    }

    @Test
    void testCreateObjectWithoutTexture() {
        Object object = new Object();
        final int rowCol = 1;

        assertEquals(rowCol, object.getColumn());
        assertEquals(rowCol, object.getRow());

        String texture = "/map/objects/textures/wood5.png";
        object.setTileTexture(texture);
        Image textureImage = new Image(Object.class.getResourceAsStream(texture));

        assertEquals(object.getImage().getUrl(), textureImage.getUrl());
    }

    @Nested
    class ObjectCollision {
        @Test
        void testObjectCollisionWithPlayer() {
            String texture = "/map/objects/textures/wood5.png";
            Object object = new Object(texture);
            object.setX(0);
            object.setY(0);

            final int playerX = 900;
            final int dx = 0;
            Client client = null;
            GamePlayer player = new GamePlayer(playerX, playerX, dx, dx, GamePlayer.playerColor.RED, client, testStage);

            assertFalse(object.collides(player));

            player.setX(0);
            player.setY(0);

            assertTrue(object.collides(player));
        }

        @Test
        void testObjectCollisionWithAiCircle() {
            String texture = "/map/objects/textures/wood5.png";
            Object object = new Object(texture);
            object.setX(0);
            object.setY(0);

            Circle aiBoundaries = new Circle();
            final double circleStartCoordinate = 900;
            final double circleRadius = 15;
            aiBoundaries.setRadius(circleRadius);
            aiBoundaries.setCenterY(circleStartCoordinate);
            aiBoundaries.setCenterX(circleStartCoordinate);
            aiBoundaries.setLayoutX(circleStartCoordinate);
            aiBoundaries.setLayoutY(circleStartCoordinate);

            assertFalse(object.collides(aiBoundaries));

            aiBoundaries.setCenterY(0);
            aiBoundaries.setCenterX(0);
            aiBoundaries.setLayoutX(0);
            aiBoundaries.setLayoutY(0);

            assertTrue(object.collides(aiBoundaries));
        }

        @Test
        void testObjectCollisionWithBullet() {
            String texture = "/map/objects/textures/wood5.png";
            Object object = new Object(texture);
            final int x = 10;
            object.setX(x);
            object.setY(x);

            final int bulletX = 900;
            final int radius = 10;
            Bullet bullet = new Bullet(bulletX, bulletX, radius, Color.RED, true);

            assertFalse(object.collides(bullet));

            bullet.setLayoutX(x);
            bullet.setLayoutY(x);
            bullet.setCenterY(x);
            bullet.setCenterX(x);

            assertTrue(object.collides(bullet));
        }
    }
}

