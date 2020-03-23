package networking;

import Game.Menu;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javafx.application.Platform;
import networking.packets.Packet001AllowAccess;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet003SendConnections;

public class ClientNetworkListener extends Listener {
    private Client client;
    private ServerClient serverClient;


    /**
     * Assign client to listener.
     *
     * @param client client to assign to listener.
     */
    public void init(Client client, ServerClient serverClient) {
        this.serverClient = serverClient;
        this.client = client;
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
        Platform.runLater(() -> this.serverClient.menu.exitScreen());
    }

    /**
     * Run when a network packet is received from the sever or other clients.
     *
     * @param connection current connection.
     * @param object     packet that was received.
     */
    @Override
    public void received(Connection connection, Object object) {
        System.out.println(object);
        if (object instanceof Packet001AllowAccess) {
            if (!((Packet001AllowAccess) object).allow) {
                System.out.println("Connection not allowed!");
                System.out.println("Disconnecting...");
                connection.close();
            } else {

                System.out.println("Connection allowed");
                connection.sendTCP(new Packet002RequestConnections());
            }

        } else if (object instanceof Packet003SendConnections) {
            int connections = ((Packet003SendConnections) object).connections;
            if (connections == 1) {
                System.out.println("No other clients connected");
            } else {
                System.out.println(String.format("%d other clients connected", connections));

            }
            this.serverClient.menu.setNumberOfCurrentConnections(connections);
        }

    }

}
