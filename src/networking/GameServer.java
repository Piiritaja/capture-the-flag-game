package networking;


import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryo.Kryo;

import java.io.IOException;

public class GameServer {


    private Server server;
    private ServerListener serverListener;

    //Server ports
    private static final int TCP_PORT = 54555;
    private static final int UDP_PORT = 54777;


    public GameServer() {
        this.server = new Server();
        this.serverListener = new ServerListener();
        setUpServer();

    }

    public void setUpServer() {
        server.addListener(serverListener);

        try {
            server.bind(TCP_PORT,UDP_PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        registerPackets();

    }

    public void registerPackets() {
        Kryo kryo = server.getKryo();
        kryo.register(serverListener.getClass());
        kryo.register(Packets.Packet000Request.class);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
        server.bind(TCP_PORT, UDP_PORT);

    }


}


