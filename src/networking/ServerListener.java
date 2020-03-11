package networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ServerListener extends Listener {

    @Override
    public void connected(Connection c) {
        System.out.println("Someone has disconnected");

    }

    @Override
    public void disconnected(Connection c) {
        System.out.println("Someone has connected");
    }


    @Override
    public void received(Connection connection, Object object) {
        System.out.println("Looking");
        if (object instanceof Packets.Packet000Request) {
            System.out.println("Got ya");

        }
    }
}
