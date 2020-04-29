package Game;

import Game.bots.Bot;
import Game.bots.BotSpawner;
import Game.maps.Base;
import Game.maps.Battlefield;
import Game.maps.MapLoad;
import Game.maps.Object;
import Game.player.AiPlayer;
import Game.player.Bullet;
import Game.player.Flag;
import Game.player.GamePlayer;
import Game.player.Player;
import com.esotericsoftware.kryonet.Client;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
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
import networking.packets.Packet015RequestAI;
import networking.packets.Packet018PlayerConnected;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Screen extends Application {

    Bullet bullet = new Bullet(0, 0, 3, Color.GREEN, true);

    private GamePlayer player;
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
    private List<AiPlayer> aiPlayers = new ArrayList<>();
    private List<Player> deadPlayers = new ArrayList<>();
    private boolean master;
    int step = 2;
    List<Base> bases;
    private int playerCount;

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

    public boolean isMaster() {
        return master;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public Screen(ServerClient serverclient) {
        this.serverclient = serverclient;
        this.client = serverclient.getClient();
        this.root = new Group();
        this.inGame = false;
        this.mapLoad = new MapLoad();
        this.botSpawner = new BotSpawner();
        this.botsOnMap = new ArrayList<>();
        this.master = false;
        this.playerCount = 1;


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
    public GamePlayer getPlayer() {
        return this.player;
    }

    public Group getRoot() {
        return this.root;
    }

    public List<AiPlayer> getAiPlayers() {
        return this.aiPlayers;
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
    GamePlayer.playerColor color = GamePlayer.playerColor.RED;


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
            this.color = GamePlayer.playerColor.GREEN;
        } else if (colorIndex == 1) {
            this.color = GamePlayer.playerColor.RED;
        }
    }

    /**
     * Creates a player.
     * Only used for creating the client's player!
     */
    public void createPlayer() {
        player = new GamePlayer(
                (int) Player.calcPlayerXStartingPosition(greenBase, redBase, color),
                (int) Player.calcPlayerYStartingPosition(greenBase, redBase, color),
                0,
                0,
                color.equals(GamePlayer.playerColor.GREEN) ? GamePlayer.playerColor.GREEN : GamePlayer.playerColor.RED,
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
    public void createPlayer(double x, double y, String id, char colorChar, int lives) {
        GamePlayer.playerColor playerColor = colorChar == 'G' ? GamePlayer.playerColor.GREEN : GamePlayer.playerColor.RED;
        GamePlayer otherPlayer = new GamePlayer(
                (int) x,
                (int) y,
                0,
                0,
                playerColor,
                client
        );
        otherPlayer.setPlayerLocationXInTiles(stage.widthProperty().get() / otherPlayer.getX());
        otherPlayer.setPlayerLocationYInTiles(stage.heightProperty().get() / otherPlayer.getY());
        otherPlayer.setRoot(root);
        otherPlayer.setId(id);
        root.getChildren().add(otherPlayer);
        players.add(otherPlayer);
        otherPlayer.setLives(lives);
        otherPlayer.setLives(lives);
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

    public void shootPlayerWithId(String id, double mouseX, double mouseY) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
                p.shoot(mouseX, mouseY, false);
            }
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
            if (p instanceof AiPlayer) {
                if (p.getId().equals(id)) {
                    switch (direction) {
                        case 1:
                            ((AiPlayer) p).movementPositionUp();
                            break;
                        case 2:
                            ((AiPlayer) p).movementPositionDown();
                            break;
                        case 3:
                            ((AiPlayer) p).movementPositionRight();
                            break;
                        case 4:
                            ((AiPlayer) p).movementPositionLeft();
                            break;
                    }
                }
            } else if (p.getId().equals(id)) {
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

    public void createAi(GamePlayer.playerColor color, double startX, double startY, String id) {
        Base base;
        Flag flag;
        if (color.equals(GamePlayer.playerColor.GREEN)) {
            base = greenBase;
            flag = mapLoad.getGreenFlag();
        } else {
            base = redBase;
            flag = mapLoad.getRedFlag();
        }

        AiPlayer ai = new AiPlayer(
                (int) startX,
                (int) startY,
                0,
                0,
                color,
                flag,
                root,
                base,
                client,
                isMaster()
        );
        ai.setId(id);
        root.getChildren().add(ai);
        aiPlayers.add(ai);
        players.add(ai);

    }

    public void createAi(GamePlayer.playerColor color) {
        double startX;
        double startY;
        Base base;
        if (color.equals(GamePlayer.playerColor.GREEN)) {
            base = greenBase;
        } else {
            base = redBase;
        }

        double rangeX = ((base.getRightX() - base.getLeftX()) + 1);
        double rangeY = ((base.getBottomY() - base.getTopY()) + 1);
        startX = (Math.random() * rangeX) + base.getLeftX();
        startY = (Math.random() * rangeY) + base.getTopY();
        createAi(color, startX, startY, UUID.randomUUID().toString().substring(0, 4));

    }

    @Override
    public void start(Stage stage) {
        System.out.println(getPlayerCount());
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
        bases = mapLoad.getBases();

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
        char colorChar = color.equals(GamePlayer.playerColor.GREEN) ? 'G' : 'R';
        positionPacket.pColor = colorChar;
        positionPacket.lives = player.lives;
        this.client.sendTCP(positionPacket);

        redFlag = mapLoad.getRedFlag();
        greenFlag = mapLoad.getGreenFlag();
        objectsOnMap = mapLoad.getObjectsOnMap();
        scoreBoard();
        requestNodesFromOtherClients();


        Timeline packetTimer = new Timeline(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (player != null) {
                    Packet012UpdatePlayerPosition updatePlayerPosition = new Packet012UpdatePlayerPosition();
                    updatePlayerPosition.id = player.getId();
                    updatePlayerPosition.positionY = (player.getY() / stage.heightProperty().get());
                    updatePlayerPosition.positionX = (player.getX() / stage.widthProperty().get());
                    client.sendUDP(updatePlayerPosition);
                }
                if (isMaster()) {
                    for (AiPlayer aiPlayer : aiPlayers) {
                        Packet012UpdatePlayerPosition updateAiPosition = new Packet012UpdatePlayerPosition();
                        updateAiPosition.id = aiPlayer.getId();
                        updateAiPosition.positionX = (aiPlayer.getX() / stage.widthProperty().get());
                        updateAiPosition.positionY = (aiPlayer.getY() / stage.heightProperty().get());
                        client.sendUDP(updateAiPosition);
                    }
                }

            }
        }));
        packetTimer.setCycleCount(Timeline.INDEFINITE);
        packetTimer.play();


        stage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                exitScreen();
            }
        });

        if (!client.isConnected() && canTickPlayers()) {
            tickPlayers();
        }

        stage.setFullScreen(fullScreen);
        stage.show();

        // save bot locations
        getBotLocationsOnMap();
        updateScale();
        Packet018PlayerConnected playerConnected = new Packet018PlayerConnected();
        client.sendTCP(playerConnected);

    }

    public boolean canTickPlayers() {
        int count = 0;
        for (Player p : players) {
            if (p instanceof GamePlayer) {
                count++;
            }
        }
        if (count == getPlayerCount()) {
            return true;
        }
        return false;

    }

    public void tickPlayers() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                for (int a = 0; a < players.size(); a++) {
                    Player p = players.get(a);
                    bullet.bulletCollision(players, objectsOnMap, root, botSpawner, client, p, deadPlayers, mapLoad, a);
                    catchTheFlag(p);
                    for (Bot bot : botsOnMap) {
                        if (!deadPlayers.contains(p)) {
                            bot.botShooting(p, root);
                        }
                    }
                    playersSpawningCorrection(p);
                }
                //Only this player can tick!
                if (player != null) {
                    player.tick(objectsOnMap, botsOnMap, players);

                }

                if (isMaster()) {
                    for (AiPlayer ai : aiPlayers) {
                        if (!deadPlayers.contains(ai)) {
                            ai.tick(objectsOnMap, botsOnMap, stage, players, deadPlayers);
                        }
                    }
                }
                if (player != null) {
                    player.setOnKeyPressed(player.pressed);
                    player.setOnKeyReleased(player.released);
                    root.setOnMouseClicked(player.shooting);
                    player.setFocusTraversable(true);
                }
                if (redTeamScore == 3 || greenTeamScore == 3) {
                    theEnd();
                }
                if (deadPlayers.contains(player)) {
                    player.setDead(true);
                }
            }
        };
        timer.start();
    }

    /**
     * If player spawns on wall, finds new location.
     * @param p Player
     */
    public void playersSpawningCorrection(Player p) {
        if (p.getClass().equals(GamePlayer.class)) {
            GamePlayer player = (GamePlayer) p;
            for (Object object : objectsOnMap) {
                if (object.collides(player)) {
                    player.setPlayerXStartingPosition(greenBase, redBase);
                    player.setPlayerYStartingPosition(greenBase, redBase);
                }
            }
        }
        if (p.getClass().equals(AiPlayer.class)) {
            AiPlayer ai = (AiPlayer) p;
            for (Object object : objectsOnMap) {
                if (object.collides(ai.collisionBoundary)) {
                    ai.setPlayerXStartingPosition(greenBase, redBase);
                    ai.setPlayerYStartingPosition(greenBase, redBase);
                }
            }
        }
    }

    public void updatePlayerLives(String id, int lives) {
        for (Player p : players) {
            if (p.getId().equals(id)) {
                p.setLives(lives);
                if (lives <= 0) {
                    root.getChildren().remove(p);
                }
            }
        }
    }

    public void requestNodesFromOtherClients() {
        List<Base> bases = mapLoad.getBases();
        if (botLocationsXY.isEmpty()) {
            System.out.println("Empty");
            createAi(GamePlayer.playerColor.GREEN);
            createAi(GamePlayer.playerColor.RED);
            botSpawner.spawnBots(4 - botsOnMap.size(), stage, root, bases, mapLoad.getObjectsOnMap(), true);
            botsOnMap = botSpawner.getBotsOnMap();
            master = true;
            setBotLocationsXY(getBotLocationsXY());
        } else {
            Packet004RequestPlayers requestPlayers = new Packet004RequestPlayers();
            requestPlayers.battlefield = getChosenMap();
            client.sendTCP(requestPlayers);
            spawnBots();
            Packet015RequestAI requestAI = new Packet015RequestAI();
            requestAI.battlefield = this.getChosenMap();
            client.sendTCP(requestAI);
        }
    }

    public void spawnBots() {
        for (Map.Entry<Integer, Double[]> entry : botLocationsXY.entrySet()) {
            Double[] positions = entry.getValue();
            int id = entry.getKey();
            botSpawner.spawnBotsWithIdAndLocation(id, 4, (int) (positions[0] * stage.widthProperty().get()), (int) (positions[1] * stage.heightProperty().get()), stage, root, false);
            botsOnMap = botSpawner.getBotsOnMap();
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
        player = null;
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
            }
        }
    }

    /**
     * Removes the player with a given id.
     *
     * @param id ID of the player to remove.
     */
    public void removePlayerWithId(String id) {
        Player playerToRemove = null;
        for (Player cPlayer : players) {
            if (cPlayer.getId().equals(id)) {
                root.getChildren().remove(cPlayer);
                playerToRemove = cPlayer;
                System.out.println("Removed a player");

            }
        }

        if (playerToRemove != null) {
            System.out.println(players.contains(playerToRemove));
            players.remove(playerToRemove);
            deadPlayers.remove(playerToRemove);
            System.out.println("Removed a player from players list");
        }
    }

    public void removeDisconnectedPlayer() {
        for (Player cPlayer : players) {
            if (cPlayer instanceof GamePlayer && !((GamePlayer) cPlayer).getClient().isConnected()) {
                removePlayerWithId(cPlayer.getId());
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
    public void catchTheFlag(Player player) {
        if (player.getColor() == GamePlayer.playerColor.RED) {
            if (player.getBoundsInParent().intersects(redFlag.getBoundsInParent())) {
                if (player.getPickedUpFlag() == null && !redFlag.isPickedUp()) {
                    player.pickupFlag(redFlag);
                }
                if (!player.getBoundsInParent().intersects(redBase.getBoundsInParent())) {
                    redFlag.relocate(player.getX() + 10, player.getY() + 10);
                } else {
                    redFlag.relocate(redBase.getLeftX() + 50, redBase.getBottomY() / 2 - greenFlag.getHeight());
                    redTeamScore += 1;
                    newRound();
                    player.dropPickedUpFlag();
                }
            }
        } else {
            if (player.getBoundsInParent().intersects(greenFlag.getBoundsInParent())) {
                if (player.getPickedUpFlag() == null && !greenFlag.isPickedUp()) {
                    player.pickupFlag(greenFlag);
                }
                if (!player.getBoundsInParent().intersects(greenBase.getBoundsInParent())) {
                    greenFlag.relocate(player.getX() + 10, player.getY() + 10);
                } else {
                    greenFlag.relocate(greenBase.getRightX() - 50,
                            greenBase.getBottomY() / 2);
                    greenTeamScore += 1;
                    newRound();
                    player.dropPickedUpFlag();
                }
            }
        }
    }

    /**
     * Starts new round.
     * Sets new score, sets all players, bots and flags to starting position.
     */
    public void newRound() {
        System.out.println(greenFlag.isPickedUp());
        System.out.println(redFlag.isPickedUp());
        root.getChildren().remove(stack);
        scoreBoard();
        timer.stop();
        for (Bot bot : botsOnMap) {
            root.getChildren().remove(bot);
        }
        botsOnMap.clear();
        spawnBots();
        greenFlag.relocate(redBase.getLeftX() + 50, redBase.getBottomY() / 2);
        redFlag.relocate(greenBase.getRightX() - 50, greenBase.getBottomY() / 2 - redFlag.getHeight());
        redFlag.drop();
        greenFlag.drop();
        players.addAll(deadPlayers);
        deadPlayers.clear();
        player.setDead(false);
        for (Player p : players) {
            if (player.getPickedUpFlag() != null) {
                player.dropPickedUpFlag();
            }
            Timeline playtime = new Timeline(
                    new KeyFrame(Duration.seconds(0), event -> p.setPlayerXStartingPosition(greenBase, redBase)),
                    new KeyFrame(Duration.seconds(0), event -> p.setPlayerYStartingPosition(greenBase, redBase)),
                    new KeyFrame(Duration.seconds(0), event -> p.setLives(10)),
                    new KeyFrame(Duration.seconds(0.5), event -> root.getChildren().remove(p)),
                    new KeyFrame(Duration.seconds(0.5), event -> root.getChildren().add(p)),
                    new KeyFrame(Duration.seconds(0.5), event -> p.playerDead.stop()),
                    new KeyFrame(Duration.seconds(0.5), event -> timer.start())
            );
            playtime.play();
            updateScale();
        }
        updateScale();
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

    /**
     * If one team has 3 points, game ends, winner team name is displayed on the screen.
     * Goes back to menu.
     */
    public void theEnd() {
        timer.stop();
        Text winner;
        if (redTeamScore == 3) {
            winner = new Text("RED TEAM WINS");
            winner.setFill(Color.RED);
        } else {
            winner = new Text("GREEN TEAM WINS");
            winner.setFill(Color.GREEN);
        }
        winner.setTextOrigin(VPos.TOP);
        winner.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, (stage.widthProperty().get() - 10) / 10));
        winner.layoutXProperty().bind(stage.widthProperty().subtract(winner.prefWidth(-1)).divide(2));
        winner.layoutYProperty().bind(stage.heightProperty().subtract(winner.prefHeight(-1)).divide(2));
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> root.getChildren().add(winner)),
                new KeyFrame(Duration.seconds(5), event -> exitScreen())
        );
        timeline.play();
    }
}
