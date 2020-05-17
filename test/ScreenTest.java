import Game.Menu;
import Game.Screen;
import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.Battlefield;
import Game.maps.MapLoad;
import Game.player.GamePlayer;
import Game.player.Player;
import com.esotericsoftware.kryonet.Client;
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
    private Client client;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        menu = new Menu();
        client = new Client();
        serverClient = new ServerClient(menu);
        screen = new Screen(serverClient);

    }

    @Test
    void setStageTest() {
        screen.setStage(stage);
        assertEquals(stage, screen.getStage());
    }

    @Test
    void setPlayerCountTest() {
        screen.setPlayerCount(10);
        assertEquals(10, screen.getPlayerCount());
    }

    @Test
    void isMasterTest() {
        assertFalse(screen.isMaster());
    }

    @Test
    void getGameIdTest() {
        screen.setGameId("20e7");
        assertEquals("20e7", screen.getGameId());
    }

    @Test
    void getBotLocationsTest() {
        Map<Integer, Double[]> botLocations = new HashMap<>();
        Double[] doubles = new Double[1];
        botLocations.put(2, doubles);
        screen.setBotLocations(botLocations);
        assertEquals(botLocations, screen.getBotLocations());
    }

    @Test
    void isInGameTest() {
        assertFalse(screen.isInGame());
    }

    @Test
    void getRootTest() {
        AnchorPane root = new AnchorPane();
        assertEquals(root.getClass(), screen.getRoot().getClass());
    }

    @Test
    void setMapTest() {
        screen.setMap(0);
        assertEquals(Battlefield.MAP1, screen.getChosenMap());
        screen.setMap(1);
        assertEquals(Battlefield.MAP2, screen.getChosenMap());
    }

    @Test
    void createAiTest() {
        root = new AnchorPane();
        mapload = new MapLoad();
        mapload.loadMap1(root, stage);
        screen.setGreenBase(mapload.getBaseByColor(Base.baseColor.GREEN));
        screen.setRedBase(mapload.getBaseByColor(Base.baseColor.RED));
        screen.createAi(GamePlayer.playerColor.RED);
        screen.createAi(GamePlayer.playerColor.GREEN, 0, 0, "100");
        assertEquals(2, screen.getAiPlayers().size());
    }

    @Test
    void getConnectedPlayerCountTest() {
        assertEquals(0, screen.getConnectedPlayerCount());
    }

    @Test
    void createPlayerTest() {
        int playersSize = screen.getPlayers().size();
        root = new AnchorPane();
        mapload = new MapLoad();
        mapload.loadMap1(root, stage);
        screen.setGreenBase(mapload.getBaseByColor(Base.baseColor.GREEN));
        screen.setRedBase(mapload.getBaseByColor(Base.baseColor.RED));
        screen.setPlayerColor(1);
        screen.setPlayerColor(0);
        screen.createPlayer();
        assertEquals(playersSize + 1, screen.getPlayers().size());
    }

    @Test
    void createOtherPlayerTest() {
        int playersSize = screen.getPlayers().size();
        screen.setStage(stage);
        screen.createPlayer(0, 0, "ok", 'G', 10);
        assertEquals(playersSize + 1, screen.getPlayers().size());
        assertEquals(playersSize + 1, screen.getConnectedPlayerCount());
        assertTrue(screen.canTickPlayers());
    }

    @Test
    void scoreBoardTest() {
        screen.setStage(stage);
        screen.scoreBoard();
        assertNotNull(screen.getStack());
    }

    @Test
    void gameEndTest() {
        screen.tickPlayers();
        screen.setStage(stage);
        screen.theEnd();
        assertNotNull(screen.getWinnerText());
    }
}
