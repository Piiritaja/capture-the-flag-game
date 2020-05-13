import Game.Screen;
import Game.bots.Bot;
import Game.maps.Base;
import Game.maps.Battlefield;
import Game.maps.Object;
import Game.player.AiPlayer;
import Game.player.Bullet;
import Game.player.Flag;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AiPlayerTest extends ApplicationTest {

    private Stage testStage;
    private AiPlayer ai;
    private final int xy = 10;
    private Flag redFlag = new Flag(xy, xy, xy, xy, Flag.flagColor.RED);
    private final int step = 2;
    private List<Object> objectList = new ArrayList<>();
    private List<Bot> botList = new ArrayList<>();
    private List<Player> playerList = new ArrayList<>();


    @Override
    public void start(Stage stage) {
        final double screenSize = 1000;
        AnchorPane sceneRoot = new AnchorPane();
        Scene scene = new Scene(sceneRoot, screenSize, screenSize);
        stage.setScene(scene);
        stage.show();
        testStage = stage;
        ai = createAi(sceneRoot);
    }

    public AiPlayer createAi(AnchorPane root) {
        final int dx = 0;
        GamePlayer.playerColor color = GamePlayer.playerColor.RED;
        Base redBase = new Base(Base.baseColor.RED, testStage.widthProperty().get() / 5,
                testStage.heightProperty().get(), 0, 0);
        Client client = new Client();
        boolean master = true;
        return new AiPlayer(xy, xy, dx, dx, color, redFlag, root, redBase, client, master, testStage);
    }

    @Test
    void testParameters() {
        assertEquals(xy, ai.getX());
        assertEquals(xy, ai.getY());
        assertNull(ai.getPickedUpFlag());

    }

    @Test
    void testFlag() {
        ai.pickupFlag(redFlag);
        assertEquals(redFlag, ai.getPickedUpFlag());
        ai.dropPickedUpFlag();
        assertNull(ai.getPickedUpFlag());
    }

    @Test
    void testSetStartingPos() {
        AnchorPane root = new AnchorPane();
        final double baseWidth = testStage.widthProperty().get() / 5;
        Base redBase = new Base(Base.baseColor.RED, baseWidth,
                testStage.heightProperty().get(), 0, 0);
        Base greenBase = new Base(Base.baseColor.GREEN, baseWidth,
                testStage.heightProperty().get(), testStage.widthProperty().get() - baseWidth, 0);
        AiPlayer aiPlayer = createAi(root);
        root.getChildren().addAll(Arrays.asList(redBase, greenBase));
        aiPlayer.setPlayerXStartingPosition(greenBase, redBase);
        aiPlayer.setPlayerYStartingPosition(greenBase, redBase);
        assertTrue(aiPlayer.getBoundsInParent().intersects(redBase.getBoundsInParent()));
    }

    @Nested
    class TickTest {
        @Test
        void testTick() {
            ai.tick(objectList, botList, testStage, playerList, playerList);

            final int mapWidthInTiles = Screen.getMAP_WIDTH_IN_TILES();
            final int mapHeightInTiles = Screen.getMAP_HEIGHT_IN_TILES();

            final double coefficient = 1.5;
            final double correctWidth = testStage.widthProperty().get() / mapWidthInTiles * coefficient;
            assertEquals(correctWidth, ai.getFitWidth());
            final double correctHeight = testStage.heightProperty().get() / mapHeightInTiles * coefficient;
            assertEquals(correctHeight, ai.getFitHeight());
        }

        @Test
        void testMoveToFlag() {
            AnchorPane anchorPane = new AnchorPane();
            List<Object> objectList1 = Collections.singletonList(new Object());
            AiPlayer aiPlayer = createAi(anchorPane);
            redFlag.setX(testStage.widthProperty().get());
            redFlag.setY(xy);
            aiPlayer.dropPickedUpFlag();
            aiPlayer.setX(xy);
            aiPlayer.setY(xy);
            final double startingX = aiPlayer.getX();
            final double destinationX = startingX + step;
            ai.tick(objectList1, botList, testStage, playerList, playerList);

            assertEquals(xy, aiPlayer.getX());
            assertEquals(xy, aiPlayer.getY());


            //every time tick is called the shooting timer goes up by 5.
            //if the timer is above 100 Ai can move.
            // every time the AI shoots the timer is reset to 0.
            final int tickMovementTimerRange = 14;
            for (int i = 0; i < tickMovementTimerRange; i++) {
                ai.tick(objectList1, botList, testStage, playerList, playerList);
            }
            assertEquals(destinationX, ai.getX());
            assertEquals(xy, ai.getY());
        }

        @Test
        void testShootBot() {
            AnchorPane rootPane = new AnchorPane();
            AiPlayer aiPlayer = createAi(rootPane);
            final double aiInitialX = aiPlayer.getX();
            final double aiInitialY = aiPlayer.getY();
            final int offset = 5;
            final int botCoordinatesX = (int) aiInitialX + offset;
            final int botCoordinatesY = (int) aiInitialY + offset;
            final int lives = 10;

            Bot bot = new Bot(botCoordinatesX, botCoordinatesY, lives, testStage, true);
            List<Bot> bots = Collections.singletonList(bot);
            rootPane.getChildren().add(bot);

            final int tickMovementTimerRange = 10;
            for (int i = 0; i < tickMovementTimerRange; i++) {
                aiPlayer.tick(objectList, bots, testStage, playerList, playerList);
            }
            assertEquals(aiInitialX, aiPlayer.getX());
            assertEquals(aiInitialY, aiPlayer.getY());

        }
    }



}

