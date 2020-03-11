package networking;

import Game.Menu;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerClient {
    private Client client;

    public ServerClient() {
        this.client = new Client();
        registerPackets();
    }

    private void registerPackets() {
        Kryo kryo = client.getKryo();
        kryo.register(Packets.Packet01Message.class);
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
        try {
            client.connect(5000, "localhost", 54555, 54777);
            System.out.println("Client connected");
            String[] arguments = {"tere"};
            Menu.main(arguments);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
