package networking;


import Game.player.AiPlayer;
import Game.player.GamePlayer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javafx.application.Platform;
import networking.packets.Packet001AllowAccess;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet003SendConnections;
import networking.packets.Packet004RequestPlayers;
import networking.packets.Packet005SendPlayerPosition;
import networking.packets.Packet006RequestBotsLocation;
import networking.packets.Packet007SendBotsLocation;
import networking.packets.Packet008SendPlayerID;
import networking.packets.Packet009BotHit;
import networking.packets.Packet010PlayerMovement;
import networking.packets.Packet011PlayerMovementStop;
import networking.packets.Packet012UpdatePlayerPosition;
import networking.packets.Packet013PlayerHit;
import networking.packets.Packet014PlayerDisconnected;
import networking.packets.Packet015RequestAI;
import networking.packets.Packet016SendAiPlayer;
import networking.packets.Packet017GamePlayerShoot;
import networking.packets.Packet018PlayerConnected;
import networking.packets.Packet021RequestGames;
import networking.packets.Packet022JoinGame;
import networking.packets.Packet023RequestGame;
import networking.packets.Packet025Score;

import java.util.List;

public class ClientNetworkListener extends Listener {
    private ServerClient serverClient;


    /**
     * Assign client to listener.
     *
     * @param serverClient client to assign to listener.
     */
    public void init(ServerClient serverClient) {
        this.serverClient = serverClient;
    }

    /**
     * Run when client connect's to the server.
     *
     * @param connection current connection.
     */
    @Override
    public void connected(Connection connection) {
        System.out.println("You are connected!");

    }

    /**
     * Run when client disconnect's from the server.
     * exits the game.
     *
     * @param connection current connection.
     */
    @Override
    public void disconnected(Connection connection) {
        System.out.println("You are disconnected!");

        // Runnable needed to call to exit the program on java fx application thread.
        if (this.serverClient.getMenu().getScreen().getPlayer().getId() != null) {
            Packet013PlayerHit playerHit = new Packet013PlayerHit();
            playerHit.playerID = serverClient.getID();
            playerHit.playerLives = 0;
            connection.sendTCP(playerHit);

        }

    }

    /**
     * Run when a network packet is received from the sever or other clients.
     *
     * @param connection current connection.
     * @param object     packet that was received.
     */
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Packet001AllowAccess) {
            if (!((Packet001AllowAccess) object).allow) {
                System.out.println("Connection not allowed!");
                System.out.println("Disconnecting...");
                connection.close();
            } else {
                System.out.println("Connection allowed");
                serverClient.setID(((Packet001AllowAccess) object).id);
                System.out.println("Your id: " + serverClient.getID());
                connection.sendTCP(new Packet002RequestConnections());
            }
            System.out.println();

        } else if (object instanceof Packet003SendConnections) {
            int connections = ((Packet003SendConnections) object).connections;
            if (connections == 1) {
            } else {

            }
            this.serverClient.getMenu().setNumberOfCurrentConnections(connections);
        } else if (object instanceof Packet004RequestPlayers) {
            if (serverClient.getMenu().getScreen().isInGame() && serverClient.getMenu().getScreen().getGameId().equals(((Packet004RequestPlayers) object).gameId)) {
                Packet005SendPlayerPosition sendPlayerPosition = new Packet005SendPlayerPosition();

                sendPlayerPosition.gameId = serverClient.getMenu().getScreen().getGameId();
                sendPlayerPosition.xPosition = serverClient.getMenu().getScreen().getPlayer().getX();
                sendPlayerPosition.yPosition = serverClient.getMenu().getScreen().getPlayer().getY();
                sendPlayerPosition.battlefield = ((Packet004RequestPlayers) object).battlefield;
                sendPlayerPosition.id = serverClient.getMenu().getScreen().getPlayer().getId();
                sendPlayerPosition.pColor = serverClient.getMenu().getScreen().getPlayer().getColor().equals(GamePlayer.playerColor.GREEN) ? 'G' : 'R';
                sendPlayerPosition.lives = serverClient.getMenu().getScreen().getPlayer().getLives();

                connection.sendTCP(sendPlayerPosition);
            }

        } else if (object instanceof Packet005SendPlayerPosition) {
            if (this.serverClient.getMenu().getScreen().isInGame() && ((Packet005SendPlayerPosition) object).gameId.equals(this.serverClient.getMenu().getScreen().getGameId())) {
                double playerXPosition = ((Packet005SendPlayerPosition) object).xPosition;
                double playerYPosition = ((Packet005SendPlayerPosition) object).yPosition;
                String id = ((Packet005SendPlayerPosition) object).id;
                char playerColor = ((Packet005SendPlayerPosition) object).pColor;
                int lives = ((Packet005SendPlayerPosition) object).lives;
                Platform.runLater(() -> this.serverClient.getMenu().getScreen().createPlayer(playerXPosition, playerYPosition, id, playerColor, lives));
            }

        } else if (object instanceof Packet006RequestBotsLocation) {
            if (serverClient.getMenu().getScreen().isInGame()
                    && ((Packet006RequestBotsLocation) object).gameId.equals(serverClient.getMenu().getScreen().getGameId())
                    && serverClient.getMenu().getScreen().isMaster()) {
                Packet007SendBotsLocation sendBots = new Packet007SendBotsLocation();
                sendBots.locations = serverClient.getMenu().getScreen().getBotLocationsXY();
                sendBots.gameId = ((Packet006RequestBotsLocation) object).gameId;
                connection.sendTCP(sendBots);

            }

        } else if (object instanceof Packet007SendBotsLocation) {
            if (((Packet007SendBotsLocation) object).gameId.equals(serverClient.getMenu().getScreen().getGameId())) {
                serverClient.getMenu().getScreen().setBotLocationsXY(((Packet007SendBotsLocation) object).locations);
            }

        } else if (object instanceof Packet008SendPlayerID) {
            Platform.runLater(() -> this.serverClient.getMenu().getScreen().removePlayerWithId(((Packet008SendPlayerID) object).playerID));
        } else if (object instanceof Packet009BotHit) {
            Platform.runLater(() -> serverClient.getMenu().getScreen().updateBotLives(((Packet009BotHit) object).botId, ((Packet009BotHit) object).lives));

        } else if (object instanceof Packet010PlayerMovement) {
            Platform.runLater(() -> serverClient.getMenu().getScreen().movePlayerWithId(((Packet010PlayerMovement) object).playerId, ((Packet010PlayerMovement) object).direction));
        } else if (object instanceof Packet011PlayerMovementStop) {
            Platform.runLater(() -> serverClient.getMenu().getScreen().stopPlayerWithId(((Packet011PlayerMovementStop) object).playerID, ((Packet011PlayerMovementStop) object).direction));
        } else if (object instanceof Packet012UpdatePlayerPosition) {
            if (serverClient.getMenu().getScreen().isInGame()) {
                Platform.runLater(() -> serverClient.getMenu().getScreen().updatePlayerPosition(
                        ((Packet012UpdatePlayerPosition) object).PlayerId,
                        (int) (((Packet012UpdatePlayerPosition) object).positionX * serverClient.getMenu().getScreen().getStage().widthProperty().get()),
                        (int) (((Packet012UpdatePlayerPosition) object).positionY * serverClient.getMenu().getScreen().getStage().heightProperty().get())));
            }
        } else if (object instanceof Packet013PlayerHit) {
            Platform.runLater(() -> serverClient.getMenu().getScreen().updatePlayerLives(((Packet013PlayerHit) object).playerID, ((Packet013PlayerHit) object).playerLives));
        } else if (object instanceof Packet014PlayerDisconnected) {
            Platform.runLater(() -> serverClient.getMenu().getScreen().removeDisconnectedPlayer());
        } else if (object instanceof Packet015RequestAI) {
            if (this.serverClient.getMenu().getScreen().getGameId().equals(((Packet015RequestAI) object).gameId)
                    && serverClient.getMenu().getScreen().isInGame()
                    && serverClient.getMenu().getScreen().isMaster()) {
                List<AiPlayer> players = this.serverClient.getMenu().getScreen().getAiPlayers();
                for (AiPlayer player : players) {
                    double x = player.getX();
                    double y = player.getY();
                    String id = player.getId();
                    char color = player.getColor().equals(GamePlayer.playerColor.GREEN) ? 'G' : 'R';
                    Packet016SendAiPlayer sendAiPlayer = new Packet016SendAiPlayer();
                    sendAiPlayer.gameId = this.serverClient.getMenu().getScreen().getGameId();
                    sendAiPlayer.pColor = color;
                    sendAiPlayer.xPosition = x;
                    sendAiPlayer.yPosition = y;
                    sendAiPlayer.id = id;
                    connection.sendTCP(sendAiPlayer);

                }
            }

        } else if (object instanceof Packet016SendAiPlayer) {
            GamePlayer.playerColor color = ((Packet016SendAiPlayer) object).pColor == 'G' ? GamePlayer.playerColor.GREEN : GamePlayer.playerColor.RED;
            Platform.runLater(() -> serverClient.getMenu().getScreen().createAi(color, ((Packet016SendAiPlayer) object).xPosition, ((Packet016SendAiPlayer) object).yPosition, ((Packet016SendAiPlayer) object).id));
        } else if (object instanceof Packet017GamePlayerShoot) {
            Platform.runLater(() -> serverClient.getMenu().getScreen().shootPlayerWithId(((Packet017GamePlayerShoot) object).playerId, ((Packet017GamePlayerShoot) object).mouseX, ((Packet017GamePlayerShoot) object).mouseY));

        } else if (object instanceof Packet018PlayerConnected) {
            if (((Packet018PlayerConnected) object).gameId.equals(serverClient.getMenu().getScreen().getGameId())) {
                Platform.runLater(() -> {
                    if (serverClient.getMenu().getScreen().canTickPlayers()) {
                        serverClient.getMenu().getScreen().tickPlayers();
                    } else {
                        serverClient.getMenu().getScreen().updateGameLabel();
                    }
                });
            }

        } else if (object instanceof Packet021RequestGames) {
            this.serverClient.getMenu().setAvailableMaps(((Packet021RequestGames) object).maps);
        } else if (object instanceof Packet022JoinGame) {
            this.serverClient.getMenu().getScreen().setMap(((Packet022JoinGame) object).mapIndex);
            this.serverClient.getMenu().getScreen().setPlayerCount(((Packet022JoinGame) object).gameCount);
            this.serverClient.getMenu().getScreen().setBotLocationsXY(((Packet022JoinGame) object).bots);
            Platform.runLater(() -> this.serverClient.getMenu().joinGame());
        } else if (object instanceof Packet023RequestGame) {
            Platform.runLater(() -> this.serverClient.getMenu().displayGame(((Packet023RequestGame) object).gameId, ((Packet023RequestGame) object).mapIndex, ((Packet023RequestGame) object).playerCount));
        } else if (object instanceof Packet025Score) {
            if (((Packet025Score) object).gameId.equals(this.serverClient.getMenu().getScreen().getGameId())) {
                Platform.runLater(() -> this.serverClient.getMenu().getScreen().score(((Packet025Score) object).team, ((Packet025Score) object).score));
            }

        }
    }

}
