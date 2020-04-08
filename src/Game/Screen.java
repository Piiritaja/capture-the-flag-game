package Game;

import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.Battlefield;
import Game.maps.MapLoad;
import Game.maps.Object;
import Game.player.Bullet;
import Game.player.Flag;
import Game.player.Player;
import com.esotericsoftware.kryonet.Client;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.util.Duration;
import networking.ServerClient;
import networking.packets.Packet004RequestPlayers;
import networking.packets.Packet005SendPlayerPosition;
import networking.packets.Packet008SendPlayerID;
import networking.packets.Packet012UpdatePlayerPosition;

import java.util.List;
import java.util.Map;


public class Screen extends Application {

    Bullet bullet = new Bullet(0, 0, 3, Color.GREEN);

    private Player player;
    private Group root;
    private MapLoad mapLoad;
    private Base greenBase;
    private Base redBase;
    private AnimationTimer timer;
    private Stage stage;
    private List<Object> objectsOnMap;
    private List<Bot> botsOnMap;
    private Map<Integer, Double[]> botLocations = new HashMap<>();
    private Map<Integer, Double[]> botLocationsXY = new HashMap<>();
    private StackPane stack;
    private List<Player> players = new ArrayList<>();

    // map size constants
    private static final int MAP_WIDTH_IN_TILES = 40;
    private static final int MAP_HEIGHT_IN_TILES = 25;

    private BotSpawner botSpawner;
    private ServerClient serverclient;
    private Client client;
    private boolean inGame;

    public Stage getStage() {
        return stage;
    }

    public Screen(ServerClient serverclient) {
        this.serverclient = serverclient;
        this.client = serverclient.getClient();
        this.root = new Group();
        this.inGame = false;
        mapLoad = new MapLoad();
        botSpawner = new BotSpawner();
        botsOnMap = new ArrayList<>();


    }

    public Map<Integer, Double[]> getBotLocations() {
        return botLocations;
    }

    public void setBotLocations(Map<Integer, Double[]> botLocations) {
        this.botLocations = botLocations;
    }

    /**
     * Returns true if the player is in the game. Usually called by the server.
     *
     * @return boolean inGame.
     */
    public boolean isInGame() {
        return this.inGame;
    }

    /**
     * Returns the chosen map as enum. Usually called by the server.
     *
     * @return chosenMap.
     */
    public Battlefield getChosenMap() {
        return chosenMap;
    }

    /**
     * Getter method for Player.
     *
     * @return Player.
     */
    public Player getPlayer() {
        return this.player;
    }

    public Group getRoot() {
        return this.root;
    }


    /**
     * Returns bot locations in format (ID, (Xlocation,Ylocation)).
     * Usually called by the server.
     *
     * @return botLocationsXY.
     */
    public Map<Integer, Double[]> getBotLocationsXY() {
        for (Bot bot : botsOnMap) {
            Double[] xy = new Double[2];
            xy[0] = bot.getX() / stage.widthProperty().get();
            xy[1] = bot.getY() / stage.heightProperty().get();
            botLocationsXY.put(bot.getBotId(), xy);

        }
        return this.botLocationsXY;
    }

    /**
     * Setter method for botLocations.
     *
     * @param botLocations where to save the locations.
     */
    public void setBotLocationsXY(Map<Integer, Double[]> botLocations) {
        this.botLocationsXY = botLocations;
    }


    // teams scores
    int redTeamScore = 0;
    int greenTeamScore = 0;

    Battlefield chosenMap = Battlefield.EMPTY;
    Player.playerColor color = Player.playerColor.RED;


    //Constants for player object
    private int playerXStartingPosition = 20;
    private int playerYStartingPosition = 20;

    //Constants for flag object
    private Flag greenFlag;
    private Flag redFlag;


    public static int getMAP_WIDTH_IN_TILES() {
        return MAP_WIDTH_IN_TILES;
    }

    public static int getMAP_HEIGHT_IN_TILES() {
        return MAP_HEIGHT_IN_TILES;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setPlayerColor(int colorIndex) {
        if (colorIndex == 0) {
            this.color = Player.playerColor.GREEN;
        } else if (colorIndex == 1) {
            this.color = Player.playerColor.RED;
        }
    }

    public void setPlayerXStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.playerXStartingPosition = (int) stage.widthProperty().get() - 100;
        } else if (color.equals(Player.playerColor.RED)) {
            this.playerXStartingPosition = 40;
        }
    }

    public void setPlayerYStartingPosition(Stage stage) {
        if (color.equals(Player.playerColor.GREEN)) {
            this.playerYStartingPosition = (int) stage.heightProperty().get() - 500;
        } else if (color.equals(Player.playerColor.RED)) {
            this.playerYStartingPosition = 40;
        }
    }

    /**
     * Creates a player.
     * Only used for creating the client's player!
     */
    public void createPlayer() {
        player = new Player(
                playerXStartingPosition,
                playerYStartingPosition,
                0,
                0,
                color.equals(Player.playerColor.GREEN) ? Player.playerColor.GREEN : Player.playerColor.RED,
                client
        );
        player.setRoot(root);
        player.setId(serverclient.getID());
        players.add(player);
        System.out.println("Created player with id: " + player.getId());
    }

    /**
     * Creates a player.
     *
     * @param x  player starting position on x axis
     * @param y  player starting position on y axis
     * @param id player id.
     */
    public void createPlayer(double x, double y, String id) {
        Player otherPlayer = new Player(
                (int) x,
                (int) y,
                0,
                0,
                color.equals(Player.playerColor.GREEN) ? Player.playerColor.RED : Player.playerColor.GREEN,
                client
        );
        otherPlayer.setPlayerLocationXInTiles(stage.widthProperty().get() / otherPlayer.getX());
        otherPlayer.setPlayerLocationYInTiles(stage.heightProperty().get() / otherPlayer.getY());
        otherPlayer.setRoot(root);
        otherPlayer.setId(id);
        root.getChildren().add(otherPlayer);
        players.add(otherPlayer);
        System.out.println("Created opponent with id: " + otherPlayer.getId());
        updateScale();
    }

    /**
     * Set the value of chosenMap.
     * Usually called from the menu class.
     *
     * @param mapIndex
     */
    public void setMap(int mapIndex) {

        if (mapIndex == 0) {
            chosenMap = Battlefield.MAP1;
        } else if (mapIndex == 1) {
            chosenMap = Battlefield.MAP2;
        }

    }

    /**
     * Moves the player on x or y axis.
     * Usually called by the server.
     *
     * @param id        player id.
     * @param direction direction to move to.
     */
    public void movePlayerWithId(String id, byte direction) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
                switch (direction) {
                    case 1:
                        p.moveUp();
                        break;
                    case 2:
                        p.moveDown();
                        break;
                    case 3:
                        p.moveRight();
                        break;
                    case 4:
                        p.moveLeft();
                        break;
                }
            }
        }
    }

    /**
     * Stops the player.
     *
     * @param id        id of the player to stop.
     * @param direction direction to stop (on x or y axis)
     */
    public void stopPlayerWithId(String id, char direction) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
                if (direction == 'y') {
                    p.stopMovementY();
                } else {
                    p.stopPlayerMovementX();
                }
            }
        }
    }

    @Override
    public void start(Stage stage) {
        inGame = true;
        boolean fullScreen = stage.isFullScreen();
        this.stage = stage;

        stage.getScene().setRoot(root);

        if (chosenMap == Battlefield.MAP1) {
            mapLoad.loadMap1(root, stage);
        } else if (chosenMap == Battlefield.MAP2) {
            mapLoad.loadMap2(root, stage);

        } else {
            throw new EnumConstantNotPresentException(Battlefield.class, "");
        }

        // both bases
        greenBase = mapLoad.getBaseByColor(Base.baseColor.GREEN);
        redBase = mapLoad.getBaseByColor(Base.baseColor.RED);


        setPlayerYStartingPosition(stage);
        setPlayerXStartingPosition(stage);


        createPlayer();

        player.setPlayerLocationXInTiles(stage.widthProperty().get() / player.getX());
        player.setPlayerLocationYInTiles(stage.heightProperty().get() / player.getY());


        root.getChildren().add(player);

        // notify other players of your position
        Packet005SendPlayerPosition positionPacket = new Packet005SendPlayerPosition();
        positionPacket.xPosition = player.getX();
        positionPacket.yPosition = player.getY();
        positionPacket.battlefield = getChosenMap();
        positionPacket.id = player.getId();
        this.client.sendTCP(positionPacket);

        redFlag = mapLoad.getRedFlag();
        greenFlag = mapLoad.getGreenFlag();
        objectsOnMap = mapLoad.getObjectsOnMap();
        scoreBoard();

        Timeline packetTimer = new Timeline(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Packet012UpdatePlayerPosition updatePlayerPosition = new Packet012UpdatePlayerPosition();
                updatePlayerPosition.id = player.getId();
                updatePlayerPosition.positionY = (player.getY() / stage.heightProperty().get());
                updatePlayerPosition.positionX = (player.getX() / stage.widthProperty().get());
                client.sendUDP(updatePlayerPosition);
            }
        }));
        packetTimer.setCycleCount(Timeline.INDEFINITE);
        packetTimer.play();
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                for (Player p : players) {
                    bullet.bulletCollision(p, objectsOnMap, root, botSpawner, client);
                    p.setFocusTraversable(true);
                }
                player.tick(objectsOnMap, botsOnMap);
                catchTheFlag();

                for (Bot bot : botsOnMap) {
                    for (Player p : players) {
                        bot.botShooting(p, root);
                    }
                }
                bullet.bulletCollision(player, objectsOnMap, root, botSpawner, client);

                player.setOnKeyPressed(player.pressed);
                player.setOnKeyReleased(player.released);
                root.setOnMouseClicked(player.shooting);
            }
        };

        stage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                exitScreen();
            }
        });

        stage.setFullScreen(fullScreen);
        timer.start();
        stage.show();
        requestNodesFromOtherClients();

        // save bot locations
        getBotLocationsOnMap();
        updateScale();


    }

    public void updatePlayerLives(String id, int lives) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
                p.setLives(lives);
            }
        }
    }

    public void requestNodesFromOtherClients() {
        List<Base> bases = mapLoad.getBases();
        if (botLocationsXY.isEmpty()) {
            botSpawner.spawnBots(4, stage, root, bases, mapLoad.getObjectsOnMap());
            botsOnMap = botSpawner.getBotsOnMap();
        } else {
            Packet004RequestPlayers requestPlayers = new Packet004RequestPlayers();
            requestPlayers.battlefield = getChosenMap();
            client.sendTCP(requestPlayers);
            for (Map.Entry<Integer, Double[]> entry : botLocationsXY.entrySet()) {
                Double[] positions = entry.getValue();
                int id = entry.getKey();
                botSpawner.spawnBotsWithIdAndLocation(id, 4, (int) (positions[0] * stage.widthProperty().get()), (int) (positions[1] * stage.heightProperty().get()), stage, root, bases, mapLoad.getObjectsOnMap());
                botsOnMap = botSpawner.getBotsOnMap();
            }
        }
    }

    /**
     * Method to update bot lives.
     * Usually called from the server.
     *
     * @param botId    botID who's lives are to be updated.
     * @param botLives How many bot lives to assign to the bot.
     */
    public void updateBotLives(int botId, int botLives) {
        for (int i = 0; i < botsOnMap.size(); i++) {
            Bot bot = botsOnMap.get(i);
            if (bot.getBotId() == botId) {
                bot.lives = botLives;
                System.out.println("new bot lives: " + bot.lives);
                if (bot.lives == 0) {
                    botSpawner.botsOnMap.remove(bot);
                    root.getChildren().remove(bot);
                }
            }
        }
    }

    /**
     * Method to exit the screen.
     */
    private void exitScreen() {
        Packet008SendPlayerID sendPlayerID = new Packet008SendPlayerID();
        sendPlayerID.playerID = player.getId();
        this.client.sendTCP(sendPlayerID);
        stage.close();
        Menu menu = new Menu(serverclient);
        menu.start(new Stage());

    }

    /**
     * Updates a player's position.
     *
     * @param id        id of the player who's position is to be updated.
     * @param positionX new position value on the x axis.
     * @param positionY new position value on the y axis.
     */
    public void updatePlayerPosition(String id, int positionX, int positionY) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
                p.setX(positionX);
                p.setY(positionY);
                System.out.println(String.format("Updated player position to (%d,%d)", positionX, positionY));
                System.out.println(String.format("Player position now (%d,%d)", (int) p.getX(), (int) p.getY()));
            }
        }
    }

    /**
     * Removes the player with a given id.
     *
     * @param id ID of the player to remove.
     */
    public void removePlayerWithId(String id) {
        for (Node node : root.getChildren()) {
            if (node instanceof Player) {
                if (id.equals(node.getId())) {
                    System.out.println("Removed player");
                    root.getChildren().remove(node);
                }
            }
        }
    }

    /**
     * Update the scaling of objects on map.
     * Used for player and bots.
     * Sets the size and location according to the JavaFx Stage of this class (main game window).
     */
    private void updateScale() {
        final double initialStageWidth = stage.widthProperty().get();
        final double initialStageHeight = stage.heightProperty().get();
        //player init
        for (Player p : players) {
            p.setFitWidth(initialStageWidth / MAP_WIDTH_IN_TILES * 1.5);
            p.setFitHeight(initialStageHeight / MAP_HEIGHT_IN_TILES * 1.5);
        }

        //bot init
        for (Bot bot : botsOnMap) {
            bot.setBotWidth(initialStageWidth / MAP_WIDTH_IN_TILES * 2);
            bot.setBotHeight(initialStageHeight / MAP_HEIGHT_IN_TILES * 2);
            bot.setX(initialStageWidth / botLocations.get(bot.getBotId())[0]);
            bot.setY(initialStageHeight / botLocations.get(bot.getBotId())[1]);
        }

        stage.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            for (Player p : players) {
                p.setFitWidth((double) newWidth / MAP_WIDTH_IN_TILES * 1.5);
            }
            for (Bot bot : botsOnMap) {
                bot.setBotWidth((double) newWidth / MAP_WIDTH_IN_TILES * 2);
                bot.setX((double) newWidth / botLocations.get(bot.getBotId())[0]);
            }
        });

        stage.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            for (Player p : players) {
                p.setFitHeight((double) newHeight / MAP_HEIGHT_IN_TILES * 1.5);
            }
            for (Bot bot : botsOnMap) {
                bot.setBotHeight((double) newHeight / MAP_HEIGHT_IN_TILES * 2);
                bot.setY((double) newHeight / botLocations.get(bot.getBotId())[1]);
            }
        });
    }

    /**
     * Adds bot locations according to the windows size to botLocations variable.
     */
    private void getBotLocationsOnMap() {
        double initialStageWidth = stage.widthProperty().get();
        double initialStageHeight = stage.heightProperty().get();
        for (Bot bot : botsOnMap) {
            Double[] botXY = new Double[2];
            botXY[0] = initialStageWidth / bot.getX();
            botXY[1] = initialStageHeight / bot.getY();
            botLocations.put(bot.getBotId(), botXY);
        }
    }

    /**
     * Player can catch the enemy team`s flag if intersects with it and bring to his base.
     * If enemy team`s flag is brought to own base then the next round starts.
     */
    public void catchTheFlag() {
        for (Player p: players){
            if (p.getColor() == Player.playerColor.RED) {
                if (p.getBoundsInParent().intersects(redFlag.getBoundsInParent())) {
                    if (p.getX() > redBase.getRightX() - redBase.getRightX() / 5) {
                        redFlag.relocate(p.getX() + 10, p.getY() + 10);
                    } else {
                        redFlag.relocate(redBase.getLeftX() + 50, redBase.getBottomY() / 2 - greenFlag.getHeight());
                        redTeamScore += 1;
                        root.getChildren().remove(stack);
                        scoreBoard();
                        timer.stop();
                        Timeline playtime = new Timeline(
                                new KeyFrame(Duration.seconds(0), event -> p.x = 40),
                                new KeyFrame(Duration.seconds(0), event -> p.y = 40),
                                new KeyFrame(Duration.seconds(0), event -> redFlag.relocate(greenBase.getRightX() - 50,
                                        greenBase.getBottomY() / 2 - redFlag.getHeight())),
                                new KeyFrame(Duration.seconds(0.5), event -> timer.start())
                        );
                        playtime.play();
                    }
                }
            } else {
                if (p.getBoundsInParent().intersects(greenFlag.getBoundsInParent())) {
                    if (p.getX() < greenBase.getLeftX()) {
                        greenFlag.relocate(p.getX() + 10, p.getY() + 10);
                    } else {
                        greenFlag.relocate(greenBase.getRightX() - 50,
                                greenBase.getBottomY() / 2);
                        greenTeamScore += 1;
                        root.getChildren().remove(stack);
                        scoreBoard();
                        timer.stop();
                        Timeline playtime = new Timeline(
                                new KeyFrame(Duration.seconds(0), event -> p.x = (int) stage.widthProperty().get() - 100),
                                new KeyFrame(Duration.seconds(0), event -> p.y = (int) stage.heightProperty().get() - 500),
                                new KeyFrame(Duration.seconds(0), event -> greenFlag.relocate(redBase.getLeftX() + 50,
                                        redBase.getBottomY() / 2)),
                                new KeyFrame(Duration.seconds(0.5), event -> timer.start())
                        );
                        playtime.play();
                    }
                }
            }
        }

    }

    /**
     * Makes scoreboard.
     */
    public void scoreBoard() {
        Rectangle scoreBoard = new Rectangle(500, 40);
        scoreBoard.setFill(Color.LIGHTGRAY);
        Text redTeam = new Text("Red Team " + redTeamScore);
        Text greenTeam = new Text("Green Team " + greenTeamScore);
        redTeam.setFill(Color.RED);
        redTeam.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
        greenTeam.setFill(Color.GREEN);
        greenTeam.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
        stack = new StackPane();
        GridPane scores = new GridPane();
        stack.setLayoutX(stage.widthProperty().get() / 2 - (scoreBoard.getWidth() / 2));
        scores.setHgap(40);
        stack.getChildren().add(scoreBoard);
        scores.add(redTeam, 1, 0);
        scores.add(greenTeam, 2, 0);
        stack.getChildren().add(scores);
        root.getChildren().add(stack);
    }
}
