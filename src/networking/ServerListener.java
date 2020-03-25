package networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import networking.packets.Packet001AllowAccess;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet003SendConnections;
import networking.packets.Packet005SendPlayerPosition;
import networking.packets.Packet006RequestRoot;
import networking.packets.Packet007SendRoot;

import java.sql.SQLOutput;

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
        if (object instanceof Packet002RequestConnections) {
            System.out.println("Received requestedConnections packet");
            Packet003SendConnections sendConnections = new Packet003SendConnections();
            sendConnections.connections = this.gameServer.getNumberOfConnections();
            server.sendToAllTCP(sendConnections);
            System.out.println("Sent sendConnections packet to all clients");

        }
        if (object instanceof Packet005SendPlayerPosition) {
            System.out.println("Received sendPlayerPosition packet");
            server.sendToAllExceptTCP(connection.getID(), object);
            System.out.println("Sent sendPlayerPosition packet to all other clients");
        }
        if (object instanceof Packet006RequestRoot) {
            System.out.println("Received requestRoot packet");
            server.sendToAllExceptTCP(connection.getID(), new Packet006RequestRoot());
            System.out.println("Sent requestRoot packet to all other clients");
        }
        if (object instanceof Packet007SendRoot) {
            System.out.println("Received sendRoot packet");
            server.sendToAllExceptTCP(connection.getID(), object);
        }

    }
}
