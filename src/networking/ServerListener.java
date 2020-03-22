package networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import networking.packets.Packet000RequestAccess;
import networking.packets.Packet001AllowAccess;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet003SendConnections;

public class ServerListener extends Listener {
    private Server server;

    // For keeping track of the number of connected clients.
    private int connections;


    /**
     * Assign's server to listener.
     *
     * @param server server to assign to the listener.
     */
    public ServerListener(Server server) {
        this.server = server;
        this.connections = 0;
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
        connections += 1;
        Packet001AllowAccess allowAccess = new Packet001AllowAccess();
        if (connections < 2) {
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
        connections -= 1;
    }


    /**
     * Run when a network packet is received from a client.
     *
     * @param connection current connection.
     * @param object     network packet that was received.
     */
    @Override
    public void received(Connection connection, Object object) {
        System.out.println(object);
        if (object instanceof Packet002RequestConnections) {
            Packet003SendConnections sendConnections = new Packet003SendConnections();
            sendConnections.connections = connections;
            connection.sendTCP(sendConnections);

        }

    }
}
