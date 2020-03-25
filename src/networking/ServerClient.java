package networking;

import Game.Menu;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import networking.packets.Packet000RequestAccess;
import networking.packets.Packet001AllowAccess;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet003SendConnections;
import networking.packets.Packet005SendPlayerPosition;
import networking.packets.Packet006RequestRoot;
import networking.packets.Packet007SendRoot;

import java.io.IOException;


public class ServerClient {
    private Client client;
    ClientNetworkListener clientNetworkListener;
    Menu menu;

    //Server ports
    private static final int TCP_PORT = 54555;
    private static final int UDP_PORT = 54777;

    /**
     * Creates the client and connects it to a server on localhost.
     * Initializes client listener
     */
    public ServerClient(Menu menu) {

        this.menu = menu;
        this.client = new Client();
        clientNetworkListener = new ClientNetworkListener();

        //Add and configure listener for the client
        setupListener();

        //Register packets for the client
        registerPackets();

        //Connect the client with a new thread
        new Thread(client).start();
        try {
            client.connect(9999, "192.168.1.200", TCP_PORT, UDP_PORT);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    /**
     * Set up client listener.
     */
    private void setupListener() {
        clientNetworkListener.init(this);
        this.client.addListener(clientNetworkListener);
    }

    /**
     * Get the client associated with this server client.
     *
     * @return Client instance to return
     */
    public Client getClient() {
        return this.client;
    }


    /**
     * Register packets for client listener.
     */
    private void registerPackets() {
        Kryo kryo = client.getKryo();
        kryo.register(clientNetworkListener.getClass());
        kryo.register(Packet000RequestAccess.class);
        kryo.register(Packet001AllowAccess.class);
        kryo.register(Packet002RequestConnections.class);
        kryo.register(Packet003SendConnections.class);
        kryo.register(Packet005SendPlayerPosition.class);
        kryo.register(Packet006RequestRoot.class);
        kryo.register(Packet007SendRoot.class);
        kryo.register(javafx.scene.Group.class);

    }
}
