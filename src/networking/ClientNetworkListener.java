package networking;


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
        Platform.runLater(() -> this.serverClient.getMenu().exitScreen());
        if (this.serverClient.getMenu().getScreen().getPlayer().getId() != null) {
            Packet008SendPlayerID sendPlayerId = new Packet008SendPlayerID();
            sendPlayerId.playerID = this.serverClient.getMenu().getScreen().getPlayer().getId();
            connection.sendTCP(sendPlayerId);
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
            System.out.println("Received sendConnections packet");
            int connections = ((Packet003SendConnections) object).connections;
            if (connections == 1) {
                System.out.println("No other clients connected");
            } else {
                System.out.println(String.format("%d other clients connected", connections - 1));

            }
            this.serverClient.getMenu().setNumberOfCurrentConnections(connections);
            System.out.println();
        } else if (object instanceof Packet004RequestPlayers) {
            System.out.println("Received requestPlayers");
            if (serverClient.getMenu().getScreen().isInGame() && serverClient.getMenu().getScreen().getChosenMap() == ((Packet004RequestPlayers) object).battlefield) {
                Packet005SendPlayerPosition sendPlayerPosition = new Packet005SendPlayerPosition();

                sendPlayerPosition.xPosition = serverClient.getMenu().getScreen().getPlayer().getX();
                sendPlayerPosition.yPosition = serverClient.getMenu().getScreen().getPlayer().getY();
                sendPlayerPosition.battlefield = ((Packet004RequestPlayers) object).battlefield;
                sendPlayerPosition.id = serverClient.getMenu().getScreen().getPlayer().getId();
                System.out.println("Received id " + sendPlayerPosition.id);

                connection.sendTCP(sendPlayerPosition);
                System.out.println("Sent sendPlayers");
                System.out.println();
            }

        } else if (object instanceof Packet005SendPlayerPosition) {
            System.out.println("Received sendPlayerPosition packet");
            if (this.serverClient.getMenu().getScreen().isInGame() && ((Packet005SendPlayerPosition) object).battlefield == this.serverClient.getMenu().getScreen().getChosenMap()) {
                double playerXPosition = ((Packet005SendPlayerPosition) object).xPosition;
                double playerYPosition = ((Packet005SendPlayerPosition) object).yPosition;
                String id = ((Packet005SendPlayerPosition) object).id;
                Platform.runLater(() -> this.serverClient.getMenu().getScreen().createPlayer(playerXPosition, playerYPosition, id));
                System.out.println("Created player at:");
                System.out.println(playerXPosition);
                System.out.println(playerYPosition);
                System.out.println();
            }

        } else if (object instanceof Packet006RequestBotsLocation) {
            System.out.println("Received requestBotsLocation packet");
            if (serverClient.getMenu().getScreen().isInGame() && ((Packet006RequestBotsLocation) object).battlefield == serverClient.getMenu().getScreen().getChosenMap()) {
                System.out.println("Sending sendBotsLocation packet");
                Packet007SendBotsLocation sendBots = new Packet007SendBotsLocation();
                sendBots.locations = serverClient.getMenu().getScreen().getBotLocationsXY();
                sendBots.battlefield = ((Packet006RequestBotsLocation) object).battlefield;
                connection.sendTCP(sendBots);
                System.out.println("Sent sendBotsLocation packet");
                System.out.println();
            }

        } else if (object instanceof Packet007SendBotsLocation) {
            System.out.println("Received sendBotsLocation packet");
            System.out.println(((Packet007SendBotsLocation) object).battlefield);
            System.out.println(this.serverClient.getMenu().getScreen().getChosenMap());
            if (((Packet007SendBotsLocation) object).battlefield == serverClient.getMenu().getScreen().getChosenMap()) {
                serverClient.getMenu().getScreen().setBotLocationsXY(((Packet007SendBotsLocation) object).locations);
                System.out.println("Set bots location");
                System.out.println();
            }

        } else if (object instanceof Packet008SendPlayerID) {
            System.out.println("Received player id: " + ((Packet008SendPlayerID) object).playerID);
            this.serverClient.getMenu().getScreen().removePlayerWithId(((Packet008SendPlayerID) object).playerID);
            System.out.println();
        } else if (object instanceof Packet009BotHit) {
            System.out.println("Received BotHit packet: " + ((Packet009BotHit) object).botId);
            Platform.runLater(() -> serverClient.getMenu().getScreen().updateBotLives(((Packet009BotHit) object).botId, ((Packet009BotHit) object).lives));

        } else if (object instanceof Packet010PlayerMovement) {
            System.out.println("Received playerMovement packet");
            Platform.runLater(() -> serverClient.getMenu().getScreen().movePlayerWithId(((Packet010PlayerMovement) object).playerId, ((Packet010PlayerMovement) object).direction));
            System.out.println("Moved player with id: " + ((Packet010PlayerMovement) object).playerId);
        } else if (object instanceof Packet011PlayerMovementStop) {
            System.out.println("Received playerMovementStop packet");
            Platform.runLater(() -> serverClient.getMenu().getScreen().stopPlayerWithId(((Packet011PlayerMovementStop) object).playerID, ((Packet011PlayerMovementStop) object).direction));
            System.out.println("Stopped player with id: " + ((Packet011PlayerMovementStop) object).playerID);
        } else if (object instanceof Packet012UpdatePlayerPosition) {
            Platform.runLater(() -> serverClient.getMenu().getScreen().updatePlayerPosition(
                    ((Packet012UpdatePlayerPosition) object).id,
                    (int) (((Packet012UpdatePlayerPosition) object).positionX * serverClient.getMenu().getScreen().getStage().widthProperty().get()),
                    (int) (((Packet012UpdatePlayerPosition) object).positionY * serverClient.getMenu().getScreen().getStage().heightProperty().get())));
        } else if (object instanceof Packet013PlayerHit) {
            System.out.println("Received PlayerHit packet");
            Platform.runLater(() -> serverClient.getMenu().getScreen().updatePlayerLives(((Packet013PlayerHit) object).playerID, ((Packet013PlayerHit) object).playerLives));
        }

    }

}
