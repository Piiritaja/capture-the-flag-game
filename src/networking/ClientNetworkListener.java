package networking;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class ClientNetworkListener extends Listener {
    Client client;

    public void init(Client client) {
        this.client = client;
    }

    @Override
    public void connected (Connection connection) {
        System.out.println("You are connected!");
        try {
            throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Packets.Packet000Request request = new Packets.Packet000Request();
        client.sendTCP(request);

    }

}
