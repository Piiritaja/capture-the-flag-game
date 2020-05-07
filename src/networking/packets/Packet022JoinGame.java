package networking.packets;

import java.util.Map;

public class Packet022JoinGame extends Packet {
    public int gameCount;
    public int mapIndex;
    public Map<Integer,Double[]> bots;
}
