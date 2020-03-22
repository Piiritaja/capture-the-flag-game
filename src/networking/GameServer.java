package networking;


import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryo.Kryo;
import networking.packets.Packet000RequestAccess;
import networking.packets.Packet001AllowAccess;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet003SendConnections;

import java.io.IOException;

public class GameServer {


    private Server server;
    private ServerListener serverListener;

    //Server ports
    private static final int TCP_PORT = 54555;
    private static final int UDP_PORT = 54777;


    /**
     * Set's up server and initializes server listener
     */
    public GameServer() {
        this.server = new Server();
        this.serverListener = new ServerListener(this.server);
        setUpServer();

    }


    /**
     * Start server
     */
    public void setUpServer() {
        server.addListener(serverListener);
        registerPackets();

        try {
            server.start();
            server.bind(TCP_PORT, UDP_PORT);
            System.out.println("Connected");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Register packets for server listener
     */
    public void registerPackets() {
        Kryo kryo = server.getKryo();
        kryo.register(serverListener.getClass());
        kryo.register(Packet000RequestAccess.class);
        kryo.register(Packet001AllowAccess.class);
        kryo.register(Packet002RequestConnections.class);
        kryo.register(Packet003SendConnections.class);

    }

    public static void main(String[] args) {
        new GameServer();

    }


}


