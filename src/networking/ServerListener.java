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
        c.sendTCP(playerDisconnected);
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
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet005SendPlayerPosition) {
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet006RequestBotsLocation) {
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet007SendBotsLocation) {
            System.out.println("SendBotsLocation");
            server.sendToAllExceptTCP(connection.getID(), object);
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
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet016SendAiPlayer) {
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof Packet017GamePlayerShoot) {
            System.out.println("Received gamePlayerShoot");
            server.sendToAllExceptUDP(connection.getID(), object);
        }

    }
}
