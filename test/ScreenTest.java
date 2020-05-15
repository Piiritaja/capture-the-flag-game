import Game.Menu;
import Game.Screen;
import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.Battlefield;
import Game.maps.MapLoad;
import Game.player.GamePlayer;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import networking.ServerClient;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScreenTest extends ApplicationTest {

    private Screen screen;
    private ServerClient serverClient;
    private Menu menu;
    private MapLoad mapload;
    private AnchorPane root;
    private Stage stage;
    private BotSpawner botSpawner;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        menu = new Menu();
        serverClient = new ServerClient(menu);
        screen = new Screen(serverClient);

    }

    @Test
    void getGameId() {
        screen.setGameId("20e7");
        assertEquals("20e7", screen.getGameId());
    }

    @Test
    void getBotLocations() {
        Map<Integer, Double[]> botLocations = new HashMap<>();
        Double[] doubles = new Double[1];
        botLocations.put(2, doubles);
        screen.setBotLocations(botLocations);
        assertEquals(botLocations, screen.getBotLocations());
    }

    @Test
    void isInGame() {
        assertFalse(screen.isInGame());
    }

    @Test
    void getRoot() {
        AnchorPane root = new AnchorPane();
        assertEquals(root.getClass(), screen.getRoot().getClass());
    }

    @Test
    void setMap() {
        screen.setMap(0);
        assertEquals(Battlefield.MAP1, screen.getChosenMap());
        screen.setMap(1);
        assertEquals(Battlefield.MAP2, screen.getChosenMap());
    }

    @Test
    void createAi() {
        root = new AnchorPane();
        mapload = new MapLoad();
        mapload.loadMap1(root, stage);
        screen.greenBase = mapload.getBaseByColor(Base.baseColor.GREEN);
        screen.redBase = mapload.getBaseByColor(Base.baseColor.RED);
        screen.createAi(GamePlayer.playerColor.RED);
        screen.createAi(GamePlayer.playerColor.GREEN, 0, 0, "100");
        assertEquals(2, screen.getAiPlayers().size());
    }

    @Test
    void getConnectedPlayerCount() {
        assertEquals(0, screen.getConnectedPlayerCount());
    }
}
