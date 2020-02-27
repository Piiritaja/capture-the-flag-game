package networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ServerListener extends Listener {

    public void connected() {
        System.out.println("Someone has disconnected");

    }

    public void disconnected() {
        System.out.println("Someone has connected");
    }


    public void recived(Connection connection, Object object) {
        if (object instanceof Packets.Packet01Message) {
            Packets.Packet01Message request = (Packets.Packet01Message) object;
            System.out.println("[Server] >> " + request.message);

        }
    }
}
