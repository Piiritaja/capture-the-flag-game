package networking;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientNetworkListener extends Listener {
    Client client;

    public void init(Client client) {
        this.client = client;
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("You are connected!");


    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("You are disconnected!");
    }

}
