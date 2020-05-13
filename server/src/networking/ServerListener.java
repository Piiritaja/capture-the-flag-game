package networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import networking.packets.Packet000RequestAccess;
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
import networking.packets.Packet020CreateGame;
import networking.packets.Packet021RequestGames;
import networking.packets.Packet022JoinGame;
import networking.packets.Packet023RequestGame;
import networking.packets.Packet024RemoveGameWithId;
import networking.packets.Packet025Score;
import networking.packets.Packet026FlagCaptured;

public class ServerListener extends Listener {
    private Server server;
    private GameServer gameServer;


    /**
     * Assign's server to listener.
     *
     * @param server server to assign to the listener.
     */
    public ServerListener(Server server, GameServer gameServer) {
        this.server = server;
        this.gameServer = gameServer;
    }

    /**
     * Run when a client connect's to the server.
     * Sends allowAccess packet.
     *
     * @param c current connection
     */
    @Override
    public void connected(Connection c) {
        System.out.println("Someone has connected");
        gameServer.setNumberOfConnections(this.gameServer.getNumberOfConnections() + 1);
        gameServer.setTotalNumberOfConnections(this.gameServer.getTotalNumberOfConnections() + 1);
        Packet001AllowAccess allowAccess = new Packet001AllowAccess();
        allowAccess.allow = true;
        allowAccess.id = "C" + gameServer.getTotalNumberOfConnections();
        c.sendTCP(allowAccess);
        System.out.println("created client with id:" + allowAccess.id);

    }

    /**
     * Run when a client connect's to the server.
     *
     * @param c current connection.
     */
    @Override
    public void disconnected(Connection c) {
        System.out.println("Someone has disconnected");
        this.gameServer.setNumberOfConnections(this.gameServer.getNumberOfConnections() - 1);
        Packet014PlayerDisconnected playerDisconnected = new Packet014PlayerDisconnected();
        server.sendToAllTCP(playerDisconnected);
        if (this.gameServer.getNumberOfConnections() == 0) {
            this.gameServer.clearData();
        }
    }


    /**
     * Run when a network packet is received from a client.
     *
     * @param connection current connection.
     * @param object     network packet that was received.
     */
    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof Packet000RequestAccess) {
            Packet001AllowAccess access = new Packet001AllowAccess();
            access.id = "C" + this.gameServer.getNumberOfConnections();
            access.allow = false;
            connection.sendTCP(access);
        } else if (object instanceof Packet002RequestConnections) {
            Packet003SendConnections sendConnections = new Packet003SendConnections();
            sendConnections.connections = this.gameServer.getNumberOfConnections();
            server.sendToAllTCP(sendConnections);

        } else if (object instanceof Packet004RequestPlayers) {
            ((Packet004RequestPlayers) object).connectionId = connection.getID();
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet005SendPlayerPosition) {
            if (((Packet005SendPlayerPosition) object).initial) {
                server.sendToAllExceptTCP(connection.getID(), object);
            }
            server.sendToTCP(((Packet005SendPlayerPosition) object).connectionId, object);
        } else if (object instanceof Packet006RequestBotsLocation) {
            if (gameServer.getBotLocations().containsKey(((Packet006RequestBotsLocation) object).gameId)) {
                Packet007SendBotsLocation sendBotsLocation = new Packet007SendBotsLocation();
                sendBotsLocation.locations = gameServer.getBotLocations().get(((Packet006RequestBotsLocation) object).gameId);
                connection.sendTCP(sendBotsLocation);
            } else {
                System.out.println("no such game");
            }
        } else if (object instanceof Packet007SendBotsLocation) {
            gameServer.setBotLocations(((Packet007SendBotsLocation) object).gameId, ((Packet007SendBotsLocation) object).locations);
        } else if (object instanceof Packet008SendPlayerID) {
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet009BotHit) {
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet010PlayerMovement) {
            server.sendToAllExceptUDP(connection.getID(), object);
        } else if (object instanceof Packet011PlayerMovementStop) {
            server.sendToAllExceptUDP(connection.getID(), object);
        } else if (object instanceof Packet012UpdatePlayerPosition) {
            server.sendToAllExceptUDP(connection.getID(), object);
        } else if (object instanceof Packet013PlayerHit) {
            server.sendToAllExceptUDP(connection.getID(), object);
        } else if (object instanceof Packet015RequestAI) {
            ((Packet015RequestAI) object).connectionId = connection.getID();
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet016SendAiPlayer) {
            server.sendToTCP(((Packet016SendAiPlayer) object).connectionId, object);
        } else if (object instanceof Packet017GamePlayerShoot) {
            server.sendToAllExceptUDP(connection.getID(), object);
        } else if (object instanceof Packet018PlayerConnected) {
            server.sendToAllTCP(object);
        } else if (object instanceof Packet020CreateGame) {
            System.out.println(((Packet020CreateGame) object).gameId);
            System.out.println(((Packet020CreateGame) object).battlefield);
            System.out.println(((Packet020CreateGame) object).playerCount);
            gameServer.createGame(((Packet020CreateGame) object).gameId, ((Packet020CreateGame) object).battlefield, ((Packet020CreateGame) object).playerCount);
        } else if (object instanceof Packet021RequestGames) {
            ((Packet021RequestGames) object).playerCounts = gameServer.getPlayerCounts();
            ((Packet021RequestGames) object).maps = gameServer.getGameMaps();
            connection.sendTCP(object);
            System.out.println("Received requestGames");
        } else if (object instanceof Packet022JoinGame) {
            if (gameServer.getBotLocations().containsKey(((Packet022JoinGame) object).gameId)) {
                System.out.println("Found the id");
                ((Packet022JoinGame) object).gameCount = gameServer.getPlayerCounts().get(((Packet022JoinGame) object).gameId);
                ((Packet022JoinGame) object).bots = gameServer.getBotLocations().get(((Packet022JoinGame) object).gameId);
                Battlefield map = gameServer.getGameMaps().get(((Packet022JoinGame) object).gameId);
                if (map.equals(Battlefield.MAP1)) {
                    ((Packet022JoinGame) object).mapIndex = 0;
                } else {
                    ((Packet022JoinGame) object).mapIndex = 1;
                }
                server.sendToTCP(connection.getID(), object);

            }
        } else if (object instanceof Packet023RequestGame) {
            if (gameServer.getBotLocations().containsKey(((Packet023RequestGame) object).gameId)) {
                Battlefield map = gameServer.getGameMaps().get(((Packet023RequestGame) object).gameId);
                int mapIndex;
                if (map.equals(Battlefield.MAP1)) {
                    mapIndex = 0;
                } else {
                    mapIndex = 1;
                }
                ((Packet023RequestGame) object).mapIndex = mapIndex;
                ((Packet023RequestGame) object).playerCount = gameServer.getPlayerCounts().get(((Packet023RequestGame) object).gameId);
                connection.sendTCP(object);
            }
        } else if (object instanceof Packet024RemoveGameWithId) {
            this.gameServer.removeGameInstances(((Packet024RemoveGameWithId) object).gameId);
        } else if (object instanceof Packet025Score) {
            server.sendToAllTCP(object);
        } else if (object instanceof Packet026FlagCaptured) {
            server.sendToAllExceptTCP(connection.getID(), object);
        }
    }
}
