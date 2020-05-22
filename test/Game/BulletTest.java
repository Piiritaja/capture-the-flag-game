package Game;

import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.MapLoad;
import Game.maps.Object;
import Game.player.Bullet;
import Game.player.GamePlayer;
import Game.player.Player;
import com.esotericsoftware.kryonet.Client;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BulletTest extends ApplicationTest {

    private Stage stage;
    private Player player;
    private Client client;
    private List<Object> objectsOnMap;
    private List<Player> deadPlayers;
    private List<Player> players;
    private MapLoad mapload;
    private BotSpawner botSpawner;
    private AnchorPane root;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Group sceneRoot = new Group();
        Scene scene = new Scene(sceneRoot, 100, 100);
        stage.setScene(scene);
        stage.show();
        client = new Client();
        player = new GamePlayer(10, 10, 0, 0, GamePlayer.playerColor.RED, client, stage);
        players = new ArrayList<>();
        deadPlayers = new ArrayList<>();
        objectsOnMap = new ArrayList<>();
        mapload = new MapLoad();
        botSpawner = new BotSpawner();
        root = new AnchorPane();
        players.add(player);
    }

    @Nested
    class BulletCollisionTests {
        @Test
        void bulletCollisionWithBotTest() {
            Bot bot = new Bot(10, 10, 0, stage, true);
            botSpawner.botsOnMap.add(bot);
            Bullet bullet = new Bullet(10, 10, 5, Color.ORANGE, true);
            player.bullets.add(bullet);
            Line lineRight = new Line(0, 0, 500, 0);
            bullet.shoot(lineRight, root, 500, player.bullets);
            bullet.bulletCollision(players, objectsOnMap, root, botSpawner, client, player, deadPlayers, mapload, 0);
            assertEquals(0, botSpawner.botsOnMap.size());
        }
        @Test
        void bulletCollisionWithPlayerTest() {
            Bullet bullet = new Bullet(25, 25, 5, Color.ORANGE, true);
            player.bullets.add(bullet);
            Line lineRight = new Line(0, 0, 500, 0);
            bullet.shoot(lineRight, root, 500, player.bullets);
            bullet.bulletCollision(players, objectsOnMap, root, botSpawner, client, player, deadPlayers, mapload, 0);
            assertEquals(1, players.size());
        }
    }
}
