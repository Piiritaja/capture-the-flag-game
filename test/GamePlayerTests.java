import Game.maps.Base;
import Game.player.Bullet;
import Game.player.Flag;
import Game.player.GamePlayer;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerTests extends ApplicationTest {

    private GamePlayer player;
    private GamePlayer player2;
    private Flag flag;
    private Base greenBase;
    private Base redBase;
    private AnchorPane root;

    @Override
    public void start(Stage stage) {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, 100, 100);
        Client client = new Client();
        player = new GamePlayer(10, 10, 0, 0, GamePlayer.playerColor.GREEN, client, stage);
        player2 = new GamePlayer(10, 10, 0, 0, GamePlayer.playerColor.RED, client, stage);
        flag = new Flag(0, 0, 10, 10, Flag.flagColor.GREEN);
        greenBase = new Base(Base.baseColor.GREEN, 200, 200, 500, 0);
        redBase = new Base(Base.baseColor.RED, 200, 200, 0, 0);
        root = new AnchorPane();
    }

    @Test
    void isDeadTest() {
        assertFalse(player.isDead());
    }

    @Test
    void setDeadTest() {
        player.setDead(true);
        assertTrue(player.isDead());
    }

    @Test
    void setDxTest() {
        player.setDx(10);
        assertEquals(10, player.getDx());
    }

    @Test
    void setDyTest() {
        player.setDy(100);
        assertEquals(100, player.getDy());
    }

    @Test
    void getDxTest() {
        assertEquals(0, player.getDx());
    }

    @Test
    void getDyTest() {
        assertEquals(0, player.getDy());
    }

    @Test
    void getWidthTest() {
        assertEquals(60, player.getWidth());
    }

    @Test
    void getHeightTest() {
        assertEquals(60, player.getHeight());
    }

    @Test
    void getColorTest() {
        assertEquals(GamePlayer.playerColor.GREEN, player.getColor());
    }

    @Test
    void setRootTest() {
        AnchorPane root = new AnchorPane();
        player.setRoot(root);
        assertEquals(root, player.getRoot());
    }

    @Test
    void getLivesTest() {
        assertEquals(10, player.getLives());
    }

    @Test
    void setPlayerLocationXInTilesTest() {
        player.setPlayerLocationXInTiles(10);
        assertEquals(10, player.getPlayerLocationXInTiles());
    }

    @Test
    void setPlayerLocationYInTilesTest() {
        player.setPlayerLocationYInTiles(10);
        assertEquals(10, player.getPlayerLocationYInTiles());
    }

    @Test
    void setLivesTest() {
        player.setLives(100);
        assertEquals(100, player.getLives());
    }

    @Test
    void pickupFlagTest() {
        player.pickupFlag(flag);
        assertEquals(flag, player.getPickedUpFlag());
    }

    @Test
    void dropPickedUpFlagTest() {
        player.dropPickedUpFlag();
        assertNull(player.getPickedUpFlag());
    }

    @Test
    void getPickedUpFlagTest() {
        player.pickupFlag(flag);
        assertEquals(flag, player.getPickedUpFlag());
    }

    @Test
    void getColorTypeColorTest() {
        assertEquals(Color.GREEN, player.getColorTypeColor());
    }

    @Test
    void getGunCoordinatesTest() {
        player.getGunCoordinates();
        player.setImage(player.walkingRightImage);
        assertEquals(player.getX() + player.getWidth(), player.shootingRightX);
    }

    @Test
    void moveUpTest() {
        player.moveUp();
        assertEquals(player.walkingUpImage, player.getImage());
    }

    @Test
    void moveDownTest() {
        player.moveDown();
        assertEquals(player.walkingDownImage, player.getImage());
    }

    @Test
    void moveRightTest() {
        player.moveRight();
        assertEquals(player.walkingRightImage, player.getImage());
    }

    @Test
    void moveLeftTest() {
        player.moveLeft();
        assertEquals(player.walkingLeftImage, player.getImage());
    }

    @Nested
    class CollisionTest {
        @Test
        void collidesBulletTest() {
            Bullet bullet = new Bullet(25, 25, 3, Color.YELLOW, true);
            assertTrue(player.collides(bullet));
        }

        @Test
        void collidesAnotherPlayerTest() {
            assertTrue(player.collides(player2));
        }
    }

    @Nested
    class setPlayerXStartingPositionTest {
        @Test
        void setGreenPlayerXStartingPositionTest() {
            System.out.println(greenBase.getLeftX());
            System.out.println(greenBase.getRightX());
            player.setPlayerXStartingPosition(greenBase, redBase);
            System.out.println(player.getX());
            System.out.println(player.getY());
            assertTrue(greenBase.getLeftX() <= player.x && player.x <= greenBase.getRightX());
        }

        @Test
        void setRedPlayerXStartingPositionTest() {
            player2.setPlayerXStartingPosition(greenBase, redBase);
            assertTrue(redBase.getLeftX() <= player2.x && player2.x <= redBase.getRightX());
        }
    }

    @Nested
    class setPlayerYStartingPositionTest {
        @Test
        void setGreenPlayerYStartingPositionTest() {
            player.setPlayerYStartingPosition(greenBase, redBase);
            assertTrue(greenBase.getBottomY() >= player.y && player.y >= greenBase.getTopY());
        }

        @Test
        void setRedPlayerYStartingPositionTest() {
            player2.setPlayerYStartingPosition(greenBase, redBase);
            assertTrue(redBase.getBottomY() >= player2.y && player2.y >= redBase.getTopY());
        }
    }

    @Nested
    class ShootTests {
        @Test
        void shootUpTest() {
            List<Bullet> bullets = new ArrayList<>(player.bullets);
            player.setRoot(root);
            player.shoot(10, 10, true);
            assertEquals(bullets.size() + 1, player.bullets.size());
        }
        @Test
        void shootDownTest() {
            List<Bullet> bullets = new ArrayList<>(player.bullets);
            player.setRoot(root);
            player.shoot(10, 20, true);
            assertEquals(bullets.size() + 1, player.bullets.size());
        }
        @Test
        void shootLeftTest() {
            List<Bullet> bullets = new ArrayList<>(player.bullets);
            player.setRoot(root);
            player.shoot(5, 10, true);
            assertEquals(bullets.size() + 1, player.bullets.size());
        }
        @Test
        void shootRightTest() {
            List<Bullet> bullets = new ArrayList<>(player.bullets);
            player.setRoot(root);
            player.shoot(20, 10, true);
            assertEquals(bullets.size() + 1, player.bullets.size());
        }
    }
}
