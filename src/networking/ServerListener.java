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
        System.out.println(this.server.getKryo().getDepth());
        gameServer.setNumberOfConnections(this.gameServer.getNumberOfConnections() + 1);
        Packet001AllowAccess allowAccess = new Packet001AllowAccess();
        if (this.gameServer.getNumberOfConnections() <= 2) {
            allowAccess.allow = true;
        }
        c.sendTCP(allowAccess);

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
            System.out.println("Received requestAccess packet");
            Packet001AllowAccess access = new Packet001AllowAccess();
            access.allow = true;
            connection.sendTCP(access);
        } else if (object instanceof Packet002RequestConnections) {
            System.out.println("Received requestedConnections packet");
            Packet003SendConnections sendConnections = new Packet003SendConnections();
            sendConnections.connections = this.gameServer.getNumberOfConnections();
            server.sendToAllTCP(sendConnections);
            System.out.println("Sent sendConnections packet to all clients");

        } else if (object instanceof Packet004RequestPlayers) {
            System.out.println("Received requestPlayers");
            server.sendToAllExceptTCP(connection.getID(), object);
            System.out.println("Sent requestPlayers packet to all other clients");
        } else if (object instanceof Packet005SendPlayerPosition) {
            System.out.println("Received sendPlayerPosition packet");
            server.sendToAllExceptTCP(connection.getID(), object);
            System.out.println("Sent sendPlayerPosition packet to all other clients");
        } else if (object instanceof Packet006RequestBotsLocation) {
            System.out.println("Received requestBotsLocation packet");
            server.sendToAllExceptTCP(connection.getID(), object);
            System.out.println("Sent requestBotsLocation packet to all other clients");
        } else if (object instanceof Packet007SendBotsLocation) {
            System.out.println("Received sendBotsLocation packet");
            server.sendToAllExceptTCP(connection.getID(), object);
            System.out.println("Sent sendBotsLocation packet to all other clients");
        } else if (object instanceof Packet008SendPlayerID) {
            System.out.println("Received sendPlayerID packet");
            server.sendToAllExceptTCP(connection.getID(), object);
            System.out.println("Sent sendPlayerID packet to all other clients");
        } else if (object instanceof Packet009BotHit) {
            System.out.println("Received botHit packet");
            System.out.println("Bot id is: " + ((Packet009BotHit) object).botId);
            server.sendToAllExceptTCP(connection.getID(), object);
        }

    }
}
