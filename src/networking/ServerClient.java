package networking;

import Game.Menu;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerClient {
    private Client client;
    ClientNetworkListener clientNetworkListener;

    public ServerClient() {
        this.client = new Client();
        clientNetworkListener = new ClientNetworkListener();
        clientNetworkListener.init(this.client);
        this.client.addListener(clientNetworkListener);
        registerPackets();
        new Thread(client).start();
        String[] arguments = {"tere"};
        try {
            client.connect(5000, "localhost", 54555, 54777);
            String[] args = {"tere"};
            Menu.main(args);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    private void registerPackets() {
        Kryo kryo = client.getKryo();
        kryo.register(Packets.Packet000Request.class);
    }

    public static void main(String[] args) {
        new ServerClient();

    }
}
