import Game.maps.Base;
import Game.maps.MapLoad;
import Game.player.Bullet;
import Game.player.Flag;
import Game.player.GamePlayer;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest extends ApplicationTest {

    GamePlayer player;
    GamePlayer player2;
    Client client;
    Flag flag;
    MapLoad mapload;
    Base greenBase;
    Base redBase;

    /*@BeforeEach
    void setUp() {

    }*/

    @Override public void start(Stage stage) {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, 100, 100);
        stage.setScene(scene);
        stage.show();
        player = new GamePlayer(10, 10, 0, 0, GamePlayer.playerColor.GREEN, client, stage);
        player2 = new GamePlayer(10, 10, 0, 0, GamePlayer.playerColor.RED, client, stage);
        flag = new Flag(0, 0, 10, 10, Flag.flagColor.GREEN);
        mapload = new MapLoad();
        greenBase = new Base(Base.baseColor.GREEN, 200, 200, 500, 0);
        redBase = new Base(Base.baseColor.RED, 200, 200, 0, 0);
    }

    @Test
    void testTest() {
        assertTrue(flag.getX() == 0);
    }

    @Test
    void isDead() {
        assertFalse(player.isDead());
    }

    @Test
    void setDead() {
        player.setDead(true);
        assertTrue(player.isDead());
    }

    @Test
    void setDx() {
        player.setDx(10);
        assertEquals(10, player.getDx());
    }

    @Test
    void setDy() {
        player.setDy(100);
        assertEquals(100, player.getDy());
    }

    @Test
    void getDx() {
        assertEquals(0, player.getDx());
    }

    @Test
    void getDy() {
        assertEquals(0, player.getDy());
    }

    @Test
    void getWidth() {
        assertEquals(60, player.getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(60, player.getHeight());
    }

    @Test
    void getColor() {
        assertEquals(GamePlayer.playerColor.GREEN, player.getColor());
    }

    @Test
    void setRoot() {
        AnchorPane root = new AnchorPane();
        player.setRoot(root);
        assertEquals(root, player.getRoot());
    }

    @Test
    void getLives() {
        assertEquals(10, player.getLives());
    }

    @Test
    void setPlayerLocationXInTiles() {
        player.setPlayerLocationXInTiles(10);
        assertEquals(10, player.getPlayerLocationXInTiles());
    }

    @Test
    void setPlayerLocationYInTiles() {
        player.setPlayerLocationYInTiles(10);
        assertEquals(10, player.getPlayerLocationYInTiles());
    }

    @Test
    void setLives() {
        player.setLives(100);
        assertEquals(100, player.getLives());
    }

    @Test
    void pickupFlag() {
        player.pickupFlag(flag);
        assertEquals(flag, player.getPickedUpFlag());
    }

    @Test
    void dropPickedUpFlag() {
        player.dropPickedUpFlag();
        assertNull(player.getPickedUpFlag());
    }

    @Test
    void getPickedUpFlag() {
        player.pickupFlag(flag);
        assertEquals(flag, player.getPickedUpFlag());
    }

    @Test
    void getColorTypeColor() {
        assertEquals(Color.GREEN, player.getColorTypeColor());
    }

    @Test
    void getGunCoordinates() {
        player.getGunCoordinates();
        player.setImage(player.walkingRightImage);
        assertEquals(player.getX() + player.getWidth(), player.shootingRightX);
    }

    @Test
    void moveUp() {
        player.moveUp();
        assertEquals(player.walkingUpImage, player.getImage());
    }

    @Test
    void moveDown() {
        player.moveDown();
        assertEquals(player.walkingDownImage, player.getImage());
    }

    @Test
    void moveRight() {
        player.moveRight();
        assertEquals(player.walkingRightImage, player.getImage());
    }

    @Test
    void moveLeft() {
        player.moveLeft();
        assertEquals(player.walkingLeftImage, player.getImage());
    }

    @Nested
    class CollisionTest {
        @Test
        void collidesBullet() {
            Bullet bullet = new Bullet(10, 10, 5, Color.YELLOW, true);
            assertFalse(player.collides(bullet));
        }

        @Test
        void collidesAnotherPlayer() {
            assertTrue(player.collides(player2));
        }
    }

    @Nested
    class setPlayerXStartingPositionTest {
        @Test
        void setGreenPlayerXStartingPosition() {
            System.out.println(greenBase.getLeftX());
            System.out.println(greenBase.getRightX());
            player.setPlayerXStartingPosition(greenBase, redBase);
            System.out.println(player.getX());
            System.out.println(player.getY());
            assertTrue(greenBase.getLeftX() <= player.x && player.x <= greenBase.getRightX());
        }

        @Test
        void setRedPlayerXStartingPosition() {
            player2.setPlayerXStartingPosition(greenBase, redBase);
            assertTrue(redBase.getLeftX() <= player2.x && player2.x <= redBase.getRightX());
        }
    }

    @Nested
    class setPlayerYStartingPositionTest {
        @Test
        void setGreenPlayerYStartingPosition() {
            player.setPlayerYStartingPosition(greenBase, redBase);
            assertTrue(greenBase.getBottomY() >= player.y && player.y >= greenBase.getTopY());
        }

        @Test
        void setRedPlayerYStartingPosition() {
            player2.setPlayerYStartingPosition(greenBase, redBase);
            assertTrue(redBase.getBottomY() >= player2.y && player2.y >= redBase.getTopY());
        }
    }

    /*@Test
    void shoot() {
        List<Bullet> bullets = new ArrayList<>(player.bullets);
        player.shoot(10, 10, true);
        assertEquals(bullets.size() + 1, player.bullets.size());
    }*/
}
