package networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class ServerClient {
    private Client client;

    public ServerClient(){
        this.client = new Client();
        registerPackets();
    }

    private void registerPackets(){
        Kryo kryo = client.getKryo();
        kryo.register(Packets.Packet01Message.class);
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
        try{
            client.connect(5000, "192.168.1.200", 54555, 54777);

        } catch (IOException ignore){

        }

    }
}
