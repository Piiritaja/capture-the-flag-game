import Game.bots.Bot;
import Game.maps.Base;
import Game.player.Bullet;
import Game.player.GamePlayer;
import Game.player.Player;
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

public class BotTest extends ApplicationTest {

    private Stage testStage;
    private final double screenSize = 1000;
    private Bot bot;
    private final int lives = 10;


    @Override
    public void start(Stage stage) {
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, screenSize, screenSize);
        stage.setScene(scene);
        stage.show();
        testStage = stage;

        final int botCoordinates = 100;
        bot = new Bot(botCoordinates, botCoordinates, lives, testStage, true);
    }

    @Test
    void testCreateBot() {
        assertNotNull(bot);
        assertEquals(bot.getBotLives(), bot.getBotLives());
    }

    @Test
    void testSetWidthHeight() {
        final double widthHeight = 45;
        bot.setBotHeight(widthHeight);
        bot.setBotWidth(widthHeight);

        assertEquals(widthHeight, bot.getBotHeight());
        assertEquals(widthHeight, bot.getBotWidth());
    }

    @Test
    void testBotId() {
        final int id = 2;
        bot.setBotId(id);
        assertEquals(id, bot.getBotId());
    }

    @Test
    void testBotCollisionPlayer() {
        final double x = 10;
        bot.setX(x);
        bot.setY(x);

        final int playerX = 900;
        final int dx = 0;
        Client client = null;
        GamePlayer player = new GamePlayer(playerX, playerX, dx, dx, GamePlayer.playerColor.RED, client, testStage);

        assertFalse(bot.collides(player));

        player.setX(x);
        player.setY(x);

        assertTrue(bot.collides(player));
    }

    @Test
    void testBotCollisionBullet() {
        final double x = 10;
        bot.setX(x);
        bot.setY(x);

        final int bulletX = 900;
        final int radius = 10;
        Bullet bullet = new Bullet(bulletX, bulletX, radius, Color.RED, true);

        assertFalse(bot.collides(bullet));

        bullet.setLayoutX(x);
        bullet.setLayoutY(x);
        bullet.setCenterY(x);
        bullet.setCenterX(x);
        
        assertTrue(bot.collides(bullet));
    }

}

